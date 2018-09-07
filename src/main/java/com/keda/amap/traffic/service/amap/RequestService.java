package com.keda.amap.traffic.service.amap;

import com.keda.amap.traffic.bootstrap.Consts;
import com.keda.amap.traffic.model.amap.BatchRequest;
import com.keda.amap.traffic.model.amap.BatchRequest.GdUrl;
import com.keda.amap.traffic.model.entity.Parts;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 构造高德请求相关
 * Created by liChenYu on 2018/9/6
 */
@Service
public class RequestService {
    public BatchRequest getBatchRequestBody(String key, List<Parts> partsList) {
        BatchRequest batchRequest = new BatchRequest();

        List<GdUrl> gdUrls = new ArrayList<>();
        batchRequest.setOps(gdUrls);
        for(Parts parts : partsList) {
            String url = Consts.gdTrafficEndpoint + key + "&rectangle=" + parts.getRequestRectangle();

            GdUrl gdUrl = new GdUrl();
            gdUrl.setUrl(url);
            gdUrls.add(gdUrl);
        }

        return batchRequest;
    }
}
