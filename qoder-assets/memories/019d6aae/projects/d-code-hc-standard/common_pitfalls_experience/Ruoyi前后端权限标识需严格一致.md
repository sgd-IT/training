---
title: "Ruoyi前后端权限标识需严格一致"
usage_scenario: []
keywords:
    - "Ruoyi"
    - "权限控制"
    - "v-hasPermi"
    - "@PreAuthorize"
source: "user"
---

Ruoyi框架中，前端按钮的v-hasPermi权限标识（如'base:riskTask:sync'）必须与后端Controller方法上的@PreAuthorize注解权限字符串完全一致，否则按钮不可见或请求被拦截。（来源：BaseRiskTaskController.java）
