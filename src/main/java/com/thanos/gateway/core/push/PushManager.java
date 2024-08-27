package com.thanos.gateway.core.push;

import com.thanos.api.proto.push.BlockBytesObject;
import com.thanos.common.utils.ThanosWorker;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.sync.BlockCache;
import com.thanos.gateway.core.sync.SyncBlock;
import com.thanos.gateway.core.model.GlobalNodeEvent;
import com.thanos.gateway.core.txpool.TransactionPool;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.spongycastle.util.encoders.Hex;

import java.util.*;

public class PushManager {

    // 交易池
    private TransactionPool transactionPool;

    // 推送客户端
    private PushClient pushClient;

    private SyncBlock syncBlock;


    private volatile long currentLatestConsensusNumber;

    public PushManager(String ip, int port, TransactionPool transactionPool, SyncBlock syncBlock) {
        this.transactionPool = transactionPool;
        this.syncBlock = syncBlock;
        pushClient = new PushClient(ip, port);
    }

    public void start() {
        // do as connect check
        new ThanosWorker("check_latest_consensus_num_thread") {
            @Override
            protected void doWork() throws Exception {
                //try {
                currentLatestConsensusNumber = pushClient.getLatestConsensusNumber();
                Thread.sleep(300);
//                } catch (Throwable e) {
//
//                }


            }
        }.start();

        new ThanosWorker("push_eth_tx_thread") {

            @Override
            protected void doWork() throws Exception {
                //推送以太交易消息
                List<EthTransaction> ethTransactions = transactionPool.getEthTransactionListByType(2, true);
                if (CollectionUtils.isNotEmpty(ethTransactions)) {
                    pushEthTxs(ethTransactions);
                }
            }
        }.start();


        new ThanosWorker("push_global_node_event_thread") {

            @Override
            protected void doWork() throws Exception {
                //推送全局交易消息
                List<GlobalNodeEvent> globalNodeEvents = transactionPool.getGlobalNodeEventListByType(true);
                if (CollectionUtils.isNotEmpty(globalNodeEvents)) {
                    pushGlobalNodeEvents(globalNodeEvents);
                }

            }
        }.start();
    }


    private void pushEthTxs(List<EthTransaction> ethTransactions) {
        pushClient.pushEthTransactions(ethTransactions);
    }

    private void pushGlobalNodeEvents(List<GlobalNodeEvent> globalNodeEvents) {
        pushClient.pushGlobalNodeEvents(globalNodeEvents);
    }

    public String getEpochState() {
        return pushClient.getEpochState();
    }

    public String getBlockByNumber(Long blockNumber) {
        BlockCache cache = syncBlock.getBlockCache(blockNumber);
        if (cache != null) {
            return Hex.toHexString(cache.getBlockCacheEncode());
        }
        BlockBytesObject res = pushClient.getBlockByNumber(blockNumber);
        //BlockCache blockInfo = new BlockCache(res.getBlockCacheEncode());
        //syncBlock.cacheBlockInfo(blockInfo);
        return Hex.toHexString(res.getBlockBaseInfo().toByteArray());
    }

    public String getTransactionByHash(byte[] txHash) {
        return syncBlock.getTransactionReceipt(Hex.toHexString(txHash));
    }

    public String getGlobalNodeEventByCache(byte[] txhash) {
        return syncBlock.getGlobalNodeEventByCache(Hex.toHexString(txhash));
    }


    public String getGlobalNodeEventReceipt(byte[] txhash) {
        return pushClient.getGlobalNodeEventReceipt(txhash);
    }

    public String getTransactionByHashByChain(byte[] txHash) {
        return pushClient.getEthTransactionByHash(txHash);
    }

    public String getGlobalNodeEventByChain(byte[] txHash) {
        return pushClient.getGlobalNodeEvent(txHash);
    }

    //根据交易hash批量查询交易回执
    public List<String> getTransactionsByHashes(List<byte[]> reqs) {
        List<String> result = new ArrayList<>();
//        List<byte[]> reqSd = new ArrayList<>();
        for (byte[] req : reqs) {
            String cache = syncBlock.getTransactionReceipt(Hex.toHexString(req));
            result.add(cache);
        }

        return result;
    }

    public Long getLatestBeExecutedNum() {
        return pushClient.getLatestBeExecutedNum();
    }

    public Long getLatestConsensusNumber() {
        return currentLatestConsensusNumber;
    }

    public Long getCurrentCommitRound() {
        return pushClient.getCurrentCommitRound();
    }

    public String ethCall(EthTransaction tx) {
        return pushClient.ethCall(tx);
    }
}
