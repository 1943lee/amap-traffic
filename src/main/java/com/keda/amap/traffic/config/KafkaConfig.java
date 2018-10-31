package com.keda.amap.traffic.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * kafka configuration
 * @author lcy
 * @date 2018/10/31
 */
@Data
@Configuration
public class KafkaConfig {

    @Value("${app.kafka.brokers}")
    private String brokers;

    @Value("${app.kafka.producer.topic}")
    private String topic;

    /**
     * 生产者配置
     */
    public Properties producerProperties() {
        Properties pro = new Properties();
        pro.put("bootstrap.servers", brokers);
        pro.put("acks", "all");
        pro.put("retries", 3);
        pro.put("batch.size", 16384);
        pro.put("linger.ms", 1);
        pro.put("buffer.memory", 33554432);
        pro.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        pro.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        return pro;
    }
}
