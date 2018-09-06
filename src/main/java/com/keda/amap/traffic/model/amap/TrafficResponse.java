package com.keda.amap.traffic.model.amap;

import lombok.Data;

import java.util.List;

/**
 * 交通态势返回结果
 * Created by liChenYu on 2018/9/6
 */
@Data
public class TrafficResponse {
    String info;
    String infocode;
    String status;
    TrafficInfo trafficInfo;

    @Data
    private class TrafficInfo {
        String description;
        Evaluation evaluation;
        List<Road> roads;
    }

    @Data
    private class Evaluation {
        String blocked;
        String congested;
        String description;
        String expedite;
        /**
         * 0：未知
         * 1：畅通
         * 2：缓行
         * 3：拥堵
         */
        Integer status;
        String unknown;
    }

    @Data
    private class Road {
        Double angle;
        String direction;
        String lcodes;
        String name;
        String polyline;
        Double speed;
        /**
         * 0：未知
         * 1：畅通
         * 2：缓行
         * 3：拥堵
         */
        Integer status;
    }
}
