package com.shuyan.demo3_trigger;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;

import static java.lang.Thread.sleep;

public class Myjob3 implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("-----------------------------------------------------");
        System.out.println(context.getJobDetail().getKey());
        System.out.println(context.getTrigger().getKey());
        System.out.println(context.getTrigger().getJobKey());
    }
}
