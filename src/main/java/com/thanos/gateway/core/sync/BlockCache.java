package com.thanos.gateway.core.sync;

import com.thanos.common.utils.ByteUtil;
import com.thanos.common.utils.HashUtil;
import com.thanos.common.utils.rlp.RLP;
import com.thanos.common.utils.rlp.RLPList;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.model.GlobalNodeEvent;
import org.spongycastle.util.encoders.Hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockCache {

    private byte[] encode;

    private byte[] hash;
    private byte[] eventId;
    private Long number;
    private Long timestamp;
    private List<byte[]> receipts;

    private List<byte[]> globalEvents;

    private HashMap<String, byte[]> receiptsTable;
    private HashMap<String, byte[]> globalEventsTable;

    public BlockCache(byte[] encode) {
        this.encode = encode;
        rlpDecodedBlock();
    }

    public byte[] getBlockCacheEncode() {
        return encode;
    }

    public byte[] getEventId() {
        return eventId;
    }

    public byte[] getHash() {
        return hash;
    }

    public Long getNumber() {
        return number;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public List<byte[]> getReceipts() {
        return receipts;
    }

    public List<byte[]> getGlobalEvents() {
        return globalEvents;
    }

    public HashMap<String, byte[]> getReceiptsTable() {
        return receiptsTable;
    }

    public HashMap<String, byte[]> getGlobalEventsTable() {
        return globalEventsTable;
    }

    public void rlpDecodedBlock() {
        byte[] bytes = this.encode;
        RLPList params = RLP.decode2(bytes);
        RLPList block = (RLPList) params.get(0);
        this.eventId = block.get(0).getRLPData();
        byte[] preEventId = block.get(1).getRLPData();
        byte[] coinbase = block.get(2).getRLPData() == null ? ByteUtil.ZERO_BYTE_ARRAY : block.get(2).getRLPData();
        byte[] stateRoot = block.get(3).getRLPData();
        byte[] receiptsRoot = block.get(4).getRLPData();
        Long epoch = ByteUtil.byteArrayToLong(block.get(5).getRLPData());
        this.number = ByteUtil.byteArrayToLong(block.get(6).getRLPData());
        this.timestamp = ByteUtil.byteArrayToLong(block.get(7).getRLPData());

        //反序列化全局事件
        byte[] globalEvent = block.get(8).getRLPData();
        rlpDecodeGlobalEvent(globalEvent);

        this.hash = HashUtil.sha3Dynamic(eventId, preEventId, coinbase, stateRoot, receiptsRoot, ByteUtil.longToBytes(epoch), ByteUtil.longToBytes(number), ByteUtil.longToBytes(timestamp));

        //反序列化回执
        byte[] rcdata = block.get(9).getRLPData();
        if (rcdata != null && rcdata.length > 0) {
            int receiptsSize = ByteUtil.byteArrayToInt(rcdata);
            List<byte[]> receipts = new ArrayList<>(receiptsSize);
            HashMap<String, byte[]> receiptsTable = new HashMap<>(receiptsSize);
            int receiptEnd = receiptsSize + 10;
            for (int i = 10; i < receiptEnd; i++) {
                receipts.add(block.get(i).getRLPData());
                receiptsTable.put(getTxReceiptHash(block.get(i).getRLPData()), block.get(i).getRLPData());
            }
            this.receipts = receipts;
            this.receiptsTable = receiptsTable;
        } else {
            this.receipts = new ArrayList<>();
            this.receiptsTable = new HashMap<>();
        }

    }

    public void rlpDecodeGlobalEvent(byte[] rlpEncoded) {
        if (rlpEncoded == null) {
            return;
        }
        if (rlpEncoded.length <= 0) {
            return;
        }
        RLPList params = RLP.decode2(rlpEncoded);
        RLPList payload = (RLPList) params.get(0);
        int globalNodeEventsSize = ByteUtil.byteArrayToInt(payload.get(0).getRLPData());
        List<byte[]> globalNodeEvents = new ArrayList<>(globalNodeEventsSize);
        HashMap<String, byte[]> globalEventsTable = new HashMap<>(globalNodeEventsSize);
        int i = 1;
        for (; i < globalNodeEventsSize + 1; i++) {
            globalNodeEvents.add(payload.get(i).getRLPData());
            globalEventsTable.put(getGlobalEventHash(payload.get(i).getRLPData()), payload.get(i).getRLPData());
        }
        this.globalEvents = globalNodeEvents;
        this.globalEventsTable = globalEventsTable;
    }

    public String getTxReceiptHash(byte[] receipt) {
        // receipt rlp decoded
        RLPList params = RLP.decode2(receipt);
        RLPList receiptD = (RLPList) params.get(0);
        RLPList transactionRLP = (RLPList) receiptD.get(1);

        // transaction rlp decoded

        EthTransaction ethTransaction = new EthTransaction(transactionRLP.getRLPData());
        //RLPList decodedTxList = RLP.decode2(transactionRLP.getRLPData());
        //RLPList transaction = (RLPList) decodedTxList.get(0);
        //cal hash
        return Hex.toHexString(ethTransaction.getHash());
    }

    public String getGlobalEventHash(byte[] data) {
        //RLPList rlpPkInfo = (RLPList) RLP.decode2(data).get(0);
        GlobalNodeEvent globalNodeEvent = new GlobalNodeEvent(data);
        return Hex.toHexString(globalNodeEvent.getHash());
    }
}

