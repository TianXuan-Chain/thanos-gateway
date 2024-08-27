package com.thanos.gateway.initializer;

import com.googlecode.jsonrpc4j.spring.AutoJsonRpcServiceImplExporter;
import com.thanos.common.utils.ThanosThreadFactory;
import com.thanos.gateway.core.common.ConfigResourceUtil;
import com.thanos.gateway.core.common.SystemConfig;
import com.thanos.gateway.jsonrpc.GatewayApiService;
import com.thanos.gateway.jsonrpc.GatewayApiServiceImpl;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ApplicationConfig.java description：
 *
 * @Author laiyiyu create on 2021-03-05 15:52:39
 */
@Configuration
public class ApplicationConfig {

    @Bean
    public static GatewayApiService gatewayApiService() {
        SystemConfig systemConfig = ConfigResourceUtil.loadSystemConfig();
        return new GatewayApiServiceImpl(systemConfig);
    }

    @Bean
    public EmbeddedServletContainerFactory createEmbeddedServletContainerFactory()
    {

        SystemConfig systemConfig = ConfigResourceUtil.loadSystemConfig();
        TomcatEmbeddedServletContainerFactory tomcatFactory = new TomcatEmbeddedServletContainerFactory();
        tomcatFactory.setPort(systemConfig.httpPort());
        tomcatFactory.addConnectorCustomizers(new MyTomcatConnectorCustomizer());
        return tomcatFactory;
    }

    class MyTomcatConnectorCustomizer implements TomcatConnectorCustomizer
    {
        public void customize(Connector connector)
        {
            SystemConfig systemConfig = ConfigResourceUtil.loadSystemConfig();

            //default
            Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
            //设置最大连接数
            protocol.setMaxConnections(systemConfig.httpAcceptCount());
            //设置业务线程池
            //protocol.setMaxThreads(systemConfig.httpMaxThreads());
            protocol.setExecutor(new ThreadPoolExecutor(systemConfig.httpMaxThreads(), systemConfig.httpMaxThreads(), 20000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(systemConfig.httpAcceptCount() * 2), new ThanosThreadFactory("json_rpc_http")));
            // 连接超时和socket 读写超时统一数值
            protocol.setConnectionTimeout(systemConfig.httpReadWriteTimeout());
            protocol.setKeepAliveTimeout(systemConfig.httpReadWriteTimeout());


        }
    }

    @Bean
    public static AutoJsonRpcServiceImplExporter autoJsonRpcServiceImplExporter() {
        AutoJsonRpcServiceImplExporter exp = new AutoJsonRpcServiceImplExporter();
        //in here you can provide custom HTTP status code providers etc. eg:
        //exp.setHttpStatusCodeProvider();
        //exp.setErrorResolver();
        return exp;
    }
}
