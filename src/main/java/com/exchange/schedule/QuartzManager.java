package com.exchange.schedule;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * create by GYH on 2022/11/27
 */
@Slf4j
@Component
public class QuartzManager {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 添加任务，使用任务组名，触发器名，触发器组名
     * 如果任务已存在则更新任务的触发器
     */
    public void addJob(ScheduleJobInfo info) {
        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            var jobKey = JobKey.jobKey(info.getJobName(), info.getGroupName());
            var cronScheduleBuilder = CronScheduleBuilder.cronSchedule(info.getCron());
            var cronTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(info.getJobName(), info.getGroupName())
                    .withSchedule(cronScheduleBuilder)
                    .build();

            if (!scheduler.checkExists(jobKey)) {
                var jobDetail = JobBuilder.newJob(info.getClassName())
                        .withIdentity(info.getJobName(), info.getGroupName())
                        .usingJobData(info.getData())
                        .build();
                log.info("添加定时任务 {} - {} - {}", info.getCron(), info.getJobName(), info.getGroupName());
                scheduler.scheduleJob(jobDetail, cronTrigger);
            } else {
                log.info("{}， {} 定时任务已经存在，只修改时间", info.getJobName(), info.getGroupName());
                var triggerKey = TriggerKey.triggerKey(info.getJobName(), info.getGroupName());
                scheduler.rescheduleJob(triggerKey, cronTrigger);
                //scheduler.resumeTrigger(triggerKey)
            }
        } catch (SchedulerException e) {
            log.error("添加失败", e);
        }
    }

    /**
     * 暂停任务
     */
    public void pauseJob(ScheduleJobInfo info) {
        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            var jobKey = JobKey.jobKey(info.getJobName(), info.getGroupName());
            scheduler.pauseJob(jobKey);
            log.info("==pause job: {} success=", info.getJobName());
        } catch (SchedulerException e) {
            log.error("暂停任务失败", e);
        }
    }

    /**
     * 恢复任务
     */
    public void resumeJob(ScheduleJobInfo info) {
        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            var jobKey = JobKey.jobKey(info.getJobName(), info.getGroupName());
            scheduler.resumeJob(jobKey);
            log.info("==resume job: {} success=", info.getJobName());
        } catch (SchedulerException e) {
            log.error("恢复任务失败", e);
        }
    }

    /**
     * 删除任务
     */
    public Boolean removeJob(ScheduleJobInfo info) {
        return removeJob(info.getGroupName(), info.getJobName());
    }

    /**
     * 删除任务
     */
    public boolean removeJob(String groupName, String jobName) {
        var result = true;
        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            var jobKey = JobKey.jobKey(jobName, groupName);
            if (scheduler.checkExists(jobKey)) {
                result = scheduler.deleteJob(jobKey);
            }
            log.info("==remove job: {} {}=", jobName, result);
        } catch (SchedulerException e) {
            log.error("删除任务失败 {}", jobName, e);
            result = false;
        }
        return result;
    }

    /**
     * 修改任务：删除老任务，添加新任务
     * 如果老任务不存在则直接添加新任务
     * 如果新任务已存在就跟新任务执行周期
     */
    public void modifyJob(ScheduleJobInfo info, String oldGroup, String oldName) {
        removeJob(oldGroup, oldName);
        addJob(info);
    }

    /**
     * 修改定时任务的时间
     */
    public Boolean modifyJobTime(ScheduleJobInfo info) {
        var result = false;
        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            var triggerKey = TriggerKey.triggerKey(info.getJobName(), info.getGroupName());
            var trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            var oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(info.getCron())) {
                var cronScheduleBuilder = CronScheduleBuilder.cronSchedule(info.getCron());
                var ct = TriggerBuilder
                        .newTrigger()
                        .withIdentity(info.getJobName(), info.getGroupName())
                        .withSchedule(cronScheduleBuilder)
                        .build();

                scheduler.rescheduleJob(triggerKey, ct);
                scheduler.resumeTrigger(triggerKey);
                result = true;
            }
        } catch (SchedulerException e) {
            log.error("修改定时任务时间失败", e);
        }
        return result;
    }

    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        try {
            var scheduler = schedulerFactoryBean.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("", e);
        }
    }
}
