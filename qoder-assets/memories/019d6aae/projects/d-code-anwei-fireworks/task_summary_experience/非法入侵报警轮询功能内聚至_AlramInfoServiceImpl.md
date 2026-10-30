---
title: "非法入侵报警轮询功能内聚至 AlramInfoServiceImpl"
usage_scenario:
    - "将定时任务逻辑从独立 Job 类迁移/合并至业务 Service 实现"
    - "在已有 Service 中安全添加 @Scheduled 方法并复用其依赖组件"
    - "处理 SearchReplace 工具不可用时的替代执行策略"
keywords:
    - "@Scheduled"
    - "AlramInfoServiceImpl"
    - "轮询语音播报"
---

## 任务描述
为非法入侵报警（alarm_type=4）添加基于 Spring `@Scheduled` 的轮询语音播报功能，并确保其在 `AlramInfoServiceImpl` 中实现，而非新建独立 Job 类。

## 执行过程
```mermaid
graph TD
    A[需求:@Scheduled 实现非法入侵轮询] --> B[搜索启动类确认 @EnableScheduling 缺失]
    B --> C[尝试 SearchReplace 添加 @EnableScheduling 失败（工具未就绪）]
    C --> D[改用 Write/SearchReplace 组合完成启动注解修改]
    D --> E[用户明确要求：直接在 AlramInfoServiceImpl.java 中实现]
    E --> F[Read 获取 AlramInfoServiceImpl 全文]
    F --> G[SearchReplace 插入 Scheduled import、VOICE_POLL_PREFIX 常量、pollIllegalIntrusionForVoice 方法]
```

## 任务总结
成功将非法入侵语音播报逻辑内聚至 `AlramInfoServiceImpl`：添加 `@Scheduled(fixedDelay = 2000)` 方法，复用已有 Redis 去重、企业分组、语音播报逻辑；同步补全 `@EnableScheduling` 启用配置。避免了跨模块新建 Job 类的耦合，更利于维护与测试。
