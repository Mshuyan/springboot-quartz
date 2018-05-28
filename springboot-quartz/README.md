>  参考资料：[SpringBoot2.0新特性 - Quartz自动化配置集成](https://www.jianshu.com/p/056281e057b3)

# 1. quartz依赖

> springboot2.0开始集成了quartz，直接引入如下依赖即可

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

> 但是在springboot2.0之前版本需要自己手动引入quartz及相关依赖，这里不做赘述

# 2. 使用

## 2.1. job

> Springboot2.0中，job的定义直接继承`QuartzJobBean`并重写`executeInternal`方法即可

```java
@Data
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class MyJob extends QuartzJobBean {
    private int index;
    static int cnt = 0;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        index ++;
        System.out.println("index : " + index);
        context.getJobDetail().getJobDataMap().put("index", index);
    }
}
```

## 2.2. Trigger与JobDetail

> springboot2.0中，向scheduler中绑定jobdetail与trigger的操作通过配置类完成
>
> 编写1个配置类，声名两个bean，分别返回trigger与jobdetail，则他们两个会被一起绑定到scheduler中

```java
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
```

> 如此，入门的quartz就可以运行的

# 3. quartz配置文件

> 1. springboot2.0中集成的quartz取消了`quartz.properties`配置文件，所有的配置可以在`application.properties`中进行配置；
> 2. 原有`quartz.properties`中的配置，加上`spring.quartz.properties.`前缀，可以在`application.properties`中直接使用

```properties
spring.quartz.properties.org.quartz.scheduler.instanceName=hs-bus
spring.quartz.properties.org.quartz.scheduler.instanceId=AUTO
spring.quartz.properties.org.quartz.jobStore.class=org.quartz.impl.jdbcjobstore.JobStoreTX
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass=org.quartz.impl.jdbcjobstore.StdJDBCDelegate
spring.quartz.properties.org.quartz.jobStore.tablePrefix=QRTZ_
spring.quartz.properties.org.quartz.jobStore.isClustered=true
spring.quartz.properties.org.quartz.jobStore.clusterCheckinInterval=10000
spring.quartz.properties.org.quartz.jobStore.useProperties=false
spring.quartz.properties.org.quartz.threadPool.class=org.quartz.simpl.SimpleThreadPool
spring.quartz.properties.org.quartz.threadPool.threadCount=10
spring.quartz.properties.org.quartz.threadPool.threadPriority=5
spring.quartz.properties.org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread=true
```

> 额外的，springboot2.0针对quartz增加了一些配置项

1. `spring.quartz.job-store-type`
   + 用户配置`JobStore`方式
   + 可取值：`jdbc`、`memory`
   + 注意：==将定时任务持久化到数据库中时，必须设置该项为jdbc==，否则如果数据源选择为应用程序中的数据源时，quatz会以为你要使用为quartz单独配置的数据源，一直报错：没有设置数据源名称
2. `spring.quartz.jdbc.initialize-schema`
3. `spring.quartz.jdbc.schema`

# 4. 持久化到数据库

## 4.1. 依赖

> 除quartz的依赖外，如果要进行持久化，还需要一些额外的依赖

1. mysql依赖

   > 要使用的数据库是mysql，所以需要mysql依赖

2. jdbc依赖

   > 如果不加jdbc依赖，会报如下错误

   ```java
   java.lang.ClassNotFoundException: org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
   ```

3. 连接池依赖

   如果想使用`alibaba`的`druid`连接池，直接引入他的依赖，就会自动切换连接池

## 4.2. 配置文件

```properties
#============================================================================
# 配置springboot中的数据源
#============================================================================
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_quartz?useUnicode=true&characterEncoding=utf-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=rootroot
spring.jooq.sql-dialect=mysql

#============================================================================
# 配置quartz持久化到数据库，数据源使用spring中的数据源
#============================================================================
# 下面这句话非常重要，否则没办法使用应用程序中的数据源
spring.quartz.job-store-type=jdbc
spring.quartz.properties.org.quartz.jobStore.class = org.quartz.impl.jdbcjobstore.JobStoreTX
spring.quartz.properties.org.quartz.jobStore.driverDelegateClass = org.quartz.impl.jdbcjobstore.StdJDBCDelegate
spring.quartz.properties.org.quartz.jobStore.tablePrefix = QRTZ_
```

## 4.3. 创建数据库表结构

> 与原生quartz中相同

