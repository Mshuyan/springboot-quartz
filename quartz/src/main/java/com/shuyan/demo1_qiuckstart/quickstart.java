package com.shuyan.demo1_qiuckstart;

import org.junit.jupiter.api.Test;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import static java.lang.Thread.sleep;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class quickstart {

    @Test
    public void test1() throws SchedulerException, InterruptedException {

        /*
         * 获取Scheduler实例有2种方法：
         * 1. 获取StdSchedulerFactory类中默认的Scheduler实例
         * 2. 从1个新的StdSchedulerFactory类的实例中获取Scheduler实例
         */
        //Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        //或
        SchedulerFactory schedFact = new StdSchedulerFactory();
        Scheduler sched = schedFact.getScheduler();

        //启动任务调度
        sched.start();

        /*
         * 将Myjob类传入，获取JobDetail实例
         * withIdentity：
         *      name：为这个JobDetail实例取个名字
         *      group：将这个JobDetail实例放在当前Scheduler的哪个分组中
         */
        JobDetail job = newJob(Myjob1.class)
                .withIdentity("myJob", "group1")
                .build();

        JobKey jobKey = job.getKey();
        /*
         * 创建Trigger实例
         * withIdentity：
         *      name：为这个Trigger实例取个名字
         *      group：将这个Trigger实例放在当前Scheduler的哪个分组中
         * startNow：
         *      一旦加入scheduler立即启动定时器
         * withSchedule：
         *
         * simpleSchedule：
         *      withIntervalInSeconds：执行的时间间隔
         *      repeatForever：如何重复执行
         */
        Trigger trigger = newTrigger()
                .withIdentity("myTrigger", "group1")
                .usingJobData("username","hanmeimei")
                .usingJobData("age",16)
                .startNow()
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(5)
                        .repeatForever())
                .build();

        /*
         * 将job和trigger加入到sched，告诉sched使用这个定时器调度这个任务
         */
        sched.scheduleJob(job, trigger);

        //定时任务执行期间，主线程不能退出
        sleep(100000);

        /*
         * shutdown：
         *      作用：
         *          终止任务调度
         *      参数：
         *          waitForJobsToComplete：
         */
        sched.shutdown(true);
    }
}
