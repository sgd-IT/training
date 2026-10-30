---
title: "sync_result字段用途规范"
usage_scenario:
    - "排查推送失败原因时查阅sync_result字段内容"
    - "设计同步状态展示功能时确定数据来源"
    - "编写同步日志审计逻辑时确认结果存储位置"
keywords:
    - "sync_result"
    - "推送响应"
    - "数据库字段"
---

系统推送接口的响应结果（成功时的data字段或失败时的msg字段）会写入数据库表的sync_result字段，用于记录同步详情。
