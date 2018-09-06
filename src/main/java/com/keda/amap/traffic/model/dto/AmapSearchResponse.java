package com.keda.amap.traffic.model.dto;

import lombok.Data;

import java.util.List;

/**
 * Created by liChenYu on 2018/9/5
 */
@Data
public class AmapSearchResponse {
    private long total;
    private List<double[][]> points;
}
