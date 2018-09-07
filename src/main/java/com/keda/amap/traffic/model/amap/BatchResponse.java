package com.keda.amap.traffic.model.amap;

import lombok.Data;

/**
 * Created by liChenYu on 2018/9/7
 */
@Data
public class BatchResponse<T> {
    Integer status;
    T body;
    String header;
}
