package com.exchange.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;

/**
 * create by GYH on 2022/11/27
 */
public interface ScheduleJobInfo {
    String getCron();

    Class<? extends Job> getClassName();  // 定时任务执行类

    JobDataMap getData(); // 要传入的数据

    String getJobName();     // 任务job的名称

    String getGroupName();  // 任务group的名称
}
