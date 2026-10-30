---
title: "新增分析对象单独推送接口 /push/riskObj"
usage_scenario:
    - "为双控模块其他实体（如风险单元、隐患）新增独立补推接口时参考实现模式"
    - "改造现有补推接口以支持可选日期参数的通用方案"
    - "排查接口参数必填性与业务逻辑一致性问题"
keywords:
    - "分析对象"
    - "补推接口"
    - "/push/riskObj"
---

## 任务描述
为系统新增单独推送分析对象的 REST 接口 /push/riskObj，支持全量推送（不传日期）和按日期范围推送两种模式。

## 执行过程
```mermaid
graph TD
    A[需求:新增分析对象单独推送接口] --> B[定位现有推送接口集]
    B --> C[确认无独立/riskObj接口]
    C --> D[在PushDataController中添加@PostMapping\"/riskObj\"方法]
    D --> E[调整@RequestParam为required=false并更新@ApiImplicitParam说明]
    E --> F[同步修改PushDataServiceImpl.rePushRiskObj逻辑：判空后走全量分支]
    F --> G[更新日志与返回文案，确保语义准确]
```

## 任务总结
成功交付 /push/riskObj 接口：Controller 层支持可选 startDate/endDate 参数，Service 层兼容全量与范围筛选逻辑，日志与返回信息清晰，满足灵活补推需求。
