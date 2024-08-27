package com.thanos.gateway.core.util;


import com.thanos.gateway.core.model.BaseTransaction;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * TxQueue.java description：
 *
 * @Author laiyiyu create on 2021-02-21 19:00:34
 */
public class TxQueue<T extends BaseTransaction> {

    private static final Logger logger = LoggerFactory.getLogger("txpool");

    public static final int QUEUES_SIZE = 10;

    ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    List<ArrayBlockingQueue<T>> queues;

    ArrayBlockingQueue<T> batchTxs;

    public TxQueue(int size) {
        int perSize = size / QUEUES_SIZE;
        this.queues = new ArrayList(QUEUES_SIZE);
        for (int i = 0; i < QUEUES_SIZE; i++) {
            queues.add(new ArrayBlockingQueue<>(perSize));
        }
        batchTxs = new ArrayBlockingQueue(size);
    }

    public void offer(T tx, long timeout, TimeUnit unit) {
        try {
            //readWriteLock.readLock().lock();
            //queue.put(ethTransaction);
            int offerIndex = Math.abs(tx.hashCode()) % QUEUES_SIZE;
            queues.get(offerIndex).offer(tx, timeout, unit);
        } catch (Exception e) {
            logger.error("offer error {}", ExceptionUtils.getStackTrace(e));
        } finally {
            //readWriteLock.readLock().unlock();
        }
    }

    public void offerBatch(T tx, long timeout, TimeUnit unit) {
        try {
            this.batchTxs.offer(tx, timeout, unit);
        } catch (Exception e) {
            logger.error("offerBatch error {}", ExceptionUtils.getStackTrace(e));
        }
    }

    public void drainTo(Collection<T> c) {
        try {
            //readWriteLock.writeLock().lock();
            batchTxs.drainTo(c);

            for (ArrayBlockingQueue<T> baseTransactions: queues) {
                baseTransactions.drainTo(c);
            }

        } catch (Exception e) {
            logger.error("drainTo error {}", ExceptionUtils.getStackTrace(e));
        } finally {
            //readWriteLock.writeLock().unlock();
        }
    }

    public int size() {
        int size = 0;
        for (ArrayBlockingQueue<T> baseTransactions: queues) {
             size += baseTransactions.size();
        }
        size += batchTxs.size();
        return size;
    }
}
