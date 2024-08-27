package com.thanos.gateway.jsonrpc;

import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.ProxyUtil;
import com.thanos.common.crypto.CastleProvider;
import com.thanos.common.utils.KeyStoreUtil;
import com.thanos.common.utils.SSLUtil;
import com.thanos.gateway.core.common.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * 类ServiceProvider.java的实现描述：
 *
 * @author xuhao create on 2020/12/3 17:54
 */

public class RpcServiceProvider {

    private static Logger logger = LoggerFactory.getLogger("jsonrpc");

    private CustomizeStreamServer streamServer;

    public RpcServiceProvider(SystemConfig systemConfig, JsonRpc jsonRpc) {
        Object compositeService = ProxyUtil.createCompositeServiceProxy(
                this.getClass().getClassLoader(),
                new Object[]{jsonRpc},
                new Class<?>[]{JsonRpc.class},
                true);
        JsonRpcServer jsonRpcServer = new JsonRpcServer(compositeService);

        ServerSocket serverSocket = loadServerSocket(systemConfig);
        this.streamServer = new CustomizeStreamServer(jsonRpcServer, systemConfig.rpcMaxThreads(), systemConfig.rpcReadWriteTimeout(), serverSocket);
    }

    public void start() {
        streamServer.start();
    }

    private ServerSocket loadServerSocket(SystemConfig systemConfig) {
        ServerSocket serverSocket = null;
        try {
            String rpcIpPort = systemConfig.rpcIpPort();
            if (StringUtils.isBlank(rpcIpPort)) {
                throw new RuntimeException("rpcIpPort not exist!");
            }
            String[] ipPortStr = rpcIpPort.split(":");
            InetAddress bindAddress = InetAddress.getByName(ipPortStr[0]);
            int port = Integer.valueOf(ipPortStr[1]);
            ServerSocketFactory serverSocketFactory;
            if (systemConfig.needTLS()) {
                SSLContext sslContext = SSLUtil.loadSSLContext(systemConfig.getKeyPath(), systemConfig.getCertsPath());
                serverSocketFactory = sslContext.getServerSocketFactory();
            } else {
                serverSocketFactory = ServerSocketFactory.getDefault();
            }
            serverSocket = serverSocketFactory.createServerSocket(port, systemConfig.rpcAcceptCount(), bindAddress);
            serverSocket.setSoTimeout(5000);
            serverSocket.setReuseAddress(false);
        } catch (Exception e) {
            logger.error("initSocket error!", e);
            throw new RuntimeException(e);
        }
        return serverSocket;
    }
}
