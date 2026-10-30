---
title: "双控数据补推功能：从多接口到单入口的push包闭环实现"
usage_scenario:
    - "新增类似‘多模块依赖顺序推送’功能时参考整体架构设计"
    - "需要在不改动现有Service层前提下快速交付新推送能力"
    - "统一日期过滤+一键全推类需求的代码组织与接口收敛方案"
keywords:
    - "双控"
    - "日期过滤"
    - "一键全推"
    - "push包"
    - "零侵入Service"
---

## 任务描述
在 push 包中实现双控模块数据补推功能，要求逻辑完全隔离于现有 Service 层，最终合并为单一 `/push/riskTask` 接口，支持按日期范围（6-7月）一键推送全部7个双控模块（四色图→分析对象→风险单元→风险事件→管控措施→任务排查→排查记录）。

## 执行过程
```mermaid
graph TD
    A[需求:双控补推] --> B[调研Mapper能力：grep确认select/updateSyncStatus方法]
    B --> C[确认API接口：read PlatementFourChartApi等]
    C --> D[创建PushDataServiceImpl：直接注入Mapper+API，零侵入Service]
    D --> E[初始Controller：暴露9个独立POST端点]
    E --> F[用户追加需求：所有双控模块需按日期范围过滤]
    F --> G[添加filterByDateRange通用方法：基于createTime过滤]
    G --> H[改造5个双控方法：增加startDate/endDate参数+Java层过滤]
    H --> I[用户再追加需求：合并为单一接口]
    I --> J[Controller：删除8个端点，仅保留/repush/riskTask并添加@ApiImplicitParams]
    J --> K[Service：新增rePushAllShuangKong方法，按依赖顺序串行调用7个子方法]
    K --> L[编译验证：确保BaseRiskFourchart继承BaseEntity，全量编译通过]
```

## 任务总结
成功构建 push 包专属双控补推体系：1）逻辑完全封闭在 `PushDataServiceImpl`，不修改任何原有 Service；2）统一日期过滤策略（`filterByDateRange` 通用方法）；3）最终收敛为 `/push/riskTask` 单一入口，按强依赖顺序执行7个模块，返回结构化【1/7】~【7/7】日志，支持 6-7 月数据精准补推。
