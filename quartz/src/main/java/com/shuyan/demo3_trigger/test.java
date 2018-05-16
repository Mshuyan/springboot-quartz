package com.shuyan.demo3_trigger;

import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.calendar.DailyCalendar;
import org.quartz.impl.calendar.HolidayCalendar;

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

        //声名 DailyCalendar
        DailyCalendar dailyCalendar = new DailyCalendar("13:06:00", "13:08:00");
        // 时间反转，为true表示只有这次时间段才会被执行，为false表示排除这时间段
        dailyCalendar.setInvertTimeRange(true);
        // 添加到scheduler
        sched.addCalendar("dailyCalendar", dailyCalendar, false, false);

        JobDetail job = newJob(Myjob3.class)
                .withIdentity("myJob", "group1")
                .build();

        Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .modifiedByCalendar("dailyCalendar")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(1)
                        .repeatForever())
                .build();

        sched.scheduleJob(job, trigger);
        sleep(100000);
        sched.shutdown(true);
    }
}
