package com.shuyan.demo4_CronTrigger;

import com.shuyan.demo5_Listener.MyJobListener;
import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.DailyCalendar;

import static java.lang.Thread.sleep;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.EverythingMatcher.*;

public class test {

    @Test
    public void test1() throws SchedulerException, InterruptedException {

        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler sched = schedFact.getScheduler();
        sched.start();

        JobDetail job = newJob(Myjob4.class)
                .withIdentity("myJob", "group1")
                .build();

        Trigger trigger1 = newTrigger()
                .withIdentity("myTrigger1", "group1")
                .startNow()
                //每个月16号秒数是5的倍数时触发1次
                .withSchedule(cronSchedule("0/5 * 0-23 16 * ?"))
                .build();
/*
        Trigger trigger2 = newTrigger()
                .withIdentity("myTrigger2", "group1")
                .startNow()
                .withSchedule(dailyAtHourAndMinute(10, 42))
                .build();*/


        sched.scheduleJob(job, trigger1);
        sleep(100000);
        sched.shutdown(true);
    }
}
