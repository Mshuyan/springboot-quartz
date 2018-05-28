package com.shuyan.springbootquartz.demo1;

import lombok.Data;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;

@Data
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MyJob extends QuartzJobBean {
    private int index;
    static int cnt = 0;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        index ++;
        System.out.println("index : " + index);
        context.getJobDetail().getJobDataMap().put("index", index);
/*        cnt ++;
        if(cnt == 5){
            try {
                context.getScheduler().shutdown(true);
            } catch (SchedulerException e) {
                e.printStackTrace();
            }
        }*/
    }
}
