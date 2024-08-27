package com.thanos.gateway.jsonrpc;

import com.googlecode.jsonrpc4j.JsonRpcMethod;
import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;

import java.util.List;


//@JsonRpcService(AppConst.JSON_RPC_PATH)
public interface JsonRpc {
//    @JsonRpcMethod("thanos_clientVersion")
    String thanos_clientVersion();

//    @JsonRpcMethod("thanos_sha3")
    String thanos_sha3(String data) throws Exception;

//    @JsonRpcMethod("thanos_net_version")
    String thanos_net_version();

//    @JsonRpcMethod("thanos_protocolVersion")
    String thanos_protocolVersion();

//    @JsonRpcMethod("thanos_getCompilers")
    String[] thanos_getCompilers();

//    @JsonRpcMethod(thanos_sendEthRawTransaction")
    String thanos_sendEthRawTransaction(@JsonRpcParam(value = "rawData") String rawData) throws Exception;

//    @JsonRpcMethod("thanos_sendEthRawTransactionList")
    List<String> thanos_sendEthRawTransactionList(String rawDataList) throws Exception;

    String thanos_sendGlobalNodeEvent(@JsonRpcParam(value = "rawData") String rawData) throws Exception;


    //    @JsonRpcMethod("thanos_ethCall")
    String thanos_ethCall(@JsonRpcParam(value = "rawData") String rawData) throws Exception;

//    @JsonRpcMethod("thanos_getLatestBeExecutedNum")
    Long thanos_getLatestBeExecutedNum() throws Exception;

//    @JsonRpcMethod("thanos_getLatestConsensusNumber")
    Long thanos_getLatestConsensusNumber() throws Exception;

    Long thanos_getCurrentCommitRound() throws Exception;

//    @JsonRpcMethod("thanos_getBlockByNumber")
    String thanos_getBlockByNumber(String blockNumber) throws Exception;

//    @JsonRpcMethod("thanos_getEthTransactionByHash")
    String thanos_getEthTransactionByHash(String transactionHash) throws Exception;

//    @JsonRpcMethod("thanos_getGlobalNodeEventByHash")
    String thanos_getGlobalNodeEventByHash(String toHexString) throws Exception;

    String thanos_getGlobalNodeEventReceiptByHash(String toHexString) throws Exception;

    String thanos_getGlobalNodeEventByHashByChain(String toHexString) throws Exception;

    //    @JsonRpcMethod("thanos_getEthTransactionByHashByChain")
    String thanos_getEthTransactionByHashByChain(String transactionHash) throws Exception;

//    @JsonRpcMethod("thanos_getEthTransactionsByHashes")
    List<String> thanos_getEthTransactionsByHashes(String transactionHashs) throws Exception;

    String getEpochState();

}
