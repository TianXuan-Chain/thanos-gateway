package com.thanos.gateway.core.model;

import com.thanos.common.utils.ByteArrayWrapper;
import com.thanos.common.utils.ByteUtil;
import com.thanos.common.utils.HashUtil;
import com.thanos.common.utils.rlp.*;
import org.spongycastle.util.BigIntegers;
import org.spongycastle.util.encoders.Hex;

import java.math.BigInteger;

/**
 * GlobalNodeEvent.java description：
 *
 * @Author laiyiyu create on 2021-03-01 17:33:07
 */
public class GlobalNodeEvent extends BaseTransaction {


    byte commandCode;

    byte[] data;

    //=====================


    public GlobalNodeEvent(byte[] rlpEncoded) {
        super(rlpEncoded);
    }

    public GlobalNodeEvent(byte[] publicKey, byte[] nonce, long futureEventNumber, byte commandCode, byte[] data, byte[] signature) {
        super(publicKey, futureEventNumber, nonce, signature);

        this.commandCode = commandCode;
        this.data = data;

        this.hash = calculateHash();
        this.rlpEncoded = rlpEncoded();
    }


    //for speed
    public GlobalNodeEvent(byte[] hash, byte[] publicKey, byte[] nonce, long futureEventNumber, byte commandCode, byte[] data, byte[] signature) {
        super(publicKey, futureEventNumber, nonce, signature);

        this.commandCode = commandCode;
        this.data = data;

        this.hash = hash;
        this.valid = true;
        this.rlpEncoded = rlpEncoded();
    }


    public byte getCommandCode() {
        return commandCode;
    }

    public byte[] getData() {
        return data;
    }

    public ByteArrayWrapper getDsCheck() {
        return dsCheck;
    }

    @Override
    protected byte[] calculateHash() {
        return HashUtil.sha3Dynamic(this.publicKey, this.nonce, BigIntegers.asUnsignedByteArray(BigInteger.valueOf(this.futureEventNumber)), BigIntegers.asUnsignedByteArray(BigInteger.valueOf(this.commandCode)), this.data);
    }

    @Override
    protected byte[] rlpEncoded() {
        byte[] publicKey = RLP.encodeElement(this.publicKey);
        byte[] nonce = RLP.encodeElement(this.nonce);
        byte[] futureEventNumber = RLP.encodeBigInteger(BigInteger.valueOf(this.futureEventNumber));

        byte[] commandCode = RLP.encodeByte(this.commandCode);
        byte[] data = RLP.encodeElement(this.data);
        byte[] signature = RLP.encodeElement(this.signature);

        return RLP.encodeList(publicKey, nonce, futureEventNumber, commandCode, data, signature);
    }

    @Override
    protected void rlpDecoded() {
        RLPList rlpInfo = (RLPList) RLP.decode2(rlpEncoded).get(0);
        this.publicKey = rlpInfo.get(0).getRLPData();
        this.nonce = rlpInfo.get(1).getRLPData();
        this.futureEventNumber = ByteUtil.byteArrayToLong(rlpInfo.get(2).getRLPData());

        this.commandCode = (byte) ByteUtil.byteArrayToInt(rlpInfo.get(3).getRLPData());
        this.data = rlpInfo.get(4).getRLPData();
        this.signature = rlpInfo.get(5).getRLPData();

        this.hash = calculateHash();
        calculateBase();

    }
}
