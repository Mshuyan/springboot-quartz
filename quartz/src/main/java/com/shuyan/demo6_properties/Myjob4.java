package com.shuyan.demo6_properties;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

public class Myjob4 implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("tes");
    }
}
