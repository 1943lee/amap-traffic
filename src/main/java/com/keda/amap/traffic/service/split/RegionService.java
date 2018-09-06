package com.keda.amap.traffic.service.split;

import com.keda.amap.traffic.bootstrap.Consts;
import com.keda.amap.traffic.config.SourceConfig;
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

    @Autowired
    SourceConfig sourceConfig;

    private GeometryFactory geometryFactory = new GeometryFactory();

    private List<Geometry> regionGeometryList = new ArrayList<>();

    /**
     * 初始化目标区域Geometry
     * @throws Exception 文件解析出错
     */
    public void initGeometry() throws Exception {
        String jsonFilePath = Consts.CLASSPATH + "regionWkt" + File.separator + sourceConfig.getFolder();
        File folder = new File(jsonFilePath);
        if(folder.exists() && folder.isDirectory() && null != folder.listFiles()) {
            for(File file : folder.listFiles()) {
                String regionWkt = new String(Files.readAllBytes(file.toPath()));
                WKTReader reader = new WKTReader(geometryFactory);
                regionGeometryList.add(reader.read(regionWkt));
            }
        }
    }

    /**
     * 判断给定矩形是否在目标区域内（相交）
     */
    public boolean inRegion(double xmin, double ymin, double xmax, double ymax) {
        Coordinate c1 = new Coordinate(xmin, ymin);
        Coordinate c2 = new Coordinate(xmin, ymax);
        Coordinate c3 = new Coordinate(xmax, ymax);
        Coordinate c4 = new Coordinate(xmax, ymin);

        Coordinate[] coordinates = new Coordinate[] {c1,c2,c3,c4, c1};
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(coordinates, 2), geometryFactory);
        Polygon rectangle = new Polygon(linearRing, null, geometryFactory);

        return inRegion(rectangle);
    }

    private boolean inRegion(Geometry geometry) {
        for (Geometry region : regionGeometryList) {
            if(geometry.intersects(region)) {
                return true;
            }
        }
        return false;
    }
}
