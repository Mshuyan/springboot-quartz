>  参考资料：[SpringBoot2.0新特性 - Quartz自动化配置集成](https://www.jianshu.com/p/056281e057b3)

# 1. 引入依赖

## 1.1. quartz依赖

> springboot2.0开始集成了quartz，直接引入如下依赖即可

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

> 但是在springboot2.0之前版本需要自己手动引入quartz及相关依赖，这里不做赘述

## 1.2. 连接池依赖

> 虽然springboot2.0集成了quartz，但是集成的quartz中的`JDBCJobStore`中用到的`C3P0`连接池，

