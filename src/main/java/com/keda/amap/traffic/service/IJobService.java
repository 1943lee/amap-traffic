package com.keda.amap.traffic.service;

import org.quartz.Scheduler;

/**
 * job interface
 * @author lcy
 * @date 2018/11/16
 */
public interface IJobService {
    void addJob(Scheduler scheduler, String jobClassName, String jobGroupName, String cronExpression) throws Exception;
}
