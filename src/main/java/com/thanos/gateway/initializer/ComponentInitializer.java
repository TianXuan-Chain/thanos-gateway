package com.thanos.gateway.initializer;

import com.thanos.gateway.core.common.ConfigResourceUtil;
import com.thanos.gateway.core.common.SystemConfig;
import com.thanos.gateway.jsonrpc.*;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 类ComponetInitializer.java的实现描述：
 *
 * @author xuhao create on 2020/12/3 16:17
 */

public class ComponentInitializer {

    public static void init(SystemConfig systemConfig) {
        GatewayApiService gatewayApiService = new GatewayApiServiceImpl(systemConfig);
        JsonRpc jsonRpc = new JsonRpcImpl(gatewayApiService);

        RpcServiceProvider rpcServiceProvider = new RpcServiceProvider(systemConfig, jsonRpc);
        rpcServiceProvider.start();
    }
}
