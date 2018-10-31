package com.keda.amap.traffic.bootstrap;

import com.keda.amap.traffic.TrafficApplication;

import java.io.File;

/**
 * 常量
 * Created by liChenYu on 2018/8/28
 */
public class Consts {
    public static final String CLASSPATH =
            new File(TrafficApplication.class.getResource("/").getPath()).getPath() + File.separatorChar;

    public static Boolean INITIALIZED = false;

    /**
     * 高德api host url
     */
    public static final String gdHost = "http://restapi.amap.com/";

    public static final String gdBatchUrl = gdHost + "v3/batch?key=";

    public static final String gdTrafficEndpoint = "/v3/traffic/status/rectangle?extensions=all&key=";
}
