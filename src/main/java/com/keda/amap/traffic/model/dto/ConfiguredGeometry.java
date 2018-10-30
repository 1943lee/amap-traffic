package com.keda.amap.traffic.model.dto;

import com.vividsolutions.jts.geom.Geometry;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 配置的Geometry，包含Geometry和行政区域信息
 * @author lcy
 * @date 2018/10/30
 */
@Data
@AllArgsConstructor
public class ConfiguredGeometry {
    private Geometry geometry;
    private String districtName;
    private String districtInnerCode;

    public String getDistrictInfo() {
        String districtCode = districtInnerCode.substring(districtInnerCode.lastIndexOf(".") + 1);
        return String.format("{\"XZQHBH\":\"%s\",\"XZQHMC\":\"%s\",\"XZQHNBBM\":\"%s\"}",
                districtCode, districtName, districtInnerCode);
    }
}
