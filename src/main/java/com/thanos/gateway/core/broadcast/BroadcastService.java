package com.thanos.gateway.core.broadcast;


import com.google.gson.Gson;
import com.thanos.api.proto.broadcast.*;
import com.thanos.gateway.core.node.Node;
import com.thanos.gateway.core.node.NodeManager;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 类 RPCAddServiceImpl.java的实现描述：
 *
 * @author dumaobing  on 2020/7/20 3:23 PM
 */
public class BroadcastService extends BroadcastServiceGrpc.BroadcastServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger("broadcast");

    private final static Gson GSON = new Gson();

    private volatile ConnectionManager connectionManager;

    private volatile NodeManager nodeManager;

    public BroadcastService(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public BroadcastService(ConnectionManager connectionManager, NodeManager nodeManager) {
        super();
        this.connectionManager = connectionManager;
        this.nodeManager = nodeManager;
    }

    @Override
    public void transportEthTransactions(EthTransactionsBroadcastDTO request, StreamObserver<DefaultResponse> responseObserver) {
        long s1 = System.currentTimeMillis();
        connectionManager.recvEthTxMessage(request);
        DefaultResponse resp = DefaultResponse.newBuilder().setResult(true).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        long s2 = System.currentTimeMillis();
        log.info("BroadcastService recvEthTxMessage, size:{}, deal time:{}", request.getTxsList().size(), (s2 - s1));
    }

    @Override
    public void transportGlobalNodeEvents(GlobalNodeEventsBroadcastDTO request, StreamObserver<DefaultResponse> responseObserver) {
        long s1 = System.currentTimeMillis();
        connectionManager.recvGlobalNodeEventsMessage(request);
        DefaultResponse resp = DefaultResponse.newBuilder().setResult(true).build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
        long s2 = System.currentTimeMillis();
        log.info("BroadcastService transportGlobalNodeEvent, size:{}, deal time:{}", request.getGlobalNodeEventsList().size(), (s2 - s1));
    }

    @Override
    public void findNodes(NodesBroadcastDTO request, StreamObserver<NodesBroadcastDTO> responseObserver) {
        log.info("BroadcastService findNodes request from {}", GSON.toJson(request.getNodesList()));
        List<Node> nodes = nodeManager.getCluster();
        NodesBroadcastDTO.Builder builder = NodesBroadcastDTO.newBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            Node n = nodes.get(i);
            NodesBroadcastDTO.NodeBroadcastDTO nv = NodesBroadcastDTO.NodeBroadcastDTO.newBuilder()
                    .setId(n.getId())
                    .setIp(n.getIp())
                    .setPort(n.getPort())
                    .build();
            builder.addNodes(i, nv);
        }
        NodesBroadcastDTO nodeList = builder.build();
        responseObserver.onNext(nodeList);
        responseObserver.onCompleted();
        List<Node> newNodes = nodeManager.addNode(request.getNodesList());
        connectionManager.connectNodes(newNodes);
    }

}
