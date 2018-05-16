package com.shuyan.demo1_qiuckstart;

import org.quartz.*;

public class Myjob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        //获取调用当前job任务的JobDetail实例的key
        JobKey key = context.getJobDetail().getKey();

        //获取调用当前job任务的JobDetail实例的JobDataMap对象
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        //获取JobDataMap对象中的数据
        String username = dataMap.getString("username");
        int age = dataMap.getInt("age");
        //打印
        System.out.println("key: " + key);
        System.out.println("username: " + username);
        System.out.println("age: " + age);

        //获取调用当前job任务的Trigger实例的JobDataMap对象
        dataMap = context.getTrigger().getJobDataMap();
        //获取JobDataMap对象中的数据
        username = dataMap.getString("username");
        age = dataMap.getInt("age");
        //打印
        System.out.println("key: " + key);
        System.out.println("username: " + username);
        System.out.println("age: " + age);

        //获取context中的JobDataMap对象
        dataMap = context.getMergedJobDataMap();
        //获取JobDataMap对象中的数据
        username = dataMap.getString("username");
        age = dataMap.getInt("age");
        //打印
        System.out.println("key: " + key);
        System.out.println("username: " + username);
        System.out.println("age: " + age);
    }
}
