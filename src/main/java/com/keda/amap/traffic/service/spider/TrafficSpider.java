package com.keda.amap.traffic.service.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.keda.amap.traffic.model.amap.BatchRequest;
import com.keda.amap.traffic.model.amap.BatchResponse;
import com.keda.amap.traffic.model.amap.TrafficResponse;
import com.keda.amap.traffic.model.entity.Parts;
import io.github.biezhi.anima.Anima;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public void batch(String batchUrl, BatchRequest requestBody, List<Parts> partsList) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(batchUrl, requestBody, String.class);

        List<BatchResponse<TrafficResponse>> batchResponses =
                JSON.parseObject(responseEntity.getBody(),
                        new TypeReference<List<BatchResponse<TrafficResponse>>>() {});

        Anima.atomic(() -> {
            for (int i = 0; i < partsList.size(); i++) {
                Parts parts = partsList.get(i);
                Integer id = parts.getId();

                TrafficResponse trafficResponse = batchResponses.get(i).getBody();
                Parts newParts = new Parts();
                newParts.setResultStatus(Integer.valueOf(trafficResponse.getStatus()));
                newParts.setResultInfoCode(trafficResponse.getInfocode());
                newParts.setResultInfo(trafficResponse.getInfo());
                newParts.setResultTraffic(JSON.toJSONString(trafficResponse.getTrafficInfo()));

                // 1. 返回-请求成功
                // 2. 返回-返回结果包含有效道路
                if(newParts.getResultStatus().equals(1) && null != trafficResponse.getTrafficInfo()
                        && !trafficResponse.getTrafficInfo().isNull()) {
                    newParts.setUseful(true);
                } else {
                    newParts.setUseful(false);
                }

                newParts.updateById(id);
            }
        });
    }

}
