/*
 * Copyright (c) [2016] [ <ether.camp> ]
 * This file is part of the ethereumJ library.
 *
 * The ethereumJ library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ethereumJ library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ethereumJ library. If not, see <http://www.gnu.org/licenses/>.
 */
package com.thanos.gateway.core.common;


import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;


/**
 * Utility class to retrieve property values from the thanos-chain.conf files
 * <p>
 * The properties are taken from different sources and merged in the following order
 * (the config option from the next source overrides option from previous):
 * - resource thanos-chain.conf : normally used as a reference config with default values
 * and shouldn't be changed
 * - system property : each config entry might be altered via -D VM option
 * - [user dir]/config/thanos-chain.conf
 * - config specified with the -Dx-chain.conf.file=[file.conf] VM option
 * - CLI options
 *
 * @author laiyiyu
 * @since 22.05.2014
 */
public class SystemConfig {

    //private static Logger logger = LoggerFactory.getLogger("general");

    private static SystemConfig CONFIG;

    public static SystemConfig getDefault() {

        if (CONFIG == null) {
            CONFIG = new SystemConfig();
        }
        return CONFIG;
    }

    /**
     * Marks config accessor methods which need to be called (for value validation)
     * upon config creation or modification
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ValidateMe {
    }

    ;


    public Config config;


    private final ClassLoader classLoader;


    public SystemConfig() {
        this("thanos-gateway.conf");
    }

    public SystemConfig(File configFile) {
        this(ConfigFactory.parseFile(configFile));
    }

    public SystemConfig(String configResource) {
        this(ConfigFactory.parseResources(configResource));
    }

    public SystemConfig(Config apiConfig) {
        this(apiConfig, SystemConfig.class.getClassLoader());
    }

    public SystemConfig(Config apiConfig, ClassLoader classLoader) {
        try {
            this.classLoader = classLoader;

            Config javaSystemProperties = ConfigFactory.load("no-such-resource-only-system-props");
            Config referenceConfig = ConfigFactory.parseResources("thanos-gateway.conf");
            //logger.info("Config (" + (referenceConfig.entrySet().size() > 0 ? " yes " : " no  ") + "): default properties from resource 'thanos-chain.conf'");
            config = apiConfig;
            config = apiConfig
                    .withFallback(referenceConfig);

//            logger.debug("Config trace: " + config.root().render(ConfigRenderOptions.defaults().
//                    setComments(false).setJson(false)));

            config = javaSystemProperties.withFallback(config)
                    .resolve();     // substitute variables in config if any
            validateConfig();

        } catch (Exception e) {
            //logger.error("Can't read config.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads resources using given ClassLoader assuming, there could be several resources
     * with the same name
     */
    public static List<InputStream> loadResources(
            final String name, final ClassLoader classLoader) throws IOException {
        final List<InputStream> list = new ArrayList<InputStream>();
        final Enumeration<URL> systemResources =
                (classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader)
                        .getResources(name);
        while (systemResources.hasMoreElements()) {
            list.add(systemResources.nextElement().openStream());
        }
        return list;
    }

    public Config getConfig() {
        return config;
    }

    private void validateConfig() {
        for (Method method : getClass().getMethods()) {
            try {
                if (method.isAnnotationPresent(ValidateMe.class)) {
                    method.invoke(this);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error validating config method: " + method, e);
            }
        }
    }


    @ValidateMe
    public String nodeIpPort() {
        return config.getString("gateway.node.myself");
    }

    @ValidateMe
    public String rpcIpPort() {
        return config.getString("gateway.rpc.address");
    }

    public int rpcAcceptCount() {
        return config.hasPath("gateway.rpc.acceptCount") ? config.getInt("gateway.rpc.acceptCount") : 100;
    }

    public int rpcMaxThreads() {
        return config.hasPath("gateway.rpc.maxThreads") ? config.getInt("gateway.rpc.maxThreads") : 200;
    }

    public int rpcReadWriteTimeout() {
        return config.hasPath("gateway.rpc.readWriteTimeout") ? config.getInt("gateway.rpc.readWriteTimeout") : 1000 * 60 * 5;
    }

    @ValidateMe
    public int httpPort() {
        return config.getInt("gateway.http.port");
    }

    public int httpAcceptCount() {
        return config.hasPath("gateway.http.acceptCount") ? config.getInt("gateway.http.acceptCount") : 100;
    }

    public int httpMaxThreads() {
        return config.hasPath("gateway.http.maxThreads") ? config.getInt("gateway.http.maxThreads") : 200;
    }

    public int httpReadWriteTimeout() {
        return config.hasPath("gateway.http.readWriteTimeout") ? config.getInt("gateway.http.readWriteTimeout") : 1000 * 60 * 5;
    }

    public List<String> getClusterNodes() {
        return config.getStringList("gateway.broadcast");
    }

    public String getPushNode() {
        return config.getString("gateway.push.address");
    }

    public Integer getSyncAddress() {
        return config.getInt("gateway.sync.address");
    }

    public Integer blockCacheLimit() {
        return config.hasPath("gateway.sync.cache.blockLimit") ? config.getInt("gateway.sync.cache.blockLimit") : 10;
    }


    @ValidateMe
    public Integer txPoolDSCacheSizeLimit() {
        return config.getInt("gateway.sync.cache.txPoolDSCacheSizeLimit");
    }

    @ValidateMe
    public int decodeProcessNum() {
        int processNum = Runtime.getRuntime().availableProcessors();
        int defaultNum = 8;
        if (processNum > 100) {
            defaultNum = 64;
        } else if (processNum > 64) {
            defaultNum = 32;
        } else if (processNum > 32) {
            defaultNum = 16;
        }
        return config.hasPath("gateway.sync.decodeProcessNum")? config.getInt("gateway.sync.decodeProcessNum"): defaultNum;
    }

    @ValidateMe
    public String getSignAlgName() {
        return config.getString("crypto.sign.algorithm");
    }

    public boolean getOnlyBroadGlobalEvent() {
        return config.getInt("gateway.switch.only.broadcast.globalEvent") == 0 ? false : true;
    }

    @ValidateMe
    public String logConfigPath() {
        return config.getString("gateway.log.logConfigPath");
    }

    public boolean needTLS() {

        return config.hasPath("tls.needTLS") ? config.getBoolean("tls.needTLS") : false;

    }

    public String getKeyPath() {
        return config.getString("tls.keyPath");
    }

    public String getCertsPath() {
        return config.getString("tls.certsPath");
    }


}

