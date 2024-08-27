package com.thanos.gateway.core.txpool;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.thanos.common.utils.ByteArrayWrapper;
import com.thanos.common.utils.ByteUtil;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.model.GlobalNodeEvent;
import com.thanos.gateway.core.util.TxQueue;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;


/**
 * TransactionPool.java description：
 *
 * @Author laiyiyu create on 2021-02-22 14:00:34
 */
public class TransactionPool {

    private static final Logger logger = LoggerFactory.getLogger("txpool");

    private static final int OFFER_TIME_OUT = 5000;

    //收到消息存储1个小时，一个小时内收到重复消息不处理
    //private Cache<String, Long> ethCache;
    //private static final EvictionListener<String, String> CONTRACT_STATE_REMOVE_LISTENER = (key, value) -> {};

    ConcurrentLinkedHashMap<ByteArrayWrapper, ByteArrayWrapper> ethCache;

    ConcurrentLinkedHashMap<ByteArrayWrapper, ByteArrayWrapper> globalEventCache;

    //以太坊交易入队顺序
    private TxQueue<EthTransaction> ethTxsPackageQueue;
    //以太坊待广播交易队列
    private TxQueue<EthTransaction> ethTxsBroadcastQueue;

    //全局事件入队顺序
    private ArrayBlockingQueue<GlobalNodeEvent> gnePackageQueue;
    //全局事件待广播交易队列
    private ArrayBlockingQueue<GlobalNodeEvent> gneBroadcastQueue;

    public TransactionPool(int txPoolDSCacheSizeLimit) {
        ethCache = new ConcurrentLinkedHashMap.Builder<ByteArrayWrapper, ByteArrayWrapper>()
                .maximumWeightedCapacity(txPoolDSCacheSizeLimit).build();

        globalEventCache = new ConcurrentLinkedHashMap.Builder<ByteArrayWrapper, ByteArrayWrapper>()
                .maximumWeightedCapacity(1024).build();

        ethTxsPackageQueue = new TxQueue(txPoolDSCacheSizeLimit);
        ethTxsBroadcastQueue = new TxQueue(txPoolDSCacheSizeLimit);

        gnePackageQueue = new ArrayBlockingQueue<>(1024, true);
        gneBroadcastQueue = new ArrayBlockingQueue<>(1024, true);

    }

    public boolean addEthTransactionFromClient(EthTransaction tx) {
        //防止已经写入的消息并发写入和排重
        if (isExistEthTransaction(tx)) {
            return true;
        }

        tx.verify();
        if (!tx.isValid()) {
            return false;
        }

        try {
            ethTxsPackageQueue.offer(tx, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
            ethTxsBroadcastQueue.offer(tx, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.warn("addEthTransactionFromClient{}, error:{}", tx, ExceptionUtils.getStackTrace(e));
        }
        //客户端的交易需要插入到广播队列中

        return true;
    }

    //批量eth交易不广播
    public void addEthTransactionList(List<EthTransaction> ethTransactions) {
        for (EthTransaction tx : ethTransactions) {

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("addEthTransactionList receive tx:{}", Hex.toHexString(tx.getHash()));
                }
                ethTxsPackageQueue.offerBatch(tx, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.warn("addEthTransactionList{}, error:{}", tx, ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public void addEthTransactionArray(EthTransaction[] ethTransactions) {
        for (EthTransaction tx : ethTransactions) {

            if (tx == null) continue;

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("addEthTransactionArray receive tx:{}", Hex.toHexString(tx.getHash()));
                }
                ethTxsPackageQueue.offerBatch(tx, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.warn("addEthTransactionArray{}, error:{}", tx, ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public boolean addEthTransactionFromBroadCast(List<EthTransaction> txList) {
        for (EthTransaction tx : txList) {
            if (isExistEthTransaction(tx)) {
                continue;
            }

            tx.verify();
            if (!tx.isValid()) {
                continue;
            }

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("addEthTransactionFromBroadCast receive tx:{}", Hex.toHexString(tx.getHash()));
                }
                ethTxsPackageQueue.offer(tx, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                logger.warn("addEthTransactionFromBroadCast{}, error:{}", tx, ExceptionUtils.getStackTrace(e));
            }
        }
        return true;
    }

    private boolean isExistEthTransaction(EthTransaction tx) {
        //String txHash = TypeConverter.toJsonHex(tx.getHash());
        ByteArrayWrapper txHash = new ByteArrayWrapper(ByteUtil.copyFrom(tx.getHash()));
        ByteArrayWrapper oldTxHash = ethCache.put(txHash, txHash);
        if (oldTxHash == null) {
            return false;
        } else {
            return true;
        }
    }

    //获取交易
    public List<EthTransaction> getEthTransactionListByType(int retryDrainTime, boolean fromPush) {
        TxQueue txsQueue = fromPush ? ethTxsPackageQueue : ethTxsBroadcastQueue;
        List<EthTransaction> list = new ArrayList(txsQueue.size());

        for (int i = 0; i < retryDrainTime; i++) {
            txsQueue.drainTo(list);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
        return list;
    }

    //=========eth tx  end==================

    public boolean addGlobalNodeEventFromClient(GlobalNodeEvent globalNodeEvent) {
        //防止已经写入的消息并发写入和排重
        if (isExistGlobalNodeEvent(globalNodeEvent)) {
            return true;
        }

        globalNodeEvent.verify();
        if (!globalNodeEvent.isValid()) {
            return false;
        }

        try {
            gnePackageQueue.offer(globalNodeEvent, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
            gneBroadcastQueue.offer(globalNodeEvent, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("addGlobalNodeEventFromClient{}, error:{}", globalNodeEvent, ExceptionUtils.getStackTrace(e));
        }
        //客户端的交易需要插入到广播队列中

        return true;
    }

    public boolean addGlobalNodeEventsFromBroadCast(List<GlobalNodeEvent> globalNodeEvents) {
        for (GlobalNodeEvent globalNodeEvent: globalNodeEvents) {
            if (isExistGlobalNodeEvent(globalNodeEvent)) {
                continue;
            }

            globalNodeEvent.verify();
            if (!globalNodeEvent.isValid()) {
                continue;
            }

            try {
                gnePackageQueue.offer(globalNodeEvent, OFFER_TIME_OUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warn("addGlobalNodeEventsFromBroadCast{}, error:{}", globalNodeEvent, ExceptionUtils.getStackTrace(e));
            }
        }
        return true;
    }

    private boolean isExistGlobalNodeEvent(GlobalNodeEvent globalNodeEvent) {
        ByteArrayWrapper txHash = new ByteArrayWrapper(ByteUtil.copyFrom(globalNodeEvent.getHash()));
        ByteArrayWrapper oldTxHash = globalEventCache.put(txHash, txHash);
        if (oldTxHash == null) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<GlobalNodeEvent> getGlobalNodeEventListByType(boolean fromPush) throws InterruptedException {
        ArrayBlockingQueue<GlobalNodeEvent> txsQueue = fromPush ? gnePackageQueue : gneBroadcastQueue;
        ArrayList<GlobalNodeEvent> result = new ArrayList<>(txsQueue.size());

        for (int i = 0; i < 2; i++) {
            try {
                Thread.sleep(50);
                txsQueue.drainTo(result);
            } catch (InterruptedException e) {
            }
        }

        return result;
    }
    //=========global node event  end==================
}
