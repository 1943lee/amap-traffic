package com.keda.amap.traffic.service.spider;

import com.keda.amap.traffic.model.amap.TrafficResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 交通态势抓取
 *
 * Created by liChenYu on 2018/9/6
 */
@Service
public class TrafficSpider {

    @Autowired
    RestTemplate restTemplate;

    public List<TrafficResponse> batch(String batchUrl, String requestBody) {
        restTemplate.postForEntity(batchUrl, requestBody, null);
        return null;
    }

}
