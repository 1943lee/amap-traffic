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

    private final SourceConfig sourceConfig;

    private GeometryFactory geometryFactory = new GeometryFactory();

    private List<Geometry> regionGeometryList = new ArrayList<>();

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
                    regionGeometryList.add(reader.read(regionWkt));
                }
                else if (file.isDirectory()) {
                    initGeometry(file);
                }
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
