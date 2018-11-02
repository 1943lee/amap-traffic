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
    /**
     * 指定道路等级，下面各值代表的含义：
     * 1：高速（京藏高速）
     * 2：城市快速路、国道(西三环、103国道)
     * 3：高速辅路（G6辅路）
     * 4：主要道路（长安街、三环辅路路）
     * 5：一般道路（彩和坊路）
     * 6：无名道路
     *
     * 默认为5
     */
    private static final int DEFAULT_LEVEL = 5;

    public BatchRequest getBatchRequestBody(String key, List<Parts> partsList) {
        return getBatchRequestBody(key, partsList, DEFAULT_LEVEL);
    }

    public BatchRequest getBatchRequestBody(String key, List<Parts> partsList, int level) {
        BatchRequest batchRequest = new BatchRequest();

        List<GdUrl> gdUrls = new ArrayList<>();
        batchRequest.setOps(gdUrls);
        for(Parts parts : partsList) {
            String url = Consts.gdTrafficEndpoint + key + "&level=" + level +
                    "&rectangle=" + parts.getRequestRectangle();

            GdUrl gdUrl = new GdUrl();
            gdUrl.setUrl(url);
            gdUrls.add(gdUrl);
        }

        return batchRequest;
    }
}
