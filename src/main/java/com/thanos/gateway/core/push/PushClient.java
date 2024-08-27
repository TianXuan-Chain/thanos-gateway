package com.thanos.gateway.core.push;

import com.google.protobuf.ByteString;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.thanos.api.proto.push.*;
import com.thanos.common.utils.ByteArrayWrapper;
import com.thanos.common.utils.ThanosThreadFactory;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.model.GlobalNodeEvent;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.buffer.PooledByteBufAllocator;
import io.grpc.netty.shaded.io.netty.channel.ChannelOption;
import io.grpc.netty.shaded.io.netty.channel.nio.NioEventLoopGroup;
import io.grpc.netty.shaded.io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class PushClient {

    private static final Logger logger = LoggerFactory.getLogger("push");

    private final static int RETRY_TIMES = 3;

    private static final int PROCESS_TIME_OUT = 1000 * 10;

    private static final int CPU_PROCESSOR = Runtime.getRuntime().availableProcessors();


    private volatile ManagedChannel managedChannel;

    private volatile PushServiceGrpc.PushServiceBlockingStub pushServiceBlockingStub;



    public PushClient(String ip, int port) {

        try {
            establish(ip, port);
        } catch (Throwable e) {
            logger.warn("establish push client [{}:{}] error! {}", ip, port, ExceptionUtils.getStackTrace(e));
        }

//        new Thread(() -> {
//            while (true) {
//                try {
//                    Thread.sleep(10000);
//                    if (managedChannel.isShutdown()) {
//                        logger.warn("PushClient current state is shutdown, need re connect!");
//                        establish(ip, port);
//                        continue;
//                    }
//
//                    saveAlive();
//                } catch (Throwable e) {
//                    logger.warn("re establish push client [{}:{}] error! {}", ip, port, ExceptionUtils.getStackTrace(e));
//                }
//
//            }
//        }, "push_client_detected_thread").start();
    }

    private void establish(String ip, int port) {
        managedChannel = NettyChannelBuilder
                .forAddress(ip, port)
                .channelFactory(() -> new NioSocketChannel())
                .eventLoopGroup(new NioEventLoopGroup(CPU_PROCESSOR, new ThanosThreadFactory("push_client_netty_worker")))
                .withOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .withOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .maxInboundMessageSize(1024 * 1024 * 256)
                .usePlaintext()
                .build();
        pushServiceBlockingStub = PushServiceGrpc
                .newBlockingStub(managedChannel)
                .withExecutor(new ThreadPoolExecutor(CPU_PROCESSOR, CPU_PROCESSOR, 1, TimeUnit.SECONDS, new ArrayBlockingQueue(100), new ThanosThreadFactory("push_client_netty_process")));;
    }


    public void pushEthTransactions(List<EthTransaction> ethTransactions) {
        try {
            //do serialize
            long s1 = System.currentTimeMillis();
            EthTransactionsPushDTO.EthTransactionPushDTO[] transactionArray = new EthTransactionsPushDTO.EthTransactionPushDTO[ethTransactions.size()];
            CountDownLatch latch = new CountDownLatch(ethTransactions.size());
            for (int i = 0; i < ethTransactions.size(); i++) {
                publicEvent(transactionArray, ethTransactions.get(i), i, latch);
            }
            latch.await();
            logger.debug("PushClient serialize ==================== time:{}.", System.currentTimeMillis() - s1);

            //do grpc
            long t1 = System.currentTimeMillis();
            List<EthTransactionsPushDTO.EthTransactionPushDTO> transactionList = Arrays.asList(transactionArray);
            EthTransactionsPushDTO transactionsPush = EthTransactionsPushDTO.newBuilder().addAllTxs(transactionList).build();

            for(int i = 0; i < RETRY_TIMES; i++) {
                try {
                    DefaultResponse defaultResponse = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT * 6, TimeUnit.MILLISECONDS).pushEthTransactions(transactionsPush);
                    if (defaultResponse.getResult() == true) {
                        break;
                    }

                    Thread.sleep(5000);
                } catch (Throwable e) {
                    logger.warn("pushServiceBlockingStub.pushEthTransactions warn[{}], warn:[{}]", i, e.getMessage());
                }
            }


            logger.info("PushClient grpc  ====================. number:{} , byte size:{}, time: {}", transactionList.size(), transactionsPush.toByteArray().length, (System.currentTimeMillis() - t1));
        } catch (Exception e) {
            logger.warn("push transaction to server error.", e);
        }
    }

    static void publicEvent(EthTransactionsPushDTO.EthTransactionPushDTO[] fillTransactions, EthTransaction ethTransactionV, int i, CountDownLatch countDownLatch) {
        int cpuIndex = i % PROCESSOR_NUM;
        RingBuffer<DecodeEvent> decodeRingBuffer = riskControlComputeEventRingBuffers.get(cpuIndex);
        long seq = decodeRingBuffer.next();
        try {
            DecodeEvent decodeEvent = decodeRingBuffer.get(seq);
            decodeEvent.fillTransactions = fillTransactions;
            decodeEvent.ethTransactionV = ethTransactionV;
            decodeEvent.i = i;
            decodeEvent.countDownLatch = countDownLatch;
            decodeEvent.futureEventNumber = ethTransactionV.getFutureEventNumber();
        } catch (Exception e) {
            logger.warn("ConsensusPayload publicEvent error!", e);
            throw new RuntimeException("ConsensusPayload publicEvent error!", e);
        } finally {
            decodeRingBuffer.publish(seq);
        }
    }

    static final class DecodeEvent {

        EthTransactionsPushDTO.EthTransactionPushDTO[] fillTransactions;

        EthTransaction ethTransactionV;

        int i;

        CountDownLatch countDownLatch;

        long futureEventNumber;

    }

    static final class DecodeEventConsumer implements EventHandler<DecodeEvent> {

        @Override
        public void onEvent(DecodeEvent decodeEvent, long sequence, boolean endOfBatch) throws Exception {
            EthTransaction t = decodeEvent.ethTransactionV;
            List<ByteString> exeStates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(t.getExecuteStates())) {
                List<ByteArrayWrapper> byteArrayWrappers = new ArrayList<>(t.getExecuteStates());
                for (ByteArrayWrapper wrapper : byteArrayWrappers) {
                    exeStates.add(ByteString.copyFrom(wrapper.getData()));
                }
            }

            EthTransactionsPushDTO.EthTransactionPushDTO inner = EthTransactionsPushDTO.EthTransactionPushDTO.newBuilder()
                    .setHash(ByteString.copyFrom(t.getHash()))
                    .setNonce(ByteString.copyFrom(t.getNonce()))
                    .setValue(ByteString.copyFrom(t.getValue()))
                    .setReceiveAddress(ByteString.copyFrom(t.getReceiveAddress()))
                    .setGasPrice(ByteString.copyFrom(t.getGasPrice()))
                    .setGasLimit(ByteString.copyFrom(t.getGasLimit()))
                    .setData(ByteString.copyFrom(t.getData()))
                    .setSignature(ByteString.copyFrom(t.getSignature()))
                    .setPublicKey(ByteString.copyFrom(t.getPublicKey()))
                    .setRlpEncoded(ByteString.copyFrom(t.getEncoded()))
                    .addAllExecuteStates(exeStates)
                    .setFutureEventNumber(decodeEvent.futureEventNumber)
                    //.setFutureEventNumber(t.getFutureEventNumber())
                    .build();

            decodeEvent.fillTransactions[decodeEvent.i] = inner;
            decodeEvent.countDownLatch.countDown();
            decodeEvent.countDownLatch = null;
            decodeEvent.fillTransactions = null;
            decodeEvent.ethTransactionV = null;
        }
    }

    static final AtomicLong THREAD_COUNTER = new AtomicLong(0);

    static final int PROCESSOR_NUM = 4;

    static List<Disruptor<DecodeEvent>> decodeDisruptors;

    static List<RingBuffer<DecodeEvent>> riskControlComputeEventRingBuffers;


    static ThreadFactory threadFactory;

    static DecodeEventConsumer decodeEventConsumer;

    static {
        threadFactory = r -> new Thread(r, "consensusPayload_decode_thread_" + THREAD_COUNTER.incrementAndGet());
        decodeEventConsumer = new DecodeEventConsumer();
        decodeDisruptors = new ArrayList<>(PROCESSOR_NUM);
        riskControlComputeEventRingBuffers = new ArrayList<>(PROCESSOR_NUM);
        for (int i = 0; i < PROCESSOR_NUM; i++) {
            Disruptor<DecodeEvent> decodeDisruptor = new Disruptor(() -> new DecodeEvent(), 256, threadFactory);
            decodeDisruptors.add(decodeDisruptor);
            decodeDisruptor.handleEventsWith(decodeEventConsumer);
            riskControlComputeEventRingBuffers.add(decodeDisruptor.getRingBuffer());
            decodeDisruptor.start();
        }
    }

    public void pushGlobalNodeEvents(List<GlobalNodeEvent> globalNodeEvents) {
        try {
            //反序列号消息
//

            List<GlobalNodeEventsPushDTO.GlobalNodeEventPushDTO> transactionList = new ArrayList<>(globalNodeEvents.size());

            for (GlobalNodeEvent globalNodeEvent: globalNodeEvents) {

                GlobalNodeEventsPushDTO.GlobalNodeEventPushDTO.Builder builder = GlobalNodeEventsPushDTO.GlobalNodeEventPushDTO.newBuilder();
                builder.setHash(ByteString.copyFrom(globalNodeEvent.getHash()));
                builder.setNonce(ByteString.copyFrom(globalNodeEvent.getNonce()));

                builder.setPublicKey(ByteString.copyFrom(globalNodeEvent.getPublicKey()));
                builder.setNonce(ByteString.copyFrom(globalNodeEvent.getNonce()));
                builder.setFutureEventNumber(globalNodeEvent.getFutureEventNumber());
                builder.setSignature(ByteString.copyFrom(globalNodeEvent.getSignature()));
                builder.setCommandCode(globalNodeEvent.getCommandCode());
                builder.setData(ByteString.copyFrom(globalNodeEvent.getData()));
                GlobalNodeEventsPushDTO.GlobalNodeEventPushDTO gb = builder.build();
                transactionList.add(gb);
            }
            pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT * 6, TimeUnit.MILLISECONDS).pushGlobalNodeEvents(GlobalNodeEventsPushDTO.newBuilder().addAllNodes(transactionList).build());
        } catch (Exception e) {
            logger.error("PushClient pushGlobalNodeEvents error.", e);
        }
    }

    //todo 查询全局事件
    public String getGlobalNodeEvent(byte[] hash){
        BytesObject req = BytesObject.newBuilder().setValue(ByteString.copyFrom(hash)).build();
        BytesObject so = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getGlobalNodeEvent(req);
        if (so != null && so.toByteArray().length > 0) {
            return Hex.toHexString(so.getValue().toByteArray());
        } else {
            return StringUtils.EMPTY;
        }
    }

    //todo 查询全局事件
    public String getGlobalNodeEventReceipt(byte[] hash){
        BytesObject req = BytesObject.newBuilder().setValue(ByteString.copyFrom(hash)).build();
        BytesObject so = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getGlobalNodeEventReceipt(req);
        if (so != null && so.toByteArray().length > 0) {
            return Hex.toHexString(so.getValue().toByteArray());
        } else {
            return StringUtils.EMPTY;
        }
    }

    public void saveAlive() {
        pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).saveAlive(DefaultRequest.newBuilder().setReq("alive").build());
    }

    //todo 查询全局事件
    public String getEpochState(){
        BytesObject so = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getEpochState(DefaultRequest.newBuilder().build());
        if (so != null && so.toByteArray().length > 0) {
            return Hex.toHexString(so.getValue().toByteArray());
        } else {
            return StringUtils.EMPTY;
        }
    }

    public long getLatestBeExecutedNum() {
        //线程级别的方法等待超时，不管任何阻塞情况
        LongObject res = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getLatestBeExecutedNum(DefaultRequest.newBuilder().build());
        return res.getValue();
    }

    public long getLatestConsensusNumber() {
        LongObject res = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getLatestConsensusNumber(DefaultRequest.newBuilder().build());
        return res.getValue();
    }

    public long getCurrentCommitRound() {
        LongObject res = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getCurrentCommitRound(DefaultRequest.newBuilder().build());
        return res.getValue();
    }

    public String getEthTransactionByHash(byte[] hash) {
        BytesObject req = BytesObject.newBuilder().setValue(ByteString.copyFrom(hash)).build();
        BytesObject so = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT * 6, TimeUnit.MILLISECONDS).getEthTransactionByHash(req);
        if (so != null && so.toByteArray().length > 0) {
            return Hex.toHexString(so.getValue().toByteArray());
        } else {
            return StringUtils.EMPTY;
        }
    }

    public List<String> getEthTransactionsByHashes(List<byte[]> hashList) {
        List<ByteString> reqs = new ArrayList<>();
        for (byte[] bt : hashList) {
            reqs.add(ByteString.copyFrom(bt));
        }
        ListBytesObject resp = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getEthTransactionsByHashes(ListBytesObject.newBuilder().addAllValue(reqs).build());
        List<String> receipts = new ArrayList<>();
        for (ByteString bs : resp.getValueList()) {
            receipts.add(Hex.toHexString(bs.toByteArray()));
        }
        return receipts;
    }

    public BlockBytesObject getBlockByNumber(Long number) {
        return pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT, TimeUnit.MILLISECONDS).getBlockByNumber(LongObject.newBuilder().setValue(number).build());
    }

    public String ethCall(EthTransaction t) {
        List<ByteString> exeStates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(t.getExecuteStates())) {
            List<ByteArrayWrapper> byteArrayWrappers = new ArrayList<>(t.getExecuteStates());
            for (ByteArrayWrapper wrapper : byteArrayWrappers) {
                exeStates.add(ByteString.copyFrom(wrapper.getData()));
            }
        }



        EthTransactionsPushDTO.EthTransactionPushDTO req = EthTransactionsPushDTO.EthTransactionPushDTO.newBuilder()
                .setHash(ByteString.copyFrom(t.getHash()))
                .setNonce(ByteString.copyFrom(t.getNonce()))
                .setValue(ByteString.copyFrom(t.getValue()))
                .setReceiveAddress(ByteString.copyFrom(t.getReceiveAddress()))
                .setGasPrice(ByteString.copyFrom(t.getGasPrice()))
                .setGasLimit(ByteString.copyFrom(t.getGasLimit()))
                .setData(ByteString.copyFrom(t.getData()))
                .setSignature(ByteString.copyFrom(t.getSignature()))
                .setPublicKey(ByteString.copyFrom(t.getPublicKey()))
                .setRlpEncoded(ByteString.copyFrom(t.getEncoded()))
                .addAllExecuteStates(exeStates)
                .setFutureEventNumber(t.getFutureEventNumber())
                .build();
        BytesObject resp = pushServiceBlockingStub.withDeadlineAfter(PROCESS_TIME_OUT * 6, TimeUnit.MILLISECONDS).ethCall(req);
        return Hex.toHexString(resp.getValue().toByteArray());
    }

}
