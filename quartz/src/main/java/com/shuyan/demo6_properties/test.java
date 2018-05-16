package com.shuyan.demo6_properties;

import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static java.lang.Thread.sleep;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class test {

    @Test
    public void test1() throws SchedulerException, InterruptedException {

        SchedulerFactory schedFact0 = new StdSchedulerFactory("properties/quartz.properties");
        //或
        SchedulerFactory schedFact1 = new StdSchedulerFactory();
        ((StdSchedulerFactory) schedFact1).initialize("properties/quartz.properties");
        Scheduler sched = schedFact1.getScheduler();
        System.out.println(sched.getSchedulerName());
        sched.start();

        JobDetail job = newJob(Myjob4.class)
                .withIdentity("myJob", "group1")
                .build();

        Trigger trigger1 = newTrigger()
                .withIdentity("myTrigger1", "group1")
                .startNow()
                .withSchedule(cronSchedule("0/5 * 0-23 16 * ?"))
                .build();

        sched.scheduleJob(job, trigger1);
        sleep(100000);
        sched.shutdown(true);
    }
}
