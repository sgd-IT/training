---
title: "修复@Scheduled未启用导致轮询日志缺失"
usage_scenario:
    - "排查定时任务@Scheduled不执行的根因"
    - "检查自定义启动注解是否遗漏关键Spring Boot功能注解"
    - "同类项目中验证@EnableScheduling是否被正确启用"
keywords:
    - "@Scheduled"
    - "@EnableScheduling"
    - "定时任务"
    - "日志缺失"
---

## 任务描述
修复非法入侵轮询任务（@Scheduled）未执行导致无日志输出的问题。

## 执行过程
```mermaid
graph TD
    A[现象:每2秒无日志] --> B[分析AlramInfoServiceImpl.pollIllegalIntrusionForVoice逻辑]
    B --> C[发现空列表直接return，无入口日志]
    C --> D[添加log.debug入口日志并升级为log.info]
    D --> E[验证仍无日志→怀疑@Scheduled未生效]
    E --> F[全局grep @EnableScheduling]
    F --> G[确认项目中无@EnableScheduling使用]
    G --> H[定位到自定义启动注解SpringBooStartApplication.java]
    H --> I[补全@EnableScheduling注解]
```

## 任务总结
根本原因是自定义启动注解SpringBooStartApplication.java import了@EnableScheduling但未声明，导致所有@Scheduled方法静默失效。通过全局搜索定位、精准补全注解，使轮询任务正常触发，INFO级日志每2秒稳定输出。
