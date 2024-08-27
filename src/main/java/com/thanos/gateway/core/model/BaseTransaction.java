package com.thanos.gateway.core.model;

import com.thanos.common.crypto.key.asymmetric.SecurePublicKey;
import com.thanos.common.utils.ByteArrayWrapper;
import com.thanos.common.utils.ByteUtil;
import com.thanos.common.utils.HashUtil;

import static com.thanos.common.utils.ByteUtil.ZERO_BYTE_ARRAY;

/**
 * BaseTransaction.java description：
 *
 * @Author laiyiyu create on 2021-03-02 15:19:41
 */
public abstract class BaseTransaction extends RLPModel {
    protected byte[] publicKey;

    protected byte[] nonce;

    protected long futureEventNumber;

    protected byte[] signature;

    //===============================

    protected byte[] hash;

    protected SecurePublicKey securePublicKey;

    protected byte[] sendAddress;

    protected ByteArrayWrapper dsCheck;

    protected volatile boolean hasValid;

    protected boolean valid;

    public BaseTransaction(byte[] rlpEncoded) {
        super(rlpEncoded);

    }

    public BaseTransaction(byte[] publicKey, long futureEventNumber, byte[] nonce, byte[] signature) {
        super(null);
        this.publicKey = publicKey;
        this.futureEventNumber = futureEventNumber;
        this.nonce = nonce;
        this.signature = signature;

        calculateBase();
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public byte[] getSignature() {
        return signature;
    }

    public long getFutureEventNumber() {
        return futureEventNumber;
    }


    public byte[] getNonce() {
        return nonce == null ? ZERO_BYTE_ARRAY : nonce;
    }

    public byte[] getHash() {
        return hash;
    }

    public byte[] getSendAddress() {
        return sendAddress;
    }

    public boolean isValid() {
        return valid;
    }

    public void verify() {
        if (hasValid) return;

        hasValid = true;

        if (!baseVerify()) {
            valid = false;
            return;
        }

        if (!this.securePublicKey.verify(this.hash, signature)) {
            valid = false;
            return;
        }
        valid = true;
    }

    protected boolean baseVerify() {
        return true;
    }

    public ByteArrayWrapper getDsCheck() {
        return dsCheck;
    }

    protected abstract byte[] calculateHash();

    protected void calculateBase() {
        this.securePublicKey = SecurePublicKey.generate(publicKey);
        this.sendAddress = this.securePublicKey.getAddress();
        this.dsCheck = new ByteArrayWrapper(ByteUtil.merge(sendAddress, nonce));
    }
}
