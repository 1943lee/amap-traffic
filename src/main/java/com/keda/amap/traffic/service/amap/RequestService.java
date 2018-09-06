package com.keda.amap.traffic.service.amap;

import com.keda.amap.traffic.model.amap.BatchRequest;
import com.keda.amap.traffic.model.amap.BatchRequest.GdUrl;
import com.keda.amap.traffic.model.entity.Parts;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 构造高德请求相关
 * Created by liChenYu on 2018/9/6
 */
@Service
public class RequestService {
    public BatchRequest getBatchRequestBody(List<Parts> parts) {
        BatchRequest batchRequest = new BatchRequest();
        GdUrl gdUrl = new GdUrl();

    }
}
