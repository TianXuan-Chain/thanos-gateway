package com.thanos.gateway.core.broadcast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.thanos.common.crypto.key.asymmetric.ec.ECKeyOld;
import com.thanos.gateway.core.model.EthTransaction;
import org.junit.Ignore;
import org.junit.Test;

import static com.thanos.gateway.jsonrpc.TypeConverter.hexToByteArray;

public class EthTransactionTest {

    private static final String str = "0xf8a6950e9431cf1200b8dcb8f6cfaa88f5d9693b268a6c4980832dc6c00180b844a9059cbb000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000051ba0226b5fdf683c7ae301338fdfadd44f77ce5693a6f32481d43c3df0cbd17c2ec6a034629c6deb6c64647122b4e8a1f45e0a107800c64297eff51fdaeaf6bd37298b";

    @Test
    @Ignore
    public void test_transaction_() throws InvalidProtocolBufferException {
        EthTransaction tx = new EthTransaction(hexToByteArray(str));
        //tx.rlpParse();

        byte[] signature = tx.getSignature();
        createByByte(signature);
    }

    private static ECKeyOld.ECDSASignature createByByte(byte[] data) {
        byte[] r = new byte[32];
        System.arraycopy(data, 0, r, 0, 32);
        byte[] s = new byte[32];
        System.arraycopy(data, 32, s, 0, 32);
        byte v = data[data.length - 1];
        return ECKeyOld.ECDSASignature.fromComponents(r, s, v);
    }

}
