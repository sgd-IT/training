---
title: "Controller方法返回类型规范"
usage_scenario:
    - "新增Controller接口方法时确定返回类型"
    - "重构现有Controller方法签名时统一响应格式"
    - "审查API响应一致性时"
keywords:
    - "Controller"
    - "R<Void>"
    - "toAjax"
    - "返回类型"
---

项目中Controller方法的返回类型需与toAjax()工具方法保持一致。当前约定为R<Void>，因此add和edit等操作方法应声明为R<Void>而非R<Integer>。
