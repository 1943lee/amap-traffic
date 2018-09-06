package com.keda.amap.traffic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 目标范围extent
 * Created by liChenYu on 2018/9/4
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "extent")
public class SourceConfig {

    private String folder;
    private String description;
    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;
    private double width;
    private double height;
}
