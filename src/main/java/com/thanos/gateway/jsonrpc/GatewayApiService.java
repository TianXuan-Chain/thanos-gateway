package com.thanos.gateway.jsonrpc;

import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.model.GlobalNodeEvent;

import java.util.List;

public interface GatewayApiService {

    void validateAndSubmitEth(EthTransaction tx);

    void validateAndSubmitEthList(List<EthTransaction> ethTransactions);

    void validateAndSubmitEthArray(EthTransaction[] ethTransactions);

    String getBlockByNumber(Long blockNumber);

    String getEthTransactionByHash(byte[] txHash);

    String getEthTransactionByHashByChain(byte[] txHash);

    List<String> getEthTransactionsByHashes(List<byte[]> reqs);

    Long getLatestBeExecutedNum();

    Long getLatestConsensusNumber();

    Long getCurrentCommitRound();

    String ethCall(EthTransaction tx);

    void validateAndSubmitGlobal(GlobalNodeEvent globalNodeEvent);

    String getGlobalNodeEventByHash(byte[] txhash);

    String getGlobalNodeEventReceiptByHash(byte[] txhash);

    String getGlobalNodeEventByHashByChain(byte[] txhash);

    String getEpochState();

}
