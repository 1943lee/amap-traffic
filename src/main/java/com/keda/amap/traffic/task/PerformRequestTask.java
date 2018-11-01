package com.keda.amap.traffic.task;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.keda.amap.traffic.InitUseful;
import com.keda.amap.traffic.bootstrap.Consts;
import com.keda.amap.traffic.config.AmapConfig;
import com.keda.amap.traffic.model.amap.BatchRequest;
import com.keda.amap.traffic.model.amap.BatchResponse;
import com.keda.amap.traffic.model.amap.TrafficResponse;
import com.keda.amap.traffic.model.amap.TrafficResponse.Road;
import com.keda.amap.traffic.model.amap.TrafficResponse.TrafficInfo;
import com.keda.amap.traffic.model.entity.Parts;
import com.keda.amap.traffic.model.kafka.Message;
import com.keda.amap.traffic.service.amap.PartsService;
import com.keda.amap.traffic.service.amap.RequestService;
import com.keda.amap.traffic.service.kafka.ProduceService;
import com.keda.amap.traffic.service.spider.TrafficSpider;
import com.keda.amap.traffic.util.AsciiUtil;
import com.keda.amap.traffic.util.JtsUtil;
import io.github.biezhi.anima.page.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时发起请求类
 * @author lcy
 * @date 2018/10/30
 */
@Slf4j
@Component
@AutoConfigureAfter(InitUseful.class)
public class PerformRequestTask {

    private final AmapConfig amapConfig;
    private final PartsService partsService;
    private final RequestService requestService;
    private final TrafficSpider trafficSpider;
    private final ProduceService produceService;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Autowired
    public PerformRequestTask(AmapConfig amapConfig, PartsService partsService,
                              RequestService requestService, TrafficSpider trafficSpider,
                              ProduceService produceService) {
        this.amapConfig = amapConfig;
        this.partsService = partsService;
        this.requestService = requestService;
        this.trafficSpider = trafficSpider;
        this.produceService = produceService;
    }

    /**
     * 定时任务
     * 每天5点到23点，每15min执行
     */
    @Scheduled(cron = "0 */15 5-23 * * *")
    public void doRequest() throws InterruptedException {
        if (!Consts.INITIALIZED) {
            log.info("scheduling is waiting for initialization!");
            return;
        }

        log.info("Start Performing request");
        final String time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

        int size = 20;
        int page = 1;
        // 由于请求有QPS限制，此处需要进行限制，请求间隔控制在500ms，即每秒40次
        long interval = 500;
        while (true) {
            // 暂时使用同一个key，测试请求间隔
            String key = amapConfig.getApiKey();

            String batchUrl = Consts.gdBatchUrl + key;

            long start = System.currentTimeMillis();
            Page<Parts> partsPage = partsService.getUsefulParts(page++, size);

            List<Parts> rows = partsPage.getRows();
            if (null == rows) continue;
            BatchRequest batchRequest = requestService.getBatchRequestBody(key, rows);

            pushMessage(trafficSpider.batch(batchUrl, batchRequest), rows, time);

            long end = System.currentTimeMillis();
            if (end - start < interval) {
                Thread.sleep(interval - (end - start));
            }

            log.info("perform request, 本次查询{}, 已完成{}, 共{}",
                    rows.size(),
                    partsPage.isLastPage() ? partsPage.getTotalRows() : partsPage.getPageNum() * size,
                    partsPage.getTotalRows());

            if(partsPage.isLastPage()) {
                log.info("Finish Performing request, {} requests in total!", partsPage.getTotalRows());
                break;
            }
        }
    }

    private void pushMessage(List<BatchResponse<TrafficResponse>> batchResponses,
                             List<Parts> partsList, String time) {
        Runnable runnable = () -> {
            for (int i = 0; i < partsList.size(); i++) {
                Parts parts = partsList.get(i);
                TrafficResponse trafficResponse = batchResponses.get(i).getBody();
                String xxbh = parts.getESId();
                Message message = new Message();
                message.setXXBH(xxbh);
                message.setXXLB("ez_lkt_write");
                message.setXXNR(generateTrafficESMessage(parts, trafficResponse, time));
                message.setCZSJ(new Date());

                produceService.sendMessage(xxbh, gson.toJson(message));
            }
        };
        executorService.execute(runnable);
    }

    private String generateTrafficESMessage(Parts parts, TrafficResponse trafficResponse, String time) {
        JSONObject rootMsg = new JSONObject();
        // 主键采用'行号#列号'格式，保证唯一
        rootMsg.put("WGBH", parts.getESId());
        rootMsg.put("WGHH", parts.getRow());
        rootMsg.put("WGLH", parts.getCol());
        rootMsg.put("WGMC", "");
        rootMsg.put("LKSJ", time);
        rootMsg.put("LKNR", AsciiUtil.sbc2dbcCase(getSimplifiedRoad(trafficResponse.getTrafficInfo())));
        rootMsg.put("SZDXZQH", JSONObject.parseObject(parts.getDistrictRegion()));
        rootMsg.put("SHAPE", JSONObject.parseObject(parts.getShapeGeo()));
        rootMsg.put("GXSJ", time);

        return rootMsg.toJSONString();
    }

    private String getSimplifiedRoad(TrafficInfo trafficInfo) {
        List<Road> roads = trafficInfo.getRoads();
        if (roads == null || roads.size() == 0) return "";

        for (Road road : roads) {
            String polyline = road.getPolyline();
            if (null == polyline || polyline.isEmpty()) continue;
            road.setPolyline(JtsUtil.simplify(polyline, 0.0001));
        }

        return JSONObject.toJSONString(trafficInfo);
    }
}
