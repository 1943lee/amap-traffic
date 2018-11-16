package com.keda.amap.traffic.service;

import org.quartz.*;
import org.springframework.stereotype.Service;

/**
 * @author lcy
 * @date 2018/11/16
 */
@Service
public class MyJobService implements IJobService {
    @Override
    public void addJob(Scheduler scheduler, String jobClassName, String jobGroupName, String cronExpression) throws Exception {
        //构建job信息
        JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName))
                .withIdentity(jobClassName, jobGroupName)
                .build();
        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder builder = CronScheduleBuilder.cronSchedule(cronExpression);
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(jobClassName, jobGroupName)
                .withSchedule(builder)
                .build();
        // 配置scheduler相关参数
        scheduler.scheduleJob(jobDetail, trigger);
    }

}
