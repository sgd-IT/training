---
title: "saveWorkSpecial接口字段必填规则与条件豁免说明"
usage_scenario:
    - "实现作业票证推送逻辑时校验字段完整性"
    - "修复接口400错误时定位缺失或非法字段"
    - "编写字段自动补全工具时确定默认值策略"
keywords:
    - "saveWorkSpecial"
    - "必填字段"
    - "条件豁免"
    - "临时用电"
    - "ticketsType=6"
---

项目`/openapi/work/saveWorkSpecial`接口中，所有标注'必需'的字段均需提供值；但部分字段存在条件豁免：当`isRelation="0"`时，`relationWorkType`、`relationWorkNo`、`relationWork`可为空；当`isChanged="0"`时，`changedCause`可为空；当`isCancelled="0"`时，`cancellenCause`可为空。对于`ticketsType="6"`（临时用电），专属必需字段为`jrdYdgl`、`sbmEdgl`、`gzdy`、`ydr`，且`level`虽标注必需但无实际级别定义，空字符串可能被平台拒绝。
