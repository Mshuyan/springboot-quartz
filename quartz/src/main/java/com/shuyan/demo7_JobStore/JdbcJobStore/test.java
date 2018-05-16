package com.shuyan.demo7_JobStore.JdbcJobStore;

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

        SchedulerFactory schedFact = new StdSchedulerFactory("properties/quartz.properties");
        Scheduler sched = schedFact.getScheduler();
        sched.start();

/*        JobDetail job = newJob(Myjob7.class)
                .withIdentity("myJob", "group1")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("myTrigger1", "group1")
                .startNow()
                //每个月16号秒数是5的倍数时触发1次
                .withSchedule(cronSchedule("0/5 * 0-23 16 * ?"))
                .build();

        sched.scheduleJob(job, trigger);*/
        sleep(100000);
        sched.shutdown(true);
    }
}
