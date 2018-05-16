package com.shuyan.demo4_CronTrigger;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class Myjob4 implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("-----------------------------------------------------");
        System.out.println(context.getJobDetail().getKey());
        System.out.println(context.getTrigger().getKey());
        System.out.println(context.getTrigger().getJobKey());
    }
}
