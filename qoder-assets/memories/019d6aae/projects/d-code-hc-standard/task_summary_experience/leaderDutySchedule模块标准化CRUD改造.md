---
title: "leaderDutySchedule模块标准化CRUD改造"
usage_scenario:
    - "为其他新业务模块（如ticketJwx、riskTask）快速实现标准CRUD功能"
    - "排查RuoYi项目中非标准页面（如无搜索/无弹窗）的改造路径"
    - "修复后端分页参数不兼容或前端API缺失导致的CRUD功能异常"
keywords:
    - "CRUD"
    - "RuoYi标准"
    - "leaderDutySchedule"
source: "auto"
---

## 任务描述
为 leaderDutySchedule 模块实现标准 RuoYi 增删改查功能，包括顶部搜索区、新增/批量删除按钮、表格列表（含修改/删除操作列）、分页及弹窗表单。

## 执行过程
```mermaid
graph TD
    A[需求:标准CRUD布局] --> B[分析现有Controller接口]
    B --> C[发现page参数不兼容、缺少add/batchDelete]
    C --> D[重构Controller：统一pageNum/pageSize、新增add/batchDelete、抽取checkMissingFields校验]
    D --> E[更新前端API：新增addLeaderDutySchedule/batchDelLeaderDutySchedule]
    E --> F[重写index.vue：标准搜索表单+操作栏+带复选框表格+时间格式化+弹窗表单+字段校验]
    F --> G[编译验证：mvn compile通过]
```

## 任务总结
成功将 leaderDutySchedule 改造为标准 CRUD 模块：后端支持分页查询（pageNum/pageSize）、单条新增、批量删除、带字段校验的修改；前端 API 新增对应函数；Vue 页面完全重写，对齐 contractor/check 等已有模块风格，支持搜索、勾选、弹窗、时间解析与手机号校验。
