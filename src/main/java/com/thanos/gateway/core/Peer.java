package com.thanos.gateway.core;


import com.google.gson.Gson;
import com.thanos.gateway.core.broadcast.BroadcastManager;
import com.thanos.gateway.core.common.SystemConfig;
import com.thanos.gateway.core.broadcast.ConnectionManager;
import com.thanos.gateway.core.node.NodeManager;
import com.thanos.gateway.core.sync.SyncBlock;
import com.thanos.gateway.core.txpool.TransactionPool;
import com.thanos.gateway.core.push.PushManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Peer.java description：
 *
 * @Author laiyiyu create on 2021-03-05 12:56:52
 */
public class Peer {

    private static final Logger log = LoggerFactory.getLogger(Peer.class);

    private final static Gson GSON = new Gson();

    private SystemConfig systemConfig;

    private NodeManager nodeManager;

    private TransactionPool transactionPool;

    private ConnectionManager connectionManager;

    private BroadcastManager broadcastManager;

    private PushManager pushManager;

    public Peer(NodeManager nodeManager, TransactionPool transactionPool, SystemConfig systemConfig) {
        this.nodeManager = nodeManager;
        this.transactionPool = transactionPool;
        this.systemConfig = systemConfig;
    }

    public void init() {
        //连接管理器
        connectionManager = new ConnectionManager(this, nodeManager, transactionPool);
        connectionManager.start();
        //广播管理器
        boolean isOnlyBroadGlobalEvent = systemConfig.getOnlyBroadGlobalEvent();
        broadcastManager = new BroadcastManager(transactionPool, connectionManager, nodeManager, isOnlyBroadGlobalEvent);
        broadcastManager.start();
        // 启动同步block服务
        SyncBlock syncBlock = new SyncBlock(systemConfig);
        syncBlock.start();
        //推送管理器
        pushManager = new PushManager(nodeManager.getPushNode().getIp(), nodeManager.getPushNode().getPort(), transactionPool, syncBlock);
        pushManager.start();
    }

    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    public ConnectionManager getNodeCnxManager() {
        return connectionManager;
    }

    public BroadcastManager getBroadcastManager() {
        return broadcastManager;
    }

    public PushManager getPushManager() {
        return pushManager;
    }

    //============================ 启动服务 ================================

    private final static String NODE_IP_PORT = "nodeipport";

    private final static String CLUSTER_NODES = "clusternodes";

    private final static String PUSH_NODE = "pushnode";

    public static Peer startUp(SystemConfig systemConfig) {
        try {
            String nodeIpPort = systemConfig.nodeIpPort();
            List<String> clusterNodes = systemConfig.getClusterNodes();
            String pushNode = systemConfig.getPushNode();

            NodeManager nodeManager = new NodeManager(nodeIpPort, clusterNodes, pushNode);
            TransactionPool transactionPool = new TransactionPool(systemConfig.txPoolDSCacheSizeLimit());
            Peer peer = new Peer(nodeManager, transactionPool, systemConfig);
            peer.init();
            return peer;
        } catch (Exception e) {
            log.error("Peer startUp node error. ", e);
            throw new RuntimeException("Peer startUp node error.");
        }
    }
}
