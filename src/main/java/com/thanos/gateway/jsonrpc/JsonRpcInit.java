package com.thanos.gateway.jsonrpc;

import com.thanos.gateway.core.common.SystemConfig;

/**
 * JsonRpcInit.java description：
 *
 * @Author laiyiyu create on 2021-03-06 11:38:03
 */
public class JsonRpcInit {

    public static void init(SystemConfig systemConfig, GatewayApiService gatewayApiService) {
        JsonRpc jsonRpc = new JsonRpcImpl(gatewayApiService);
        RpcServiceProvider rpcServiceProvider = new RpcServiceProvider(systemConfig, jsonRpc);
        rpcServiceProvider.start();
    }
}
