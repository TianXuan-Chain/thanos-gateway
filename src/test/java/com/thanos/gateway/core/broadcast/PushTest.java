package com.thanos.gateway.core.broadcast;

import com.google.protobuf.InvalidProtocolBufferException;
import com.thanos.gateway.core.model.EthTransaction;
import com.thanos.gateway.core.push.PushClient;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

import static com.thanos.gateway.jsonrpc.TypeConverter.hexToByteArray;

public class PushTest {

    @Test
    @Ignore
    public void test_grpc_send2Executor() throws InvalidProtocolBufferException {
        PushClient client = new PushClient("127.0.0.1", 5005);

        String str = "0xf8a6950e9431cf1200b8dcb8f6cfaa88f5d9693b268a6c4980832dc6c00180b844a9059cbb000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000051ba0226b5fdf683c7ae301338fdfadd44f77ce5693a6f32481d43c3df0cbd17c2ec6a034629c6deb6c64647122b4e8a1f45e0a107800c64297eff51fdaeaf6bd37298b";
        EthTransaction tx = new EthTransaction(hexToByteArray(str));

        for(int i=0;i<100;i++) {
            client.pushEthTransactions(Arrays.asList(tx));
        }
    }

}
