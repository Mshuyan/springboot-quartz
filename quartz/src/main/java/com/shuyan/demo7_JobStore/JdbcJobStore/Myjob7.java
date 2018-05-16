package com.shuyan.demo7_JobStore.JdbcJobStore;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class Myjob7 implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("-----------------------------------------------------");
        System.out.println(context.getJobDetail().getKey());
        System.out.println(context.getTrigger().getKey());
        System.out.println(context.getTrigger().getJobKey());
    }
}
