package com.shuyan.demo1_qiuckstart;

import org.quartz.*;

public class Myjob1 implements Job {
    private String username;
    private int age;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobKey jobKey = context.getJobDetail().getKey();
        TriggerKey triggerKey = context.getTrigger().getKey();
        System.out.println("username: " + username);
        System.out.println("age: " + age);

    }
}
