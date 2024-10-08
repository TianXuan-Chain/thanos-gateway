package com.thanos.gateway.core.common;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.Status;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * ConfigResourceUtil.java description：
 *
 * @Author laiyiyu create on 2020-08-06 16:37:31
 */
public class ConfigResourceUtil {

    public static final SystemConfig systemConfig;

    static {
        systemConfig = initSystemConfig();
    }


    /**
     * 1、在启动时，若通过-DconfPath 来设置系统配置参数，则使用confPath
     * 2、否则，回到运行目录同级查找
     * 3、否则，在classpath 的resource 查找
     * @return
     */
    public static SystemConfig loadSystemConfig() {
        return systemConfig;
    }


    public static void loadLogConfig(String logPath) {
        File logbackFile = new File(logPath);
        if (logbackFile.exists()) {
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.getStatusManager().clear();
            lc.reset();
            try {
                configurator.doConfigure(logbackFile);

                try {
                    Logger logger = LoggerFactory.getLogger("main");
                    StringBuilder sb = new StringBuilder("{");
                    for (Status status: configurator.getStatusManager().getCopyOfStatusList()) {
                        sb.append(status).append(",").append("\n");
                    }
                    sb.append("}");
                    logger.info(" logback config:{}", sb.toString());

                } catch (Exception e) {


                }

            }
            catch (JoranException e) {
                e.printStackTrace(System.err);
                System.exit(-1);
            }
        }
    }

    private static SystemConfig initSystemConfig() {
        String confPath = System.getProperty("confPath");
        SystemConfig systemConfig;
        if (!StringUtils.isEmpty(confPath)) {
            File configFile = new File(confPath);
            systemConfig = new SystemConfig(configFile);
        } else {

            File configFile = new File("./resource/thanos-gateway.conf");
            if (configFile.canRead()) {
                systemConfig = new SystemConfig(configFile);
            } else {
                systemConfig = new SystemConfig();
            }

        }

        return systemConfig;
    }
}
