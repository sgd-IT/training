---
title: "PlatementTicketNewApi推送字段清理机制建设"
usage_scenario:
    - "对接第三方平台时DTO推送字段校验与清理"
    - "排查因JSON字段冗余导致的500错误"
    - "设计安全的DTO序列化字段过滤策略"
keywords:
    - "字段清理"
    - "非平台字段"
    - "JSON推送"
    - "黑名单白名单"
---

## 任务描述
修复PlatementTicketNewApi推送JSON因携带非平台字段导致500错误的问题。

## 执行过程
```mermaid
graph TD
    A[问题定位] --> B[对比日志与平台接口文档]
    B --> C[识别多余字段：子表对象、显示字段、内部业务字段、BaseEntity冗余字段]
    C --> D[首次修复：在PlatementTicketNewApi中添加黑名单NON_PLATFORM_FIELDS及cleanNonPlatformFields方法]
    D --> E[二次补漏：追加contractorId和deptName到黑名单]
    E --> F[方案优化：提出白名单PLATFORM_FIELDS方案，更安全可控]
```

## 任务总结
成功建立字段清理机制：实现cleanNonPlatformFields方法，通过黑名单方式移除22类非平台字段（含approveList/sensorList等子表对象、prepareIdShow等显示字段、curOperatorId等内部字段、searchValue/params等BaseEntity冗余字段），并论证白名单方案更优，适用于所有对接外部平台的DTO推送场景。
