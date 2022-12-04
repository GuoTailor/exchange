package com.exchange.schedule;

import com.exchange.enums.FuturesTypeEnum;
import org.quartz.Job;
import org.quartz.JobDataMap;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

/**
 * create by GYH on 2022/11/27
 */
public class CloseOutTimeJobInfo implements ScheduleJobInfo {
    public static final String typeKay = "type";
    private final String cron;
    private final Class<? extends Job> className = CloseOutTimeTask.class;
    private final JobDataMap jobDataMap;
    private final String jobName;
    private final String groupName = "CloseOutTime";

    public CloseOutTimeJobInfo(String jobName, Duration time, FuturesTypeEnum futuresType) {
        this.jobName = jobName;
        LocalTime localTime = LocalTime.ofNanoOfDay(time.toNanos());
        cron = String.format("%d %d %d * * ?", localTime.getSecond(), localTime.getMinute(), localTime.getHour());
        jobDataMap = new JobDataMap(Map.of(typeKay, futuresType));
    }

    public CloseOutTimeJobInfo(String jobName) {
        this.jobName = jobName;
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
