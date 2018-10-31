package com.keda.amap.traffic.service.spider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.keda.amap.traffic.model.amap.BatchRequest;
import com.keda.amap.traffic.model.amap.BatchResponse;
import com.keda.amap.traffic.model.amap.TrafficResponse;
import com.keda.amap.traffic.model.entity.Parts;
import io.github.biezhi.anima.Anima;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * 交通态势抓取
 *
 * Created by liChenYu on 2018/9/6
 */
@Service
public class TrafficSpider {

    public List<BatchResponse<TrafficResponse>> batch(String batchUrl, BatchRequest requestBody) {
        return getBatchResponses(batchUrl, requestBody);
    }

    public void batch(String batchUrl, BatchRequest requestBody, List<Parts> partsList) {

        List<BatchResponse<TrafficResponse>> batchResponses = getBatchResponses(batchUrl, requestBody);

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

    private List<BatchResponse<TrafficResponse>> getBatchResponses(String batchUrl, BatchRequest requestBody) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(batchUrl);
        StringEntity entity = new StringEntity(JSON.toJSONString(requestBody), "utf-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String responseEntity = EntityUtils.toString(response.getEntity());
            return JSON.parseObject(responseEntity,
                    new TypeReference<List<BatchResponse<TrafficResponse>>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
