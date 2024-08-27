package com.thanos.gateway.jsonrpc.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.ByteString;
import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImpl;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.thanos.api.proto.push.DefaultResponse;
import com.thanos.api.proto.push.EthTransactionsPushDTO;
import com.thanos.common.utils.ByteArrayWrapper;
import com.thanos.common.utils.HashUtil;
import com.thanos.gateway.core.common.ConfigResourceUtil;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.push.PushClient;
import com.thanos.gateway.jsonrpc.GatewayApiService;
import com.thanos.gateway.jsonrpc.TypeConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.thanos.gateway.jsonrpc.TypeConverter.hexToByteArray;

/**
 * JsonRpc4HttpImpl.java description：
 * @Author laiyiyu create on 2021-03-05 15:56:52
 */
@Service
@AutoJsonRpcServiceImpl
public class JsonRpc4HttpImpl implements JsonRpc4Http {

    private static final Logger log = LoggerFactory.getLogger("jsonrpc");

    static final class DecodeEvent {

        EthTransaction[] fillTransactions;

        int start;

        int end;

        List<String> rawDatas;

        ArrayBlockingQueue<List<String>> hashes;

        CountDownLatch countDownLatch;
    }

    static void publicEvent(EthTransaction[] fillEthTransactions, int start, int end, int cpuIndex, List<String> rawDatas, ArrayBlockingQueue<List<String>> hashes, CountDownLatch countDownLatch) {
        RingBuffer<DecodeEvent> decodeRingBuffer = riskControlComputeEventRingBuffers.get(cpuIndex);
        long seq = decodeRingBuffer.next();
        try {
            DecodeEvent decodeEvent = decodeRingBuffer.get(seq);
            decodeEvent.fillTransactions = fillEthTransactions;
            decodeEvent.start = start;
            decodeEvent.end = end;
            decodeEvent.rawDatas = rawDatas;
            decodeEvent.hashes = hashes;
            decodeEvent.countDownLatch = countDownLatch;
        } catch (Exception e) {
            log.warn("JsonRpc4HttpImpl publicEvent error!", e);
            throw new RuntimeException("JsonRpc4HttpImpl publicEvent error!", e);
        } finally {
            decodeRingBuffer.publish(seq);
        }
    }

    static final class DecodeEventConsumer implements EventHandler<DecodeEvent> {

        @Override
        public void onEvent(DecodeEvent decodeEvent, long sequence, boolean endOfBatch) throws Exception {
            try {

                List<String> hashList = new ArrayList<>(decodeEvent.end - decodeEvent.start + 1);
                decodeEvent.hashes.add(hashList);
                for (int i = decodeEvent.start; i <= decodeEvent.end; i++) {
                    try {

                        String rawData = decodeEvent.rawDatas.get(i);

                        if (StringUtils.isBlank(rawData)) {
                            continue;
                        }
                        EthTransaction tx = new EthTransaction(hexToByteArray(rawData));

                        tx.verify();
                        if (!tx.isValid()) {
                            log.warn("tx not valid. tx:{}", tx);
                            continue;
                        }

                        hashList.add(TypeConverter.toJsonHex(tx.getHash()));
                        decodeEvent.fillTransactions[i] = tx;
                    } catch (Exception e) {

                    }
                }

            } catch (Throwable e) {

            } finally {
                doRelease(decodeEvent);
            }


        }

        private void doRelease(DecodeEvent decodeEvent) {
            decodeEvent.countDownLatch.countDown();
            decodeEvent.countDownLatch = null;
            decodeEvent.fillTransactions = null;
            decodeEvent.hashes = null;
            decodeEvent.rawDatas = null;
        }
    }

    private final static Gson GSON;

    private final static Type GSON_TYPE;

    static final AtomicLong THREAD_COUNTER;

    static final int PROCESSOR_NUM;

    static List<Disruptor<DecodeEvent>> decodeDisruptors;

    static List<RingBuffer<DecodeEvent>> riskControlComputeEventRingBuffers;


    static ThreadFactory threadFactory;

    static DecodeEventConsumer decodeEventConsumer;

    static {
        GSON = new Gson();
        GSON_TYPE = new TypeToken<List<String>>() {
        }.getType();
        THREAD_COUNTER = new AtomicLong(0);
        PROCESSOR_NUM = ConfigResourceUtil.loadSystemConfig().decodeProcessNum();
        threadFactory = r -> new Thread(r, "JsonRpc4HttpImpl_decode_thread_" + THREAD_COUNTER.incrementAndGet());
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


    @Autowired
    private GatewayApiService gatewayApiService;


    public String thanos_clientVersion() {
        Pattern shortVersion = Pattern.compile("(\\d\\.\\d).*");
        Matcher matcher = shortVersion.matcher(System.getProperty("java.version"));
        matcher.matches();

        return Arrays.asList("thanos", "v1.0.0", System.getProperty("os.name"), "Java" + matcher.group(1), "1.0.0")
                .stream().collect(Collectors.joining("/"));
    }

    public String thanos_sha3(String data) throws Exception {
        byte[] result = HashUtil.sha3(TypeConverter.hexToByteArray(data));
        return TypeConverter.toJsonHex(result);
    }

    public String thanos_net_version() {
        return "1.0.0";
    }

    public String thanos_protocolVersion() {
        return "1.0.0";
    }

    @Override
    public String[] thanos_getCompilers() {
        return new String[]{"solidity"};
    }

    @Override
    public Long thanos_getLatestBeExecutedNum() throws Exception {
        return gatewayApiService.getLatestBeExecutedNum();
    }

    @Override
    public Long thanos_getLatestConsensusNumber() throws Exception {
        return gatewayApiService.getLatestConsensusNumber();
    }

    @Override
    public Long thanos_getCurrentCommitRound() throws Exception {
        return gatewayApiService.getCurrentCommitRound();
    }

    public String thanos_getBlockByNumber(String blockNumber) throws Exception {
        return gatewayApiService.getBlockByNumber(Long.valueOf(blockNumber));
    }

    public String thanos_getEthTransactionByHash(String transactionHash) throws Exception {
        final byte[] txHash = hexToByteArray(transactionHash);
        return gatewayApiService.getEthTransactionByHash(txHash);
    }


    @Override
    public String thanos_getEthTransactionByHashByChain(String transactionHash) throws Exception {
        final byte[] txHash = hexToByteArray(transactionHash);
        return gatewayApiService.getEthTransactionByHashByChain(txHash);
    }

    @Override
    public List<String> thanos_getEthTransactionsByHashes(String transactionHashs) throws Exception {
        List<String> transactionHashList = GSON.fromJson(transactionHashs, GSON_TYPE);

        List<byte[]> reqs = new ArrayList<>();
        for (String rq : transactionHashList) {
            reqs.add(hexToByteArray(rq));
        }
        return gatewayApiService.getEthTransactionsByHashes(reqs);
    }


    @Override
    public String thanos_sendEthRawTransaction(String rawData) throws Exception {
        long s1 = System.currentTimeMillis();
        EthTransaction tx = new EthTransaction(hexToByteArray(rawData));
        //tx.verify();

        long s2 = System.currentTimeMillis();
        gatewayApiService.validateAndSubmitEth(tx);
        long s3 = System.currentTimeMillis();
        log.info("JsonRpc4HttpImpl thanos_sendEthRawTransaction rlpParse time: {}, validateAndSubmitEth time: {}, total time:{}, sender:{}", (s2 - s1), (s3 - s2), (s3 - s1), TypeConverter.toJsonHex(tx.getSendAddress()));
        return TypeConverter.toJsonHex(tx.getHash());
    }

    @Override
    public List<String> thanos_sendEthRawTransactionList(String rawDataList) throws Exception {
        List<String> rawDatas = GSON.fromJson(rawDataList, GSON_TYPE);
        int txsSize = rawDatas.size();


        EthTransaction[] ethTransactions = new EthTransaction[txsSize];
        List<String> result = new ArrayList<>(txsSize);


        if (txsSize < PROCESSOR_NUM) {
            EthTransaction tx = null;
            for (int i = 0; i < txsSize; i++) {
                String rawData = rawDatas.get(i);
                if (StringUtils.isBlank(rawData)) {
                    continue;
                }
                tx = new EthTransaction(hexToByteArray(rawData));

                tx.verify();
                if (!tx.isValid()) {
                    continue;
                }

                ethTransactions[i] = tx;
                result.add(TypeConverter.toJsonHex(tx.getHash()));
            }
        } else {
            int batchSize = txsSize / PROCESSOR_NUM;
            int remainder = txsSize % PROCESSOR_NUM;

            ArrayBlockingQueue<List<String>> hashes = new ArrayBlockingQueue<>(PROCESSOR_NUM);

            CountDownLatch await = new CountDownLatch(PROCESSOR_NUM);
            //System.out.println("user parallel!");
            for (int count = 0; count < PROCESSOR_NUM; count++) {
                int startPosition = count * (batchSize);
                int endPosition = (count + 1) * batchSize - 1;

                if (count == PROCESSOR_NUM - 1) {
                    endPosition += remainder;
                }

                publicEvent(ethTransactions, startPosition, endPosition, count, rawDatas, hashes, await);
                //System.out.println(startPosition + "-" + endPosition + "-" + count);
            }
            await.await();


            for (List<String> ethTxHashes : hashes) {
                result.addAll(ethTxHashes);
            }
        }


        gatewayApiService.validateAndSubmitEthArray(ethTransactions);

        return result;
    }


    @Override
    public String thanos_ethCall(String rawData) throws Exception {
        EthTransaction tx = new EthTransaction(hexToByteArray(rawData));
        tx.verify();
        if (!tx.isValid()) {
            return "";
        }

        return gatewayApiService.ethCall(tx);
    }


}
