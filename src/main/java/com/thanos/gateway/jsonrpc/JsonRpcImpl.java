package com.thanos.gateway.jsonrpc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thanos.common.utils.HashUtil;
import com.thanos.common.utils.Numeric;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.model.GlobalNodeEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.thanos.gateway.jsonrpc.TypeConverter.hexToByteArray;

//@Service
//@AutoJsonRpcServiceImpl
public class JsonRpcImpl implements JsonRpc {

    private static final Logger log = LoggerFactory.getLogger("jsonrpc");

    private final static Gson GSON = new Gson();
    private final static Type GSON_TYPE = new TypeToken<List<String>>() {
    }.getType();

    private GatewayApiService gatewayApiService;

    public JsonRpcImpl(GatewayApiService gatewayApiService) {
        this.gatewayApiService = gatewayApiService;
    }

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
    public String thanos_getGlobalNodeEventByHash(String transactionHash) throws Exception {
        final byte[] txHash = hexToByteArray(transactionHash);
        return gatewayApiService.getGlobalNodeEventByHash(txHash);
    }

    @Override
    public String thanos_getGlobalNodeEventReceiptByHash(String transactionHash) throws Exception {
        final byte[] txHash = hexToByteArray(transactionHash);
        return gatewayApiService.getGlobalNodeEventReceiptByHash(txHash);
    }

    @Override
    public String thanos_getGlobalNodeEventByHashByChain(String transactionHash) throws Exception {
        final byte[] txHash = hexToByteArray(transactionHash);
        return gatewayApiService.getGlobalNodeEventByHashByChain(txHash);
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
        EthTransaction tx = null;
        try {
            tx = new EthTransaction(hexToByteArray(rawData));
            gatewayApiService.validateAndSubmitEth(tx);
            return TypeConverter.toJsonHex(tx.getHash());
        } catch (Exception e) {
            log.warn("thanos_sendEthRawTransaction error. txHash:{}.", tx == null ? "" : Numeric.toHexString(tx.getHash()), e);
            throw e;
        }
    }

    @Override
    public List<String> thanos_sendEthRawTransactionList(String rawDataList) throws Exception {

        List<String> rawDatas = GSON.fromJson(rawDataList, GSON_TYPE);

        List<String> result = new ArrayList<>();

        List<EthTransaction> ethTransactions = new ArrayList<>();
        EthTransaction tx = null;
        for (String rawData : rawDatas) {
            if (StringUtils.isBlank(rawData)) {
                continue;
            }
            tx = new EthTransaction(hexToByteArray(rawData));

            tx.verify();
            if (!tx.isValid()) {
                continue;
            }

            ethTransactions.add(tx);
            result.add(TypeConverter.toJsonHex(tx.getHash()));
        }

        gatewayApiService.validateAndSubmitEthList(ethTransactions);
        return result;
    }

    @Override
    public String thanos_sendGlobalNodeEvent(String rawData) throws Exception {
        GlobalNodeEvent globalNodeEvent = null;
        try {
            globalNodeEvent = new GlobalNodeEvent(hexToByteArray(rawData));
            gatewayApiService.validateAndSubmitGlobal(globalNodeEvent);
            return TypeConverter.toJsonHex(globalNodeEvent.getHash());
        } catch (Exception e) {
            log.warn("thanos_sendGlobalNodeEvent error. txHash:{}.", globalNodeEvent == null ? "" : Numeric.toHexString(globalNodeEvent.getHash()), e);
            throw e;
        }
    }

    @Override
    public String thanos_ethCall(String rawData) throws Exception {
        try {
            EthTransaction tx = new EthTransaction(hexToByteArray(rawData));
            tx.verify();
            return gatewayApiService.ethCall(tx);
        } catch (Exception e) {
            log.warn("thanos_ethCall error. txHash:{}.", HashUtil.sha3(hexToByteArray(rawData)), e);
            throw e;
        }
    }

    @Override
    public String getEpochState() {
        return gatewayApiService.getEpochState();
    }

}
