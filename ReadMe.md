<div style="width: 100%; font-size: 3rem; text-align: center;"><span>记账助手</span></div>

```
创建时间：2022-09-30 07:41:39
描   述：本次的Api是对之前的研发进行重构
技 术 栈：SpringBoot、SpringCloudAlibaba(Nacos)、Mybatis Plus(自动生成代码且预置了一些增删改查)
```

# 项目架构

## 微服务模块介绍

### gateway-service

本服务作为所有微服务的网关服务。

### user-service

用户微服务

### record-account-service

记账微服务



## 微服务中间关系

<img src="folder/imgs/01.jpg" style="zoom: 60%">













# 错误整理

## idea 注入mapper报错报红

### 报错示例

<img src="folder/imgs/02.png" alt="image-20221003103412593" style="zoom:50%;" />

### 解决方案

#### 方案一 （推荐）

将 @Autowird 改为 @Resource 即可。

#### 方案二

为 @Autowired 注解设置required = false；他表达的意思是：使用 @Autowired 注解不再去校验userMapper是否存在了，也就不会有警告了

#### 方案三

把IDEA的警告关闭掉；不建议使用，因为Idea的灵魂就是提示，去掉了就没多大灵魂所在。

<img src="folder/imgs/03.png" alt="image-20221003104216034" style="zoom:40%;" />

### 参考地址

https://blog.csdn.net/JFENG14/article/details/123281224

































