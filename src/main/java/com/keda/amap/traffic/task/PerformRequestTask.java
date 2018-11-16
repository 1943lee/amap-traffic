package com.keda.amap.traffic.task;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

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
@DisallowConcurrentExecution
public class PerformRequestTask implements Job {
    /**
     * 在Quartz Job中注入bean，需使用属性注入形式，不可使用构造函数注入
     */
    @Autowired
    private AmapConfig amapConfig;
    @Autowired
    PartsService partsService;
    @Autowired
    RequestService requestService;
    @Autowired
    TrafficSpider trafficSpider;
    @Autowired
    ProduceService produceService;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        doRequest();
    }

    private void doRequest() {
        if (!Consts.INITIALIZED) {
            log.info("scheduling is waiting for initialization!");
            return;
        }

        log.info("Start Performing request");
        final String time = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

        Integer[] level = amapConfig.getLevel();
        if (null != level && level.length > 0) {
            for (Integer i : level) {
                doRequest(time, i);
            }
        }
        else {
            doRequest(time, 5);
        }
    }

    /**
     * 发起请求-由于存在QPS限制，单ak时需要同步处理
     * @param time 请求开始时间 用于统一路况时间
     * @param level 道路级别，默认 5
     */
    private void doRequest(String time, int level) {
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
            BatchRequest batchRequest = requestService.getBatchRequestBody(key, rows, level);

            pushMessage(trafficSpider.batch(batchUrl, batchRequest), rows, time, level);

            long end = System.currentTimeMillis();
            if (end - start < interval) {
                try {
                    Thread.sleep(interval - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.info("perform request, level = {}, 本次查询{}, 已完成{}, 共{}", level,
                    rows.size(),
                    partsPage.isLastPage() ? partsPage.getTotalRows() : partsPage.getPageNum() * size,
                    partsPage.getTotalRows());

            if(partsPage.isLastPage()) {
                break;
            }
        }
    }

    private void pushMessage(List<BatchResponse<TrafficResponse>> batchResponses,
                             List<Parts> partsList, String time, int level) {
        Runnable runnable = () -> {
            for (int i = 0; i < partsList.size(); i++) {
                Parts parts = partsList.get(i);
                TrafficResponse trafficResponse = batchResponses.get(i).getBody();
                String xxbh = parts.getESId(level);
                Message message = new Message();
                message.setXXBH(xxbh);
                message.setXXLB("ez_lkt_write");
                message.setXXNR(generateTrafficESMessage(parts, trafficResponse, time, level));
                message.setCZSJ(new Date());

                produceService.sendMessage(xxbh, gson.toJson(message));
            }
        };
        executorService.execute(runnable);
    }

    private String generateTrafficESMessage(Parts parts, TrafficResponse trafficResponse, String time, int level) {
        JSONObject rootMsg = new JSONObject();
        // 主键采用'行号A列号'格式，保证唯一
        rootMsg.put("WGBH", parts.getObjectId());
        // 道路级别，根据传入数据显示
        rootMsg.put("DLJB", level);
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
        if (null == trafficInfo) return "";
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
