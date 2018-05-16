package com.shuyan.demo2_concurrency;

import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static java.lang.Thread.sleep;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class test {

    @Test
    public void test1() throws SchedulerException, InterruptedException {

        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler sched = schedFact.getScheduler();
        sched.start();

        JobDetail job = newJob(Myjob2.class)
                .withIdentity("myJob", "group1")
                .usingJobData("index0",0)
                //设置RequestsRecover
                .requestRecovery(true)
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever())
                .build();

        sched.scheduleJob(job, trigger);
        sleep(100000);
        sched.shutdown(true);
    }
}
