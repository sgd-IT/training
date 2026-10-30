---
title: "PC端特殊作业建票入口不可用与双通道限制"
usage_scenario:
    - "选择建票通道时判断权限与子表副作用"
    - "排查APP作业票操作按钮显隐来源"
keywords:
    - "PC建票按钮注释"
    - "saveWorkSpecial走93项预校验"
    - "insertBaseWorkSpecial绕过预校验不生成子表"
    - "user_type01返回403"
source: "auto"
---

hc-standard 特殊作业链路上两处反直觉的结构约束，改动或验证前须知：

1. PC 建票入口不可用：ruoyi-ui views/base/workSpecial/index.vue 的工具栏新增/修改按钮自初版即注释，实际建票只有 APP 的 saveWorkSpecial（走平台 93 项字段预校验）与后端 POST /base/workSpecial（insertBaseWorkSpecial，绕过 93 项但不生成编号与安全措施/审批流子表）两条路；后者需 base:workSpecial:add 权限，APP 侧 user_type=01 账号调用返回 403。

2. 作业票操作权限与状态可见性口径见「特殊作业操作权限由状态掩码改为viewOnly加canOperate模型」：原详情接口对非 curOperatorId 强改 workStatus="1" 的状态掩码已下线，本条不再描述该机制。
