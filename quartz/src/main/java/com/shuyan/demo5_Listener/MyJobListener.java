package com.shuyan.demo5_Listener;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.listeners.JobListenerSupport;

public class MyJobListener extends JobListenerSupport {
    @Override
    public String getName() {
        return null;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        super.jobToBeExecuted(context);
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        super.jobExecutionVetoed(context);
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        super.jobWasExecuted(context, jobException);
    }
}
