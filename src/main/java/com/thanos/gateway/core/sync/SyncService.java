package com.thanos.gateway.core.sync;

import com.thanos.api.proto.sync.BlockBytesObject;
import com.thanos.api.proto.sync.DefaultResponse;
import com.thanos.api.proto.sync.SyncServiceGrpc;
import com.thanos.common.utils.ThanosWorker;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

public class SyncService extends SyncServiceGrpc.SyncServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger("sync");

    private SyncBlock syncBlock;

    private ArrayBlockingQueue<BlockCache> blockInfoQueue = new ArrayBlockingQueue(30);

    public SyncService(SyncBlock syncBlock) {

        this.syncBlock = syncBlock;

        new ThanosWorker("sync_block_thread") {
            @Override
            protected void doWork() throws Exception {
                BlockCache blockInfo = blockInfoQueue.take();
                syncBlock.cacheBlockInfo(blockInfo);
            }
        }.start();
    }

    @Override
    public void syncBlock(BlockBytesObject request, StreamObserver<DefaultResponse> responseObserver) {
        try {
            BlockCache blockInfo = new BlockCache(request.getBlockBaseInfo().toByteArray());
            if (log.isDebugEnabled()) {
                log.debug("syncBlock receive blockInfo, number:[{}], table[]", blockInfo.getNumber(), blockInfo.getGlobalEventsTable());
            }
            blockInfoQueue.put(blockInfo);
            //syncBlock.cacheBlockInfo(blockInfo);
        }catch (Exception e){
            log.error("SyncService syncBlock error.", e);
        }
        responseObserver.onNext(DefaultResponse.newBuilder().setResult(true).build());
        responseObserver.onCompleted();
    }
}
