package com.exchange.schedule;

import com.exchange.enums.FuturesTypeEnum;
import com.exchange.service.CloseOutTimeService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * create by GYH on 2022/11/27
 */
@Component
public class CloseOutTimeTask implements Job {
    @Autowired
    private CloseOutTimeService closeOutTimeService;

    @Override
    public void execute(JobExecutionContext context) {
        closeOutTimeService.closeOut((FuturesTypeEnum) context.getMergedJobDataMap().get(CloseOutTimeJobInfo.typeKay));
    }
}
