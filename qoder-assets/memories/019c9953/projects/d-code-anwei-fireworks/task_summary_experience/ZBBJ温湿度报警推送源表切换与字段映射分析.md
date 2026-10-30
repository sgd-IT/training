---
title: "ZBBJ温湿度报警推送源表切换与字段映射分析"
usage_scenario:
    - "政务平台报警数据对接ZBBJ规范时的字段映射与缺失项诊断"
    - "排查报警推送数据源错误（如误用历史表）的技术验证流程"
    - "类似预警表（如early_warning_info_push_record）对接新规范时的字段可行性评估"
keywords:
    - "ZBBJ规范"
    - "温湿度报警"
    - "字段映射"
    - "em_early_warning_info_push_record"
---

## 任务描述
修正温湿度报警（ZBBJ）推送的数据源，从原错误使用的 `em_alram_info` 表切换为实际业务表 `em_early_warning_info_push_record`，并完成ZBBJ规范字段与该表字段的映射分析及缺失项识别。

## 执行过程
```mermaid
graph TD
    A[确认ZBBJ当前错误使用em_alram_info] --> B[查询em_early_warning_info_push_record表结构]
    B --> C[获取真实报警数据样例验证warning_type含义]
    C --> D[定位推送服务PushingGZDataService.java和EarlyWarningInfoPushRecordService]
    D --> E[对比ZBBJ规范表9字段与该表字段映射关系]
    E --> F[识别targetcode/place_no/enddate/maxvalue等4类必填字段缺失]
    F --> G[提出4个关键业务问题：指标编码生成逻辑、消警判定依据、待推送筛选条件、缺失字段填充策略]
```

## 任务总结
确认ZBBJ温湿度报警应基于 `em_early_warning_info_push_record` 表（warning_type=0/1），完成全字段映射分析：id/company_code/targettype等12项可映射或默认，但 targetcode、place_no、enddate、maxvalue/maxtime 等5项关键字段缺失，需业务方确认关联逻辑或数据补全方案。
