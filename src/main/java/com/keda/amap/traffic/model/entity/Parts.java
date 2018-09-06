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
    private Double xminMer;
    private Double xmaxMer;
    private Double yminMer;
    private Double ymaxMer;
    private Boolean inRegion;
    private Boolean useful;
    private String shapeGeo;
    private Boolean resultStatus;
    private String resultInfoCode;
}
