package com.shuyan.demo2_concurrency;

import org.quartz.*;

import static java.lang.Thread.sleep;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class Myjob2 implements Job {
    private int index0;

    public void setIndex0(int index0) {
        this.index0 = index0;
    }
    @Override
    public void execute(JobExecutionContext context) {
        System.out.println(context.getJobDetail().getKey() + " :" + index0);
        try {
            sleep(2000);
            index0 ++;
            //将新数据put到JobDetail的JobDataMap中之后，@PersistJobDataAfterExecution才能起作用
            context.getJobDetail().getJobDataMap().put("index0",index0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
