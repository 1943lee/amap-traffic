package com.keda.amap.traffic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liChenYu on 2018/9/5
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "es")
public class EsConfig {
    private String hosts;
    private String username;
    private String password;
}
