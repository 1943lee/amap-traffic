package com.keda.amap.traffic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author lcy
 * @date 2018/11/16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.job")
public class JobConfig {
    private boolean enable;
    private String cron;
    private String group;
    private String description;
}
