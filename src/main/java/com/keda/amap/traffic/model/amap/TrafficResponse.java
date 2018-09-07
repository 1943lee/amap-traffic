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
    public static class TrafficInfo {
        String description;
        Evaluation evaluation;
        List<Road> roads;

        /**
         * 如果没有道路的线信息，就当做空数据处理
         */
        public boolean isNull() {
            return null == roads || (roads.size() == 1 && roads.get(0).isNull());
        }
    }

    @Data
    public static class Evaluation {
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
        String status;
        String unknown;
    }

    @Data
    public static class Road {
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
        String status;

        /**
         * 如果没有线信息，就当做空数据处理
         */
        boolean isNull() {
            return null == polyline;
        }
    }
}
