package com.shuyan.springbootquartz.demo1;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Configuration
public class MyTimingTask {

    @Bean
    public Trigger getTrigger(){
        return newTrigger()
                .forJob(getJobDetail())
                .withIdentity("myTrigger1", "group1")
                .startNow()
                .withSchedule(cronSchedule("0/5 * * * * ?"))
                .build();
    }

    @Bean
    public JobDetail getJobDetail(){
        return newJob(MyJob.class)
                .storeDurably(true)
                .withIdentity("myJob", "group1")
                .usingJobData("index",0)
                .build();
    }
}
