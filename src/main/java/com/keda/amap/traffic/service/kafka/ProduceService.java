package com.keda.amap.traffic.service.kafka;

import com.keda.amap.traffic.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * kafka producer
 * @author lcy
 * @date 2018/10/31
 */
@Service
public class ProduceService {

    private final KafkaConfig kafkaConfig;

    private KafkaProducer<String, String> kafkaProducer;

    @Autowired
    public ProduceService(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    //生产者
    private void getKafkaProducer() {
        try {
            kafkaProducer = new KafkaProducer<>(kafkaConfig.producerProperties());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //发送数据
    public void sendMessage(String id, String message) {
        if (kafkaProducer == null) {
            getKafkaProducer();
        }

        try {
            String topicName = kafkaConfig.getTopic();
            ProducerRecord<String, String> record = new ProducerRecord<>(topicName, id, message);
            kafkaProducer.send(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
