package com.keda.amap.traffic.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import java.util.ArrayList;
import java.util.List;

/**
 * jts util
 * @author lcy
 * @date 2018/11/1
 */
public class JtsUtil {
    private static GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * 简化
     * @param polyline 线段的点集，点之间；隔开，经纬度，隔开
     * @param distanceTolerance 简化阈值，已输入为单位，若为gps坐标，即小数点多少位不同，越小越精确
     * @return 简化后的点集
     */
    public static String simplify(String polyline, double distanceTolerance) {
        String[] points = polyline.split(";");
        Coordinate[] coordinates = new Coordinate[points.length];
        for (int i = 0; i < points.length; i++) {
            String point = points[i];
            String[] var1 = point.split(",");
            if (var1.length == 2) {
                double lng = Double.parseDouble(var1[0]);
                double lat = Double.parseDouble(var1[1]);
                coordinates[i] = new Coordinate(lng, lat);
            }
        }
        CoordinateSequence coordinateSequence = new CoordinateArraySequence(coordinates, 2);
        LineString lineString = new LineString(coordinateSequence, geometryFactory);
        LineString simplifiedLineString = (LineString) DouglasPeuckerSimplifier.simplify(lineString, distanceTolerance);

        return getLineStringStr(simplifiedLineString);
    }

    private static String getLineStringStr(LineString lineString) {
        Coordinate[] coordinates = lineString.getCoordinates();
        List<String> points = new ArrayList<>(coordinates.length);
        for (Coordinate coordinate : coordinates) {
            String point = coordinate.x + "," + coordinate.y;
            points.add(point);
        }
        return String.join(";", points);
    }
}
