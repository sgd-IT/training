---
title: "包保履职功能 createTime 校验约束"
usage_scenario:
    - "实现或修复包保履职数据推送逻辑时处理平台校验要求"
    - "排查推送失败原因时定位字段缺失问题"
    - "新成员理解该模块与外部平台对接的关键字段映射规则"
keywords:
    - "包保履职"
    - "createTime校验"
    - "startTime填充"
---

项目包保履职功能涉及tgr-gz-vue.platement_implement_task表，当前存在平台校验要求createTime非空但表中无该字段的问题，解决方案是推送前用startTime字段值填充createTime以满足校验。
