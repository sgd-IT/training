---
title: "Java同名DTO类因包路径不一致导致类型不匹配"
usage_scenario:
    - "编译报错'无法应用于'且参数类型看似相同"
    - "Controller与Service引用同名DTO但编译失败"
    - "多模块/分包项目中DTO类被错误导入相邻包路径"
keywords:
    - "Java包路径"
    - "DTO类型不匹配"
    - "import错误"
    - "编译错误"
---

Java类编译报错 '无法应用于' 同名DTO参数，根本原因是Controller与Service导入了不同包路径下的同名类（如 `com.monkey.general.loulv.dto.XxxVo` vs `com.monkey.general.utils.loulv.dto.XxxVo`），导致类型不匹配。修复方案：统一所有引用方的import路径为正确路径 `com.monkey.general.utils.loulv.dto.XxxVo`（来源：Read）
