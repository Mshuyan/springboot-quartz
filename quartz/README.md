> 参考资料：[W3CSchool-Quartz](https://www.w3cschool.cn/quartz_doc/quartz_doc-lwuv2d2a.html)
## 1. 关键类及接口
+ Job<br/>
    接口，用户要执行的任务放在该接口的实现类的`execute`方法中
+ JobDetail<br/>
    接口，将Job接口的实现类的class属性传入，获得1个任务的实例（JobBuilder），JobBuilder实现了JobDetail接口<br/>
    JobDetail中保存了要执行的任务以及任务的相关属性、设置等信息
+ Trigger<br/>
    定时器的接口，创建1个TriggerBuilder实例，用来定时调度JobDetail任务，TriggerBuilder实现了Trigger接口
+ Scheduler<br/>
    接口，通过StdSchedulerFactory获得1个StdScheduler实例，该实例用于管理Trigger定时器和JobDetail任务，StdScheduler实现了Scheduler接口
+ JobBuilder<br/>
    JobDetail接口的实现类
+ TriggerBuilder<br/>
    Trigger接口的实现类
### 2. Scheduler
1. 生命周期<br/>
    从SchedulerFactory创建Scheduler时开始<br/>
    执行Scheduler的shutdown方法时结束
2. 状态转换<br/>
    Scheduler被创建后（就绪状态） -->
    调用start方法后（执行状态） -->
    暂停后（阻塞状态） -->
    继续后（执行状态） -->
    调用shutdown方法后（死亡状态）
3. Scheduler创建后，随时可以添加删除Trigger和JobDetail，但是只有Scheduler处于执行态时，任务才会被执行
4. 处于`死亡状态`的Scheduler无法再次start，只能获得一个新的实例再进行调度
5. 同1个SchedulerFactory实现类的实例，如果之前没有获取过Scheduler实例或之前的Scheduler实例已经处于死亡状态，则调用getScheduler方法时返回的是新的Scheduler实例；否则跟之前获取的一直都是同一个实例
### 3. Job
1. 生命周期<br/>
    job任务每次被调度时，创建1个新的实例，执行任务，调度结束后，实例被销毁；下次再调度这个任务时，再创建新的实例
2. job实现类必须有无参构造
3. job实现类中不能直接使用有状态的数据属性（因为每次执行都重新创建实例了）
#### 3.1. 状态与并发
>   状态：指的是JobDataMap中的有状态的数据属性<br/>
    并发：指的是同一个JobDetail实例中的job接口实现类的多个实例的并发

+ @DisallowConcurrentExecution<br/>
    该注解加在job接口实现类上<br/>
    用于告诉Quartz，不要并发的执行同一个JobDetail实例中job接口实现类的多个实例,出现并发现象时，等待前1个执行结束后再执行下一个<br/>
    但是可以并发的执行不同JobDetail实例中同1个job接口实现类的多个实例
    如：<br/>
    1个JobDetail实例中，job任务的执行时间是2s，设置定时器是1s执行一次，则会导致该JobDetail实例中的job接口实现类的多个实例的并发，这样是不允许的，需要等待上1个结束才能执行下一个<br/>
    2个JobDetail实例中，使用了同1个job接口实现类，并且都设置定时器定时1s执行1次，则两个JobDetail实例会并发执行各自的job接口实现类的实例，这样是允许的
+ @PersistJobDataAfterExecution<br/>
    该注解加在job接口实现类上<br/>
    告诉Quartz在成功执行了job类的execute方法后，更新JobDetail中JobDataMap的数据<br/>
    该注解需要与@DisallowConcurrentExecution一起使用，否则会导致数据的线程不安全问题
    ```java
    @DisallowConcurrentExecution
    @PersistJobDataAfterExecution
    public class Myjob2 implements Job {
        private int index0;
    
        public void setIndex0(int index0) {
            this.index0 = index0;
        }
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println(context.getJobDetail().getKey() + " :" + index0);
            try {
                sleep(2000);
                index0 ++;
                //将新数据put到JobDetail的JobDataMap中之后，@PersistJobDataAfterExecution才能起作用
                context.getJobDetail().getJobDataMap().put("index0",index0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    ```
### 4. JobDataMap
+ Job任务中需要使用的有状态的数据属性，可以设置到JobDataMap中，每次执行job任务时可以获取JobDataMap中的数据
+ 定义JobDetail或Trigger实例时可以使用usingJobData方法将数据设置到各自JobDataMap中
+ JobDetail和Trigger拥有自己的JobDataMap对象，Job接口实现类的参数context的JobDataMap对象是JobDetail和Trigger中JobDataMap对象的并集，但是如果存在重复属性，后者会覆盖前者
  
#### 4.1. 使用
1. 设置数据
    + 向JobDetail的JobDataMap对象中设置属性
        ```java
        JobDetail job = newJob(Myjob.class)
                        .withIdentity("myJob", "group1")
                        .usingJobData("username","lilei")
                        .usingJobData("age",15)
                        .build();
        ```
    + 向Trigger的JobDataMap对象中设置属性
        ```java
        Trigger trigger = newTrigger()
                        .withIdentity("myTrigger", "group1")
                        .usingJobData("username","hanmeimei")
                        .usingJobData("age",16)
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .withIntervalInSeconds(1)
                                .repeatForever())
                        .build();
        ```
2. job任务中取出数据
    + 从context中手动获取数据
        ```java
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
        ```
    + JobFactory实现数据的自动“注入”<br/>
      满足以下几点，JobFactory会自动将context中JobDataMap对象中设置的数据自动注入到Job接口实现类的成员变量中
      + 设置的属性名与Job实现类中成员变量名相同
      + 为成员变量提供set方法
      ```java
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
              System.out.println("username: " + username);
              System.out.println("age: " + age);
          }
      }
      ```

### 5. key、name、group
<a name="name1"/>

+ 在创建`JobDetail`和`Trigger`实例时，都需要指定这个实例的name和group属性
+ group分组是相对于当前scheduler而言的，1个scheduler可以有多个group，每个group中可以存多个JobDetail或Trigger
+ name是当前实例的名称，同一分组下，无论Trigger还是JobDetail的name属性不能重复，
+ 每个JobDetail或Trigger实例的key都是他的group和name属性的组合，如：`group.name`,key可以通过实例的getKey方法获得
+ key是实例的唯一标识，通过他可以知道是哪个任务和定时器调用的当前job实例中的execute方法
```java
//在job实现类中获取key
public void execute(JobExecutionContext context) throws JobExecutionException {
    JobKey jobKey = context.getJobDetail().getKey();
    TriggerKey triggerKey = context.getTrigger().getKey();
}
```
```java
//在创建实例时获取key
JobDetail job = newJob(Myjob1.class)
        .withIdentity("myJob", "group1")
        .build();

JobKey jobKey = job.getKey();
```
### 6. JobDetail
#### 6.1. 属性配置
> 通过JobDetail可以为Job配置一些属性
1. Durability<br/>
    如果一个job是非持久的，当没有活跃的trigger与之关联的时候，默认情况下（Durability = false）会被自动地从scheduler中删除；设置`Durability`为`true`后，会在scheduler中保留
    ```java
    JobDetail job = newJob(Myjob2.class)
                    .withIdentity("myJob", "group1")
                    //设置Durability
                    .storeDurably(true)
                    .build();
    ```
2. RequestsRecover<br/>
    如果一个job是可恢复的，并且在其执行的时候，scheduler发生硬关闭（比如运行的进程崩溃了，或者关机了），则当scheduler重新启动的时候：<br/>
    如果（RequestsRecover = true）该job会被重新执行。此时，该job的JobExecutionContext.isRecovering() 返回true。<br/>
    如果（RequestsRecover = false（默认））该job不会被重新执行。
    ```java
    JobDetail job = newJob(Myjob2.class)
                    .withIdentity("myJob", "group1")
                    //设置RequestsRecover
                    .requestRecovery(true)
                    .build();
    ```
    + 可恢复是什么意思？<br/>
      可以理解为就是持久化的
      
### 7. Trigger
> Trigger主要分为2种：SimpleTrigger、CronTrigger,这两个都是接口<br/>
    SimpleTrigger和CronTrigger都继承自Trigger，所以他们也具备一些公共属性

#### 7.1. 公共
##### 7.1.1. 属性
1. TriggerKey<br/>
    参见`1.5. key、name、group`
2. jobKey<br/>
    与当前`trigger`绑定的`jobDetail`实例的key
3. startTime<br/>
    设置trigger生效时间（Date类型）
4. endTime<br/>
    设置trigger失效时间（Date类型）
##### 7.1.2. 优先级
>   同时触发的trigger数量高于quartz线程池内总线程数时，会比较优先级决定先执行哪个trigger<br/> 
    通过withPriority()方法设置priority属性设置优先级，默认为5，参数可以是任意整数
##### 7.1.3. Calendar
>   Quartz的Calendar对象(不是java.util.Calendar对象)可以在定义和存储trigger的时候与trigger进行关联。<br/>
    Calendar用于从trigger的调度计划中排除或使用时间段。<br/>
    比如:需求是工作日每天8点执行1个任务，则可以创建一个trigger，每天的上午8:00执行，然后增加一个Calendar，排除掉所有的节假日。<br/>

+ `Calendar`是1个接口，他有如下几个实现类
  + BaseCalendar<br/>
    为高级的 Calendar 实现了基本的功能,基本不使用
  + HolidayCalendar<br/>
    排除指定的某几天，没有周期，以天为单位<br/>
    **注意**：<br/>
    > 这里的年是生效的，排除的是指定的年月日<br/>
  + AnnualCalendar<br/>
    排除每年的某几天，以年为周期，以天为单位<br/>
    **注意**：<br/>
    > 这里的年是没用的，即使设置了年，也是每年排除这几天<br/>
  + MonthlyCalendar<br/>
    排除每月的某几天，以月为周期，以天为单位
  + WeeklyCalendar<br/>
    排除每周的某几天，以周为周期，以天为单位
  + DailyCalendar
    排除每天的指定时间段，格式是HH:MM[:SS[:mmm]]。也就是最大精度可以【到毫秒】
  + CronCalendar<br/>
    指定Cron表达式。精度取决于Cron表达式，也就是最大精度可以【到秒】
+ 每种Calendar都可以通过`setInvertTimeRange()`设置是否在指定的时间段内触发<br/>
    true:在这个时间段内触发<br/>
    false:在这个时间段内不触发
+ Scheduler的addCalendar方法参数说明
  + calName：日历的名字
  + calendar：日历对象
  + replace：当日历已经存在的情况下是否替换，true=替换， false=不替换 如果不替换还出现重复的情况会抛出异常
  + updateTriggers：当存在同名的calendar时，如果之前的calendar已经被trigger使用，替换旧的calendar的同时，是否让trigger使用新的calendar
+ 更多内容参见[博客](http://www.cnblogs.com/daxin/p/3925619.html)
##### 7.1.4. 错过触发(misfire Instructions)
>   如果scheduler关闭了，或者Quartz线程池中没有可用的线程来执行job，
    此时持久性的trigger就会错过(miss)其触发时间，即错过触发(misfire)。不同类型的trigger，有不同的misfire机制。
    它们默认都使用“智能机制(smart policy)”，即根据trigger的类型和配置动态调整行为。当scheduler启动的时候，
    查询所有错过触发(misfire)的持久性trigger。然后根据它们各自的misfire机制更新trigger的信息。
#### 7.2. Simple Trigger
+ 不需要基于`Calendar`的`Trigger`使用`Simple Trigger`即可
+ 特点：<br/>
    `Simple Trigger`的trigger创建时，`withSchedule()`传入的是`simpleSchedule()`
#### 7.3. CronTrigger
> + 基于日历的定时任务使用CronTrigger<br/>
> + 基于日历的定时任务有2种实现方式：<br/>
>   + 上面介绍的Clendar方式<br/>
>   + TriggerBuilder和CronScheduleBuilder两个包下的类构建的CronTrigger
1. CronTrigger的创建
    + 使用CRON表达式构建<br/>
        ```java
        Trigger trigger1 = newTrigger()
                        .withIdentity("myTrigger1", "group1")
                        .startNow()
                        .withSchedule(cronSchedule("cron表达式"))
                        .build();
        ```
    + 使用`CronScheduleBuilder`包下的其他类构建<br/>
      `CronScheduleBuilder`包下的用于构建`CronTrigger`的类还有很多，这里只拿`dailyAtHourAndMinute`举例
        ```java
        Trigger trigger2 = newTrigger()
                        .withIdentity("myTrigger2", "group1")
                        .startNow()
                        .withSchedule(dailyAtHourAndMinute(10, 42))
                        .build();
        ```
2. CRON表达式<br/>
    1. 表达式规则<br/>
       Cron表达式由6或7个由空格分隔的时间字段组成，各个字段的含义(按顺序)如下表所示：<br/>

       |   字段位置   |   位置含义   |   允许值   |   允许的特殊字符   |
       | :--: | ---- | ---- | ---- |
       |   1  | 秒 | 0-59 |, - * /|
       | 2 | 分钟 | 0-59 |, - * /|
       | 3 | 小时 | 0-23 |, - * /|
       | 4 | 日期 | 1-31 |, - * ? / L W C|
       | 5 | 月份 | 1-12 |, - * /|
       | 6 | 星期 | 1-7（对应星期日-星期六） |, - * ? / L C #|
       | 7 | 年（可选） | 空值;1970-2099 |, - * /|

    2. 特殊字符
        > 各个字段可以使用允许值，也可以使用允许的特殊字符<br/>
          如：“0 0 12 ？* WED“ - 这意味着”每个星期三下午12:00“<br/>
          特殊字符含义如下：

        + 星号（*）<br/>
            表示任意值；如：*在分钟字段时，表示“每分钟”
        + 问号（？）<br/>
            占位符，没有意义
        + 减号（-）<br/>
            表示范围；如：小时字段中使用“10-12”，则表示从10到12点
        + 逗号（,）<br/>
            表示列表时用于分割列表中的元素，如：在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期五
        + 斜杠（/）<br/>
            `x/y`表示起始值为x，步长为y的等步长序列；如：在分钟字段中使用0/15，则表示为0,15,30和45秒
        + L<br/>
            L可以用在`日期`和`星期`字段，用在这两个字段时意义不同：
            + 日期<br/>
                L表示该月的最后一天
            + 星期<br/>
                L表示星期六，等同于7<br/>
                xL表示该月最后的星期x；如：6L表示本月的最后1个星期五
        + W<br/>
            该字符只能出现在日期字段里，是对前导日期的修饰，表示离该日期最近的工作日。
            例如15W表示离该月15号最近的工作日，如果该月15号是星期六，则匹配14号星期五；
            如果15日是星期日，则匹配16号星期一；如果15号是星期二，那结果就是15号星期二。
            但必须注意关联的匹配日期不能够跨月，如你指定1W，如果1号是星期六，
            结果匹配的是3号星期一，而非上个月最后的那天。W字符串只能指定单一日期，
            而不能指定日期范围；
        + LW组合<br/>
            表示本月最后1个工作日
        + 井号（#）<br/>
            `w#n`表示本月第n个星期w
        + C<br/>
            5C在日期字段中就相当于日历5日以后的第一天。<br/>
            1C在星期字段中相当于星期日后的第一天。

    3. 更多资料参见[Quartz——CronTrigger触发器](http://eksliang.iteye.com/blog/2208295)
### 8. Listener
#### 8.1. TriggerListener和JobListener
+ JobListener<br/>
    + 继承`JobListenerSupport`类，重写想要监听的事件的方法
    + 必须重写`getName()`，为这个Listener起个名字并返回
    + 注册Listener
        ```java
        //为指定job添加监听
        scheduler.getListenerManager().addJobListener(myJobListener, jobKeyEquals(jobKey("myJobName", "myJobGroup")));
        //为1个组的job添加监听
        scheduler.getListenerManager().addJobListener(myJobListener, jobGroupEquals("myJobGroup"));
        //为多个组的job添加监听
        scheduler.getListenerManager().addJobListener(myJobListener, or(jobGroupEquals("myJobGroup"), jobGroupEquals("yourGroup")));
        //为所有job添加监听
        scheduler.getListenerManager().addJobListener(myJobListener, allJobs());
        ```
+ TriggerListener<br/>
    + 继承`TriggerListenerSupport`类
    + 其他与JobListener相同
#### 8.2. SchedulerListener
+ 创建Listener<br/>
    实现`SchedulerListener`接口
+ 注册Listener
    ```java
    scheduler.getListenerManager().addSchedulerListener(mySchedListener);
    ```
+ 删除Listener<br/>
    ```java
    scheduler.getListenerManager().removeSchedulerListener(mySchedListener);
    ```
### 9. 配置文件
#### 9.1. 加载位置
+ 默认按如下顺序进行加载，前面的加载失败才加载后面的:
    + 从“当前工作目录”(resources目录)加载名为“quartz.properties”的属性文件
    + 加载`quartz包下org/quartz/quartz.properties`
+ 自己指定加载配置文件
    + 创建`StdSchedulerFactory`对象时通过构造方法指定配置文件
    + 创建`StdSchedulerFactory`对象后，执行`getScheduler`之前，调用`initialize`方法指定配置文件
    ```java
    SchedulerFactory schedFact0 = new StdSchedulerFactory("properties/quartz.properties");
    //或
    SchedulerFactory schedFact1 = new StdSchedulerFactory();
    ((StdSchedulerFactory) schedFact1).initialize("properties/quartz.properties");
    ```
### 10.JobStore
> JobStore用于配置将Job、trigger、日历等加入到了scheduler中的任务等内容存放在何处

#### 10.1. RAMJobStore
    + 存在内存中，非持久化的，容易丢失，但是速度快
    + 在配置文件中将`org.quartz.jobStore.class`配置为`org.quartz.simpl.RAMJobStore`即可<br/>
#### 10.2 JDBC JobStore
> 存在数据库中，持久化的，但是速度慢
1. 创建数据库表<br/>
    + 在[quartz官网](http://www.quartz-scheduler.org/downloads/)下载资料`quartz-2.2.3.tar.gz`,解压后找到数据库建表脚本：`docs/dbTables/tables_mysql_innodb.sql`
    + 该脚本在本工程根目录下也拷贝了1份
    + 在数据库中创建1个数据源，如：quartz，在该表中执行sql脚本

2. 事务
    + JobStoreTX：Quartz自己管理事务
    + JobStoreCMT：服务器容器（spring）管理事务

3. DataSource<br/>
    quartz有2种获取数据库连接的方式：
    + quartz自己创建管理datasource
    + 使用服务器应用程序管理的datasources

    




​    

