package com.keda.amap.traffic.task;

import com.keda.amap.traffic.InitUseful;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时发起请求类
 * @author lcy
 * @date 2018/10/30
 */
@Slf4j
@Component
@AutoConfigureAfter(InitUseful.class)
public class PerformRequestTask {

    /**
     * 定时任务
     * 每天7点到23点，每15min执行
     */
    @Scheduled(cron = "0 */15 7-23 * * *")
    public void doRequest() {
        log.info("Start Performing request");
        //ToDo: 定时请求

    }

}
