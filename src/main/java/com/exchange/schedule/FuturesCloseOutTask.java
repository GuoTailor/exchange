package com.exchange.schedule;

import com.exchange.service.FuturesService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * create by GYH on 2022/11/27
 */
@Component
public class FuturesCloseOutTask implements Job {
    @Autowired
    private FuturesService futuresService;

    @Override
    public void execute(JobExecutionContext context) {
        futuresService.closeOut((Integer) context.getMergedJobDataMap().get(FuturesCloseOutJobInfo.idKey));
    }
}
