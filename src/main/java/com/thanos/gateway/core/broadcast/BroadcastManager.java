package com.thanos.gateway.core.broadcast;

import com.google.protobuf.ByteString;
import com.thanos.api.proto.broadcast.EthTransactionsBroadcastDTO;
import com.thanos.api.proto.broadcast.GlobalNodeEventsBroadcastDTO;
import com.thanos.common.utils.ThanosWorker;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.node.Node;
import com.thanos.gateway.core.node.NodeManager;
import com.thanos.gateway.core.model.GlobalNodeEvent;
import com.thanos.gateway.core.txpool.TransactionPool;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * BroadcastManager.java description：
 *
 * @Author dumaobing create on 2020-07-22 15:00:34
 */
public class BroadcastManager {

    private static final Logger log = LoggerFactory.getLogger("broadcast");

    //交易来自何处
    public final static Integer FROM_CLIENT = 0; //来自客户端
    public final static Integer FROM_PARENT = 1; //来自父节点
    public final static Integer FROM_RANDOM = 2; //来自随机节点

    private TransactionPool transactionPool;

    private ConnectionManager connectionManager;

    private NodeManager nodeManager;

    private boolean onlyBroadcastGlobalNodeEvent = false;

    public BroadcastManager(TransactionPool transactionPool, ConnectionManager connectionManager, NodeManager nodeManager, boolean isOnlyBroadGlobalEvent) {
        this.transactionPool = transactionPool;
        this.connectionManager = connectionManager;
        this.nodeManager = nodeManager;
        onlyBroadcastGlobalNodeEvent = isOnlyBroadGlobalEvent;
    }

    public void start() {
        new ThanosWorker("broadcast_globalNodeEvent_manager_thread") {
            @Override
            protected void doWork() throws Exception {
                //广播全局事件消息
                List<GlobalNodeEvent> globalNodeEvents = transactionPool.getGlobalNodeEventListByType( false);
                if (!CollectionUtils.isEmpty(globalNodeEvents)) {
                    broadcastGlobalNodeEventsFromClient(globalNodeEvents);
                }
            }
        }.start();

        new ThanosWorker("broadcast_ethTx_manager_thread") {
            @Override
            protected void doWork() throws Exception {
                //广播以太交易消息
                List<EthTransaction> ethTransactions = transactionPool.getEthTransactionListByType(2, false);
                if (CollectionUtils.isNotEmpty(ethTransactions)) {
                    broadcastEthTxsFromClient(ethTransactions);
                }
            }
        }.start();
    }

    private void broadcastEthTxsFromClient(List<EthTransaction> ethTransactions) {
        long s1 = System.currentTimeMillis();
        if(CollectionUtils.isEmpty(ethTransactions)){
            return;
        }
        List<EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO> toRoot = new ArrayList<>(ethTransactions.size());
        List<EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO> toRandom = new ArrayList<>(ethTransactions.size());
        List<Node> randomNodes = nodeManager.getRandoms();

        if (onlyBroadcastGlobalNodeEvent) {
            return;
        }
        for (EthTransaction t : ethTransactions) {
//            if (onlyBroadcastGlobalNodeEvent && !Numeric.toHexString(t.getData()).startsWith(nodeManagerSignMethod)) {
//                //如果开启了仅广播节点变更事件，则不广播普通交易
//                continue;
//            }

            //广播到子节点
            EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO fp = EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO.newBuilder()
                    .setRlpEncoded(ByteString.copyFrom(t.getEncoded()))
                    .setFromType(FROM_PARENT)
                    .build();
            toRoot.add(fp);

            //广播到随机节点
            if (randomNodes.size() > 0) {
                EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO fq = EthTransactionsBroadcastDTO.EthTransactionBroadcastDTO.newBuilder()
                        .setRlpEncoded(ByteString.copyFrom(t.getEncoded()))
                        .setFromType(FROM_RANDOM)
                        .build();
                toRandom.add(fq);
            }
        }

        long s2 = System.currentTimeMillis();

        Node root = nodeManager.getRoot();
        Node sf = nodeManager.getMySelf();
        if (root.getId().equals(sf.getId())) {
            List<Node> subNodes = nodeManager.getSubNode(sf.getId());
            if (CollectionUtils.isNotEmpty(subNodes)) {
                connectionManager.broadcastEthTxListToNodeList(subNodes, toRoot);
            }
        } else {
            connectionManager.broadcastEthTxListToNode(nodeManager.getRoot(), toRoot);
        }
        //广播到随机节点
        if (randomNodes.size() > 0) {
            connectionManager.broadcastEthTxListToNodeList(randomNodes, toRandom);
        }
        long s3 = System.currentTimeMillis();

        log.debug("BroadcastManager broadcastEthTxsFromClient {}- time: {} - {}", toRoot.size(), (s2 - s1), (s3 - s2));
    }

    private void broadcastGlobalNodeEventsFromClient(List<GlobalNodeEvent> globalNodeEvents) {
        long s1 = System.currentTimeMillis();
        if(CollectionUtils.isEmpty(globalNodeEvents)){
            return;
        }

        List<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> toRoot = new ArrayList<>(globalNodeEvents.size());
        List<GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO> toRandom = new ArrayList<>(globalNodeEvents.size());
        List<Node> randomNodes = nodeManager.getRandoms();

        for (GlobalNodeEvent t : globalNodeEvents) {
            //广播到子节点
            GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO rootGlobalNodeEvent = GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO.newBuilder()
                    .setRlpEncoded(ByteString.copyFrom(t.getEncoded()))
                    .setFromType(FROM_PARENT)
                    .build();
            toRoot.add(rootGlobalNodeEvent);

            //广播到随机节点
            if (randomNodes.size() > 0) {

                GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO randomGlobalNodeEvent = GlobalNodeEventsBroadcastDTO.GlobalNodeEventBroadcastDTO.newBuilder()
                        .setRlpEncoded(ByteString.copyFrom(t.getEncoded()))
                        .setFromType(FROM_RANDOM)
                        .build();
                toRandom.add(randomGlobalNodeEvent);
            }
        }

        long s2 = System.currentTimeMillis();

        Node root = nodeManager.getRoot();
        Node sf = nodeManager.getMySelf();
        if (root.getId().equals(sf.getId())) {
            List<Node> subNodes = nodeManager.getSubNode(sf.getId());
            if (CollectionUtils.isNotEmpty(subNodes)) {
                connectionManager.broadcastGlobalNodeEventsToNodeList(subNodes, toRoot);
            }
        } else {
            connectionManager.broadcastGlobalNodeEventsToNode(nodeManager.getRoot(), toRoot);
        }
        //广播到随机节点
        if (randomNodes.size() > 0) {
            connectionManager.broadcastGlobalNodeEventsToNodeList(randomNodes, toRandom);
        }
        long s3 = System.currentTimeMillis();

        log.debug("BroadcastManager broadcastGlobalNodeEventsFromClient {}- time: {} - {}", toRoot.size(), (s2 - s1), (s3 - s2));
    }
}
