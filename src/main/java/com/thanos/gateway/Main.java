package com.thanos.gateway;

import com.thanos.gateway.core.common.ConfigResourceUtil;
import com.thanos.gateway.core.common.SystemConfig;
import com.thanos.gateway.initializer.ComponentInitializer;
import com.thanos.gateway.jsonrpc.GatewayApiService;
import com.thanos.gateway.jsonrpc.JsonRpcInit;
import com.typesafe.config.ConfigRenderOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 类Main.java的实现描述：
 *
 * @author laiyiyu create on 2020/12/3 16:14
 */
@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SystemConfig systemConfig = ConfigResourceUtil.loadSystemConfig();
        ConfigResourceUtil.loadLogConfig(systemConfig.logConfigPath());
        Logger logger = LoggerFactory.getLogger("main");
        logger.info("Config trace: " + systemConfig.config.root().render(ConfigRenderOptions.defaults().setComments(false).setJson(false)));

        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(Main.class, args);
        GatewayApiService gatewayApiService = configurableApplicationContext.getBean("gatewayApiService", GatewayApiService.class);
        JsonRpcInit.init(systemConfig, gatewayApiService);
        logger.info("Main start success!!");
    }
}
