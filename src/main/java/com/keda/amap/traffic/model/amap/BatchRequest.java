package com.keda.amap.traffic.model.amap;

import lombok.Data;

import java.util.List;

/**
 * Created by liChenYu on 2018/9/6
 */
@Data
public class BatchRequest {
    List<GdUrl> ops;

    @Data
    public static class GdUrl {
        String url;
    }
}
