package com.thanos.gateway.core.sync;

import com.thanos.gateway.core.common.SystemConfig;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SyncBlock {

    private static final Logger log = LoggerFactory.getLogger("sync");

    private int port;

    private int blockCacheLimit;

    private ConcurrentHashMap<Long, BlockCache> blockCache;
    private volatile LinkedList<BlockCache> blockCacheLinkedList;

    private ConcurrentHashMap<String, byte[]> txReceiptCache;

    private ConcurrentHashMap<String, byte[]> globalNodeEventCache;

    public SyncBlock(SystemConfig systemConfig) {
        this.blockCacheLimit = systemConfig.blockCacheLimit();
        this.port = systemConfig.getSyncAddress();
        initCache(systemConfig);
    }

    private void initCache(SystemConfig systemConfig) {

        blockCache = new ConcurrentHashMap<>(blockCacheLimit);
        blockCacheLinkedList = new LinkedList();
        this.txReceiptCache = new ConcurrentHashMap<>();
        this.globalNodeEventCache = new ConcurrentHashMap<>();
    }

    public void start() {
        try {
            io.grpc.Server server = NettyServerBuilder
                    .forPort(port)
                    .maxInboundMessageSize(1024 * 1024 * 1024)
                    .withOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .addService(new SyncService(this))
                    .build().start();

            log.info("Listener Grpc server start... port:{}", port);
            //等待客户端的连接
            //server.awaitTermination();
        } catch (Exception e) {
            log.info("Listener Grpc server start error. port:{}" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void cacheBlockInfo(BlockCache blockInfo) {

        if (blockCacheLimit < blockCacheLinkedList.size()) {
            BlockCache removeBlockInfo = blockCacheLinkedList.pollFirst();
            removeCache(removeBlockInfo);
        }

        addCache(blockInfo);
    }

    private void addCache(BlockCache blockInfo) {

        blockCacheLinkedList.add(blockInfo);
        blockCache.put(blockInfo.getNumber(), blockInfo);
        if (log.isDebugEnabled()) {
            for (String receiptHash : blockInfo.getReceiptsTable().keySet()) {
                log.debug("addCache block[{}] has tnxHash:{}.", blockInfo.getNumber(), receiptHash);
            }
        }

        //缓存交易回执
        if (blockInfo.getReceiptsTable() != null && blockInfo.getReceiptsTable().size() != 0) {
            txReceiptCache.putAll(blockInfo.getReceiptsTable());
        }
        //缓存全局事件
        if (blockInfo.getGlobalEventsTable() != null && blockInfo.getGlobalEventsTable().size() != 0) {
            for (String eventHash : blockInfo.getGlobalEventsTable().keySet()) {
                log.debug("block[{}] has eventHash:{}.", blockInfo.getNumber(), eventHash);
            }
            globalNodeEventCache.putAll(blockInfo.getGlobalEventsTable());
        }
    }

    private void removeCache(BlockCache blockInfo) {
        //删除交易回执
        if (blockInfo.getReceiptsTable() != null && blockInfo.getReceiptsTable().size() != 0) {
            for (String txHash : blockInfo.getReceiptsTable().keySet()) {
                txReceiptCache.remove(txHash);
            }
        }
        //删除全局事件
        if (blockInfo.getGlobalEventsTable() != null && blockInfo.getGlobalEventsTable().size() != 0) {
            for (String txHash : blockInfo.getGlobalEventsTable().keySet()) {
                globalNodeEventCache.remove(txHash);
            }
        }
        //删除区块
        blockCache.remove(blockInfo.getNumber());
    }

    public BlockCache getBlockCache(Long blockNumber) {
        return blockCache.get(blockNumber);
    }

    public String getTransactionReceipt(String toHexString) {
        byte[] tr = txReceiptCache.get(toHexString);
        return tr != null ? Hex.toHexString(tr) : StringUtils.EMPTY;
    }

    public String getGlobalNodeEventByCache(String toHexString) {
        byte[] tr = globalNodeEventCache.get(toHexString);
        return tr != null ? Hex.toHexString(tr) : StringUtils.EMPTY;
    }
}
