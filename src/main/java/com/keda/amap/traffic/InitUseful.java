package com.keda.amap.traffic;

import com.keda.amap.traffic.bootstrap.Consts;
import com.keda.amap.traffic.config.AmapConfig;
import com.keda.amap.traffic.model.amap.BatchRequest;
import com.keda.amap.traffic.model.entity.Parts;
import com.keda.amap.traffic.service.amap.PartsService;
import com.keda.amap.traffic.service.amap.RequestService;
import com.keda.amap.traffic.service.spider.TrafficSpider;
import io.github.biezhi.anima.page.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 初始化useful矩形区域
 * Created by liChenYu on 2018/9/6
 */
@Slf4j
@Order(2)
@Component
public class InitUseful implements CommandLineRunner {
    private final PartsService partsService;
    private final AmapConfig amapConfig;
    private final RequestService requestService;
    private final TrafficSpider trafficSpider;

    @Autowired
    public InitUseful(PartsService partsService, AmapConfig amapConfig,
                      RequestService requestService, TrafficSpider trafficSpider) {
        this.partsService = partsService;
        this.amapConfig = amapConfig;
        this.requestService = requestService;
        this.trafficSpider = trafficSpider;
    }

    @Override
    public void run(String... args) throws Exception {
        if (Files.exists(Paths.get(Consts.CLASSPATH + "initialize.lock"))) {
            log.info("useful parts has been initialized！");
            Consts.INITIALIZED = true;
            return;
        }

        // 暂时使用同一个key，测试请求间隔
        String key = amapConfig.getApiKey();

        String batchUrl = Consts.gdBatchUrl + key;

        int size = 20;
        int page = 1;
        // 由于请求有QPS限制，此处需要进行限制，请求间隔控制在500ms，即每秒40次
        long interval = 500;
        while (true) {
            long start = System.currentTimeMillis();
            Page<Parts> partsPage = partsService.getInRegionParts(page++, size);

            List<Parts> rows = partsPage.getRows();
            if (null == rows) continue;
            BatchRequest batchRequest = requestService.getBatchRequestBody(key, rows);

            log.info("perform batch request, [{}]", batchUrl);
            trafficSpider.batch(batchUrl, batchRequest, rows);

            long end = System.currentTimeMillis();
            if (end - start < interval) {
                Thread.sleep(interval - (end - start));
            }

            log.info("perform request, 本次查询{}, 已完成{}, 共{}",
                    rows.size(),
                    partsPage.isLastPage() ? partsPage.getTotalRows() : partsPage.getPageNum() * size,
                    partsPage.getTotalRows());

            if(partsPage.isLastPage()) {
                File lock = new File(Consts.CLASSPATH + "initialize.lock");
                lock.createNewFile();
                log.info("initializing is initialized, {} requests in total!", partsPage.getTotalRows());
                Consts.INITIALIZED = true;
                break;
            }
        }
    }
}
