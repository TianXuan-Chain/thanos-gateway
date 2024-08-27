package com.thanos.gateway.jsonrpc;

import com.thanos.gateway.core.common.SystemConfig;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.Peer;
import com.thanos.gateway.core.model.GlobalNodeEvent;

import java.util.List;


/**
 * 类Main.java的实现描述：
 *
 * @Author GatewayApiServiceImpl create on 2019-11-28 12:27:49
 */
public class GatewayApiServiceImpl implements GatewayApiService {

    private Peer peer;

    public GatewayApiServiceImpl(SystemConfig systemConfig) {
        peer = Peer.startUp(systemConfig);
    }

    @Override
    public void validateAndSubmitEth(EthTransaction tx) {
        peer.getTransactionPool().addEthTransactionFromClient(tx);
    }

    @Override
    public void validateAndSubmitEthList(List<EthTransaction> ethTransactions) {
        peer.getTransactionPool().addEthTransactionList(ethTransactions);
    }

    public void validateAndSubmitEthArray(EthTransaction[] ethTransactions) {
        peer.getTransactionPool().addEthTransactionArray(ethTransactions);
    }

    @Override
    public String getBlockByNumber(Long blockNumber) {
        return peer.getPushManager().getBlockByNumber(blockNumber);
    }

    @Override
    public String getEthTransactionByHash(byte[] txHash) {
        return peer.getPushManager().getTransactionByHash(txHash);
    }

    @Override
    public String getGlobalNodeEventByHash(byte[] txhash) {
        return peer.getPushManager().getGlobalNodeEventByCache(txhash);
    }

    @Override
    public String getGlobalNodeEventReceiptByHash(byte[] txhash) {
        return peer.getPushManager().getGlobalNodeEventReceipt(txhash);
    }

    @Override
    public String getGlobalNodeEventByHashByChain(byte[] txhash) {
        return peer.getPushManager().getGlobalNodeEventByChain(txhash);
    }

    @Override
    public String getEpochState() {
        return peer.getPushManager().getEpochState();
    }

    @Override
    public String getEthTransactionByHashByChain(byte[] txHash) {
        return peer.getPushManager().getTransactionByHashByChain(txHash);
    }

    @Override
    public List<String> getEthTransactionsByHashes(List<byte[]> reqs) {
        return peer.getPushManager().getTransactionsByHashes(reqs);
    }

    @Override
    public Long getLatestBeExecutedNum() {
        return peer.getPushManager().getLatestBeExecutedNum();
    }

    @Override
    public Long getLatestConsensusNumber() {
        return peer.getPushManager().getLatestConsensusNumber();
    }

    @Override
    public Long getCurrentCommitRound() {
        return peer.getPushManager().getCurrentCommitRound();
    }

    @Override
    public String ethCall(EthTransaction tx) {
        return peer.getPushManager().ethCall(tx);
    }

    @Override
    public void validateAndSubmitGlobal(GlobalNodeEvent globalNodeEvent) {
        peer.getTransactionPool().addGlobalNodeEventFromClient(globalNodeEvent);
    }
}
