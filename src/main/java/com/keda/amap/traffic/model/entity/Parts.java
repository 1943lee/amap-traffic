package com.keda.amap.traffic.model.entity;

import io.github.biezhi.anima.Model;
import io.github.biezhi.anima.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by liChenYu on 2018/9/4
 */
@Data
@NoArgsConstructor
@Table(name = "t_parts")
public class Parts extends Model {
    private Integer id;
    private Integer row;
    private Integer col;
    private Double xmin;
    private Double xmax;
    private Double ymin;
    private Double ymax;
    private Double xminGcj;
    private Double xmaxGcj;
    private Double yminGcj;
    private Double ymaxGcj;
    private Boolean inRegion;
    private Boolean useful;
    private String shapeGeo;
    private Integer resultStatus;
    private String resultInfoCode;
    private String resultInfo;
    private String resultTraffic;
    /**
     * 行政区域信息，存储格式为json串
     */
    private String districtRegion;

    /**
     * 获取高德请求面信息参数
     */
    public String getRequestRectangle() {
        return xminGcj + "," + yminGcj + ";" + xmaxGcj + "," + ymaxGcj;
    }

    public String getESId() {
        return row + "A" + col;
    }
}
