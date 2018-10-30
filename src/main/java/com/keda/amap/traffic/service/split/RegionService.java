package com.keda.amap.traffic.service.split;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.keda.amap.traffic.bootstrap.Consts;
import com.keda.amap.traffic.config.SourceConfig;
import com.keda.amap.traffic.model.dto.ConfiguredGeometry;
import com.keda.amap.traffic.model.dto.DistrictRegion;
import com.keda.amap.traffic.model.entity.Parts;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.io.WKTReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liChenYu on 2018/9/5
 */
@Slf4j
@Service
public class RegionService {

    private final SourceConfig sourceConfig;

    private GeometryFactory geometryFactory = new GeometryFactory();

    private List<ConfiguredGeometry> regionGeometryList = new ArrayList<>();

    @Autowired
    public RegionService(SourceConfig sourceConfig) {
        this.sourceConfig = sourceConfig;
    }

    /**
     * 初始化目标区域Geometry
     * @throws Exception 文件解析出错
     */
    public void initGeometry() throws Exception {
        String jsonFilePath = Consts.CLASSPATH + "regionWkt" + File.separator + sourceConfig.getFolder();
        File folder = new File(jsonFilePath);
        initGeometry(folder);
    }

    /**
     * 递归遍历配置目录下所有文件，获取wkt文本
     * @param directory 目录
     * @throws Exception 解析异常
     */
    private void initGeometry(File directory) throws Exception {
        if(directory.exists() && directory.isDirectory() && null != directory.listFiles()) {
            for(File file : directory.listFiles()) {
                if (file.isFile()) {
                    String regionWkt = new String(Files.readAllBytes(file.toPath()));
                    WKTReader reader = new WKTReader(geometryFactory);
                    Geometry geometry = reader.read(regionWkt);

                    regionGeometryList.add(getRegionInfo(file, geometry));
                }
                else if (file.isDirectory()) {
                    initGeometry(file);
                }
            }
        }
    }

    private ConfiguredGeometry getRegionInfo(File file, Geometry geometry) throws Exception {
        for (DistrictRegion districtRegion : sourceConfig.getSubFiles()) {
            if (file.getName().equals(districtRegion.getName() + ".wkt")) {
                return new ConfiguredGeometry(geometry, districtRegion.getName(), districtRegion.getInnerCode());
            }
        }
        throw new Exception("wkt文件对应的行政区域信息配置有误！");
    }

    /**
     * 设置行政区域信息，若不在区域内，返回false
     */
    public boolean setRegionInfo(Parts parts, double xmin, double ymin, double xmax, double ymax) {
        Polygon polygon = getPolygon(xmin, ymin, xmax, ymax);
        boolean inRegion = false;
        JSONArray districtInfoList = new JSONArray();
        for (ConfiguredGeometry region : regionGeometryList) {
            if(polygon.intersects(region.getGeometry())) {
                inRegion = true;
                districtInfoList.add(JSONObject.parseObject(region.getDistrictInfo()));
            }
        }
        parts.setDistrictRegion(inRegion ?
                districtInfoList.size() > 1 ? districtInfoList.toJSONString() : districtInfoList.get(0).toString()
                : null);
        parts.setInRegion(inRegion);
        return inRegion;
    }

    private Polygon getPolygon(double xmin, double ymin, double xmax, double ymax) {
        Coordinate c1 = new Coordinate(xmin, ymin);
        Coordinate c2 = new Coordinate(xmin, ymax);
        Coordinate c3 = new Coordinate(xmax, ymax);
        Coordinate c4 = new Coordinate(xmax, ymin);

        Coordinate[] coordinates = new Coordinate[] {c1,c2,c3,c4, c1};
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(coordinates, 2), geometryFactory);
        return new Polygon(linearRing, null, geometryFactory);
    }

    public String getPartGeoShape(double xmin, double ymin, double xmax, double ymax) {
        JSONObject shape = new JSONObject();
        shape.put("type", "polygon");
        JSONArray coordinates = new JSONArray();
        JSONArray polygon1 = new JSONArray();
        polygon1.add(getGeoShapeCoord(xmin,ymin));
        polygon1.add(getGeoShapeCoord(xmin,ymax));
        polygon1.add(getGeoShapeCoord(xmax,ymax));
        polygon1.add(getGeoShapeCoord(xmax,ymin));
        polygon1.add(getGeoShapeCoord(xmin,ymin));
        coordinates.add(polygon1);
        shape.put("coordinates", coordinates);
        return shape.toJSONString();
    }

    private JSONArray getGeoShapeCoord(double lng, double lat) {
        JSONArray coord = new JSONArray();
        coord.add(lng);
        coord.add(lat);
        return coord;
    }
}
