package com.exchange.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;

import java.time.LocalTime;
import java.util.Map;

/**
 * create by GYH on 2022/11/30
 */
public class FuturesCloseOutJobInfo implements ScheduleJobInfo {
    public static final String idKey = "id";
    private final String cron;
    private final Class<? extends Job> className = FuturesCloseOutTask.class;
    private final JobDataMap jobDataMap;
    private final String jobName;
    private final String groupName = "FuturesCloseOut";

    public FuturesCloseOutJobInfo(Integer id, LocalTime localTime) {
        this.jobName = id.toString();
        cron = String.format("%d %d %d * * ?", localTime.getSecond(), localTime.getMinute(), localTime.getHour());
        jobDataMap = new JobDataMap(Map.of(idKey, id));
    }

    public FuturesCloseOutJobInfo(Integer id) {
        this.jobName = id.toString();
        cron = null;
        jobDataMap = null;
    }

    @Override
    public String getCron() {
        return cron;
    }

    @Override
    public Class<? extends Job> getClassName() {
        return className;
    }

    @Override
    public JobDataMap getData() {
        return jobDataMap;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    @Override
    public String getGroupName() {
        return groupName;
    }
}
