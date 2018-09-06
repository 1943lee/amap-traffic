package com.keda.amap.traffic.util;

/**
 * geo数据转换类
 *
 * Created by liChenYu on 2018/9/4
 */
public class GeoConvert {
    // π
    private static double pi = 3.1415926535897932384626;
    // 长半轴
    private static double a = 6378245.0;
    // 扁率
    private static double ee = 0.00669342162296594323;

    /**
     * wgs84坐标转web墨卡托投影坐标
     *
     * @param lon 经度
     * @param lat 纬度
     * @return 经度、纬度数组
     */
    public static double[] wgs84ToWebMercator(double lon, double lat)
    {
        double x = lon * 20037508.34 / 180;
        double y = Math.log(Math.tan((90 + lat) * pi / 360)) / (pi / 180);
        y = y * 20037508.34 / 180;
        return new double[] { x, y };
    }

    /**
     * web墨卡托投影坐标转wgs84坐标
     *
     * @param x 经度
     * @param y 纬度
     * @return 经度、纬度数组
     */
    public static double[] webMercatorToWgs84(double x, double y)
    {
        double lon = (x / 20037508.34) * 180;
        double lat = (y / 20037508.34) * 180;

        lat = 180 / pi * (2 * Math.atan(Math.exp(lat * pi / 180)) - pi / 2);
        return new double[] { lon, lat };
    }
    
    /**
     * WGS84转GCJ02(火星坐标系)
     *
     * @param lng WGS84坐标系的经度
     * @param lat WGS84坐标系的纬度
     * @return 火星坐标数组
     */
    public static double[] wgs84togcj02(double lng, double lat) {
        if (out_of_china(lng, lat)) {
            return new double[] { lng, lat };
        }
        double dlat = transformlat(lng - 105.0, lat - 35.0);
        double dlng = transformlng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * pi;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * pi);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * pi);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        return new double[] { mglng, mglat };
    }

    /**
     * GCJ02(火星坐标系)转GPS84
     *
     * @param lng 火星坐标系的经度
     * @param lat 火星坐标系纬度
     * @return WGS84坐标数组
     */
    public static double[] gcj02towgs84(double lng, double lat) {
        if (out_of_china(lng, lat)) {
            return new double[] { lng, lat };
        }
        double dlat = transformlat(lng - 105.0, lat - 35.0);
        double dlng = transformlng(lng - 105.0, lat - 35.0);
        double radlat = lat / 180.0 * pi;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * pi);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * pi);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        return new double[] { lng * 2 - mglng, lat * 2 - mglat };
    }

    /**
     * 纬度转换
     */
    private static double transformlat(double lng, double lat) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * pi) + 40.0 * Math.sin(lat / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * pi) + 320 * Math.sin(lat * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 经度转换
     */
    private static double transformlng(double lng, double lat) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * pi) + 20.0 * Math.sin(2.0 * lng * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * pi) + 40.0 * Math.sin(lng / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * pi) + 300.0 * Math.sin(lng / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 判断是否在国内，不在国内不做偏移
     */
    private static boolean out_of_china(double lng, double lat) {
        if (lng < 72.004 || lng > 137.8347) {
            return true;
        } else {
            return lat < 0.8293 || lat > 55.8271;
        }
    }
}
