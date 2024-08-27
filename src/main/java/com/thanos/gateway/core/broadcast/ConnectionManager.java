package com.thanos.gateway.core.broadcast;

import com.thanos.api.proto.broadcast.*;
import com.thanos.common.utils.ThanosWorker;
import com.thanos.gateway.core.Peer;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.node.Node;
import com.thanos.gateway.core.node.NodeManager;
import com.thanos.gateway.core.model.GlobalNodeEvent;
import com.thanos.gateway.core.txpool.TransactionPool;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.buffer.PooledByteBufAllocator;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.thanos.gateway.core.broadcast.BroadcastManager.FROM_PARENT;

public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger("broadcast");

    /*
     * Peer
     */
    final Peer self;

    /*
     * 节点管理
     */
    private NodeManager nodeManager;

    /*
     * 交易池管理
     */
    private TransactionPool transactionPool;


    /*
     * Peer sid => sender Thread
     */
    private final ConcurrentHashMap<String, SendWorker> senderWorkerMap;

    /*
     *  Peer sid => receiver Thread
     */
    private final ConcurrentHashMap<String, LinkedBlockingQueue<EthTransactionsBroadcastDTO>> ethTxListSendQueueMap;


    private final ConcurrentHashMap<String, LinkedBlockingQueue<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO>> gneSendQueueMap;

    /*
     * Listener thread
     */
    Server server;

    /*
     * Counter to count worker threads
     */
    private AtomicInteger threadCnt = new AtomicInteger(0);

    /*
     * Shutdown flag
     */
    public volatile boolean initSuccess = false;

    public volatile boolean shutdown = false;

    private static final int PROCESS_TIME_OUT = 1000 * 60 * 5;


    /*
     * 定时执行连接检查
     */
    private ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);

    public ConnectionManager(Peer peer, NodeManager nodeManager, TransactionPool transactionPool) {
        this.ethTxListSendQueueMap = new ConcurrentHashMap<>();
        this.gneSendQueueMap = new ConcurrentHashMap<>();
        this.senderWorkerMap = new ConcurrentHashMap<>();
        this.self = peer;
        this.nodeManager = nodeManager;
        this.transactionPool = transactionPool;
    }

    public void start() {
        new Thread(() -> {
            try {
                buildListen();
                connectAll();
                initSuccess = true;
            } catch (Exception e) {
                logger.error("NodeCnxManager connect all node error.", e);
            }
        }).start();

        //启动定时任务，定时管理连接
        executor.scheduleAtFixedRate(() -> {
            try {
                if (initSuccess) {
                    checkConnect();
                }
            } catch (Exception e) {
                logger.warn("NodeCnxManager checkShutdown error.", e);
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    private void buildListen() throws IOException {
        server = NettyServerBuilder.forPort(nodeManager.getMySelf().getPort())
                .maxInboundMessageSize(1024 * 1024 * 1024)
                .withOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .addService(new BroadcastService(this, nodeManager))
                .build().start();

        logger.info("Listener Grpc server start... port:{}", nodeManager.getMySelf().getPort());
    }

    private void connectAll() {
        List<Node> nodes = nodeManager.getCluster();
        Node myself = nodeManager.getMySelf();
        for (Node otherNode : nodes) {
            if (otherNode.getId().equals(myself.getId())) {
                continue;
            }
            connectOne(otherNode);
        }
    }

    //循环查找节点，直到所有节点都连接上
    public void checkConnect() {
        //检查未建立连接的节点，执行节点连接
        ConcurrentHashMap<String, SendWorker> senderWorkerMapSnapshot = this.senderWorkerMap;

        for (SendWorker sendWorker: senderWorkerMapSnapshot.values()) {
            sendWorker.checkShutdown();
            //send rpc and do connect if channel is IDLE
            sendWorker.findCluster();
        }

    }

    public void connectNodes(List<Node> newNodes) {
        for (Node node: newNodes) {
            connectOne(node);
        }
    }

    public void connectOne(Node node) {
        synchronized (this) {
            if (senderWorkerMap.get(node.getId()) != null) {
                return;
            }
            SendWorker worker = new SendWorker(node.getId(), node.getIp(), node.getPort());
            senderWorkerMap.put(node.getId(), worker);
            ethTxListSendQueueMap.put(node.getId(), new LinkedBlockingQueue<>());
            gneSendQueueMap.put(node.getId(), new LinkedBlockingQueue<>());
            worker.start();
        }
    }



    public void broadcastEthTxListToNodeList(List<Node> nodes, List<EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO> txList) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (Node n : nodes) {
            broadcastEthTxListToNode(n, txList);
        }
    }

    public void broadcastEthTxListToNode(Node node, List<EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO> txList) {
        if (CollectionUtils.isEmpty(txList)) {
            return;
        }
        if (node.getId().equals(nodeManager.getMySelf().getId())) {
            return;
        }
        LinkedBlockingQueue<EthTransactionsBroadcastDTO> deque = ethTxListSendQueueMap.get(node.getId());
        if (deque == null) {
            return;
        }
        EthTransactionsBroadcastDTO list = EthTransactionsBroadcastDTO.newBuilder().addAllTxs(txList).build();
        deque.add(list);
    }


    public void broadcastGlobalNodeEventsToNodeList(List<Node> nodes, List<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> txList) {
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (Node n : nodes) {
            broadcastGlobalNodeEventsToNode(n, txList);
        }
    }

    public void broadcastGlobalNodeEventsToNode(Node node, List<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> txList) {
        if (CollectionUtils.isEmpty(txList)) {
            return;
        }
        if (node.getId().equals(nodeManager.getMySelf().getId())) {
            return;
        }
        LinkedBlockingQueue<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> deque = gneSendQueueMap.get(node.getId());
        if (deque == null) {
            return;
        }

        //EthTransactionList list = EthTransactionList.newBuilder().addAllTxs(txList).build();
        deque.addAll(txList);
    }


    class SendWorker {

        private final String id;

        private final String ip;

        private final int port;

        private volatile boolean shutdown;

        private ManagedChannel managedChannel;

        private BroadcastServiceGrpc.BroadcastServiceBlockingStub broadcastService;

        SendWorker(String id, String ip, int port) {
            this.id = id;
            this.ip = new String(ip.getBytes());
            this.port = port;
            try {
                establish(ip, port);
                logger.info("establish SendWorker success [{}:{}]!", ip, port);
            } catch (Exception e) {
                logger.warn("SendWorker init [{}:{}] error! {}", ip, port, e.getMessage());
            }
        }

        public void start() {
            new ThanosWorker("SendWorker_" + id + "_thread") {

                @Override
                protected void doWork() throws Exception {

                    try {
                        BlockingQueue<EthTransactionsBroadcastDTO> ethTxListQueue = ethTxListSendQueueMap.get(id);
                        EthTransactionsBroadcastDTO ethTransactionList = ethTxListQueue.poll(100, TimeUnit.MILLISECONDS);
                        if (ethTransactionList != null ) {
                            broadcastService.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).transportEthTransactions(ethTransactionList);
                            if (logger.isDebugEnabled()) {
                                logger.debug("SendWorker send EthTxList Message  number size:{}", ethTransactionList.getTxsList().size());
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("SendWorker send EthTx Message error, id:{}, ip:{}, port:{}, exception:{}", id, ip, port, ExceptionUtils.getStackTrace(e));

                    }

                    try {
                        BlockingQueue<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> globalNodeEventQueue = gneSendQueueMap.get(id);
                        List<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> globalNodeEventBroadcastDTOs = new ArrayList<>();
                        globalNodeEventQueue.drainTo(globalNodeEventBroadcastDTOs);

                        if (!CollectionUtils.isEmpty(globalNodeEventBroadcastDTOs)) {
                            GlobalNodeEventsBroadcastDTO globalNodeEventsBroadcastDTO = GlobalNodeEventsBroadcastDTO.newBuilder().addAllGlobalNodeEvents(globalNodeEventBroadcastDTOs).build();
                            broadcastService.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).transportGlobalNodeEvents(globalNodeEventsBroadcastDTO);
                            if (logger.isDebugEnabled()) {
                                logger.debug("SendWorker send globalNodeEventsBroadcastDTO Message: {}-{}", globalNodeEventsBroadcastDTO.toString());
                            }
                        }

                    } catch (Throwable e) {
                        logger.warn("SendWorker send GlobalNodeEvent Message error, id:{}, ip:{}, port:{}, exception:{}", id, ip, port, ExceptionUtils.getStackTrace(e));

                    }

                }
            } .start();
        }

        public void checkShutdown() {
            try {
                if (managedChannel.isShutdown()) {
                    logger.warn("PushClient current connectivityState is shutdown, need re connect!");
                    establish(ip, port);
                }
            } catch (Throwable t) {
                logger.warn("check establish SendWorker[{}:{}] error! {}", ip, port, ExceptionUtils.getStackTrace(t));
            }
        }

        private void establish(String ip, int port) {
            managedChannel = NettyChannelBuilder
                    .forAddress(ip, port)
                    .withOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .withOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .usePlaintext()
                    .build();
            broadcastService = BroadcastServiceGrpc.newBlockingStub(managedChannel);
        }

        public void findCluster() {
            try {
                Node n = nodeManager.getMySelf();
                NodesBroadcastDTO.NodeBroadcastDTO nv = NodesBroadcastDTO.NodeBroadcastDTO.newBuilder().setId(new String(n.getId().getBytes())).setIp(new String(n.getIp().getBytes())).setPort(n.getPort()).build();
                NodesBroadcastDTO list = NodesBroadcastDTO.newBuilder().addNodes(nv).build();
                NodesBroadcastDTO nodeList = broadcastService.findNodes(list);
                List<NodesBroadcastDTO.NodeBroadcastDTO> nodes = nodeList.getNodesList();
                List<Node> newNodes = nodeManager.addNode(nodes);
                connectNodes(newNodes);
            } catch (Exception e) {
                logger.error("SendWorker findCluster node error, id:{}, ip:{}, port:{}", id, ip, port, e);

            }
        }

        public void shutdown() {
            this.shutdown = true;
            senderWorkerMap.remove(id);
            threadCnt.decrementAndGet();
            if (managedChannel != null) {
                managedChannel.shutdownNow();
            }
        }
    }

    public void recvEthTxMessage(EthTransactionsBroadcastDTO request) {
        EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO tx = request.getTxsList().get(0);
        if (tx.getFromType() == FROM_PARENT) { // 广播给子节点
            Node n = nodeManager.getMySelf();
            List<Node> subNodes = nodeManager.getSubNode(n.getId());
            if (CollectionUtils.isNotEmpty(subNodes)) {
                for (Node nd : subNodes) {
                    LinkedBlockingQueue<EthTransactionsBroadcastDTO> deque = ethTxListSendQueueMap.get(nd.getId());
                    deque.add(request);
                }
            }
        }


        List<EthTransaction> result = new ArrayList<>(request.getTxsList().size());
        for (EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO tv : request.getTxsList()) {
            EthTransaction ethTransaction = new EthTransaction(tv.getRlpEncoded().toByteArray());
            result.add(ethTransaction);
        }
        transactionPool.addEthTransactionFromBroadCast(result);
    }

    public void recvGlobalNodeEventsMessage(GlobalNodeEventsBroadcastDTO request) {

        List<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> globalNodeEventBroadcastDTOS = request.getGlobalNodeEventsList();

        for (GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO globalNodeEventBroadcastDTO: globalNodeEventBroadcastDTOS) {
            if (globalNodeEventBroadcastDTO.getFromType() == FROM_PARENT) {
                Node n = nodeManager.getMySelf();
                List<Node> subNodes = nodeManager.getSubNode(n.getId());
                if (CollectionUtils.isNotEmpty(subNodes)) {
                    for (Node nd : subNodes) {
                        LinkedBlockingQueue<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> deque = gneSendQueueMap.get(nd.getId());
                        deque.add(globalNodeEventBroadcastDTO);
                    }
                }
            }
        }


        List<GlobalNodeEvent> globalNodeEvents = new ArrayList<>(globalNodeEventBroadcastDTOS.size());
        for (GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO globalNodeEventBroadcastDTO: globalNodeEventBroadcastDTOS) {
            globalNodeEvents.add(new GlobalNodeEvent(globalNodeEventBroadcastDTO.getRlpEncoded().toByteArray()));
        }

        transactionPool.addGlobalNodeEventsFromBroadCast(globalNodeEvents);
    }
}
