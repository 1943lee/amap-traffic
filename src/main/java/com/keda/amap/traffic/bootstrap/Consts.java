package com.keda.amap.traffic.bootstrap;

import com.keda.amap.traffic.TrafficApplication;

import java.io.File;

/**
 * 常量
 * Created by liChenYu on 2018/8/28
 */
public interface Consts {
    String CLASSPATH = new File(TrafficApplication.class.getResource("/").getPath()).getPath() + File.separatorChar;

    String esIndex = ".xzqy_china";
    String esType = "china";
    String esChinaId = "1";

    String chinaEndPoint = "/" + esIndex + "/" + esType + "/" + esChinaId;

    /**
     * 高德api host url
     */
    String gdHost = "http://restapi.amap.com/";

    String gdBatchUrl = gdHost + "v3/batch?key=";

    String gdTrafficEndpoint = "/v3/traffic/status/rectangle?extensions=all&key=";
}
