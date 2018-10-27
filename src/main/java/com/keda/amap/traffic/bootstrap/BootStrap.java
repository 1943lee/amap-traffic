package com.keda.amap.traffic.bootstrap;

import com.keda.amap.traffic.config.SourceConfig;
import com.keda.amap.traffic.service.split.RegionService;
import com.keda.amap.traffic.service.split.SplitService;
import io.github.biezhi.anima.Anima;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动类
 * Created by liChenYu on 2018/9/4
 */
@Slf4j
@Component
@Order(1)
public class BootStrap implements CommandLineRunner {
    @Autowired
    SplitService splitService;

    @Autowired
    RegionService regionService;

    @Autowired
    SourceConfig sourceConfig;

    @Override
    public void run(String... args) throws Exception {
        // 处理数据库相关
        SqliteJdbc.importSql(sourceConfig.getFolder());
        Anima.open(SqliteJdbc.DB_SRC);
        Anima.me().getSql2o().setIsolationLevel(java.sql.Connection.TRANSACTION_SERIALIZABLE);

        // 初始化目标区域Geometry
        regionService.initGeometry();

        // 切分目标范围
        splitService.init();

        log.info("Initialization finished !");
    }
}
