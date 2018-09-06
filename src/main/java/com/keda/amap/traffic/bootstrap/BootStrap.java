package com.keda.amap.traffic.bootstrap;

import com.keda.amap.traffic.service.split.RegionService;
import com.keda.amap.traffic.service.split.SplitService;
import io.github.biezhi.anima.Anima;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动类
 * Created by liChenYu on 2018/9/4
 */
@Component
@Order(1)
public class BootStrap implements CommandLineRunner {
    @Autowired
    SplitService splitService;

    @Autowired
    RegionService regionService;

    @Override
    public void run(String... args) throws Exception {
        // 处理数据库相关
        SqliteJdbc.importSql();
        Anima.open(SqliteJdbc.DB_SRC);
        Anima.me().getSql2o().setIsolationLevel(java.sql.Connection.TRANSACTION_SERIALIZABLE);

        // 初始化目标区域Geometry
        regionService.initGeometry();

        // 切分目标范围
        splitService.init();
    }
}
