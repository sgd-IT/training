---
title: "双控模块统一为/push/riskTask一键全推"
usage_scenario:
    - "双控类业务需合并多个独立接口为统一入口时的改造路径"
    - "排查记录等存在状态字段干扰的模块，需绕过业务状态限制实现纯日期范围推送"
    - "同类多模块需统一参数、统一日志、统一返回格式的标准化改造"
keywords:
    - "双控"
    - "一键全推"
    - "日期范围过滤"
    - "排查记录"
---

## 任务描述
将双控7个模块（四色图、分析对象、风险单元、风险事件、管控措施、任务排查、排查记录）的独立推送接口，统一整合为单接口 `/push/riskTask`，并确保所有模块严格按6-7月日期范围过滤推送。

## 执行过程
```mermaid
graph TD
    A[需求:双控模块统一推送] --> B[分析各模块时间字段与过滤方式]
    B --> C[为前5模块添加filterByDateRange通用方法]
    C --> D[Controller层统一增加startDate/endDate参数及Swagger注解]
    D --> E[删除原7个独立接口，仅保留/push/riskTask]
    E --> F[Service层新增rePushAllShuangKong方法，按依赖顺序串行调用]
    F --> G[发现排查记录模块SQL过滤过严导致漏数据]
    G --> H[新增Mapper方法getRecordListByDateRange及对应XML SQL]
    H --> I[更新rePushRiskTaskRecord使用新查询]
    I --> J[编译验证+数据量验证确认2299条全量覆盖]
```

## 任务总结
成功实现双控数据一键全推：统一入口`/push/riskTask`，7模块按依赖顺序执行；前5模块复用`createTime` Java层过滤，排查记录模块采用`check_time` SQL层日期范围查询（修复原`sync_status!=1 AND check_status=1`漏数据问题），任务排查模块保持原有SQL日期过滤；最终6-7月2299条排查记录全部纳入推送范围。
