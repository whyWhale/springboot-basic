package org.prgrms.kdt.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "kdt")
public class OrderProperties implements InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(OrderProperties.class);

    private String version;
    private int minimumOrderAmount;
    private List<String> supportVendors;
    private String description;

    @Value("${JAVA_HOME}")
    private String javaHome;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.debug("[OrderProperties] version -> {}", version);
        logger.debug("[OrderProperties] minimumOrderAmount -> {}", minimumOrderAmount);
        logger.debug("[OrderProperties] supportVendors -> {}", supportVendors);
        logger.debug("[OrderProperties] description -> {}", description);
        logger.debug("[OrderProperties] javaHome -> {}", javaHome);
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getVersion() {
        return version;
    }

    public int getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public List<String> getSupportVendors() {
        return supportVendors;
    }

    public String getDescription() {
        return description;
    }

    public String getJavaHome() {
        return javaHome;
    }
}
