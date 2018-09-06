package com.keda.amap.traffic.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by liChenYu on 2018/9/5
 */
@Data
public class GeoJsonType {
    private String type;
    private List<GeometryJson> geometries;

    @Data
    public class  GeometryJson {
        private String type;
        private List coordinates;
    }
}
