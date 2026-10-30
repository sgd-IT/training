---
title: "RuoYi-Vue-Plus 项目总体介绍"
usage_scenario:
    - "理解项目定位、适用场景与核心价值"
    - "掌握系统整体架构与模块划分"
keywords:
    - "分布式集群"
    - "插件化架构"
    - "Sa-Token"
    - "MyBatis-Plus"
    - "SnailJob"
---

项目定位：Dromara RuoYi-Vue-Plus 是面向分布式集群场景对 RuoYi-Vue 的全方位重写升级版（不兼容原框架），开源免费可商用。

核心架构：前后端分离，前端基于 Vue3 + TS + ElementPlus；后端采用插件化+扩展包结构，解耦易扩展。

核心能力：
- 全面权限体系（Sa-Token + JWT + JustAuth）
- 多数据源/多数据库异构支持（dynamic-datasource + MyBatis-Plus）
- 分布式能力（Redisson锁、SnailJob调度、WarmFlow工作流、MinIO/S3云存储）
- 安全增强（动态AES+RSA接口加密、数据脱敏/加解密、幂等控制）
- 开发提效（SpringDoc零侵入文档、Fesod Excel框架、代码生成器）
