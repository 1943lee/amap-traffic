package com.keda.amap.traffic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liChenYu on 2018/9/6
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "amap")
public class AmapConfig {
    private String[] apiKey;

    private int index = 0;

    public String getApiKey() {
        if(null != apiKey && apiKey.length > 0) {
            String ak = apiKey[index];
            index++;
            index = index % apiKey.length;
            return ak;
        }
        return "";
    }
}
