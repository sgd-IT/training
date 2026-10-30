---
title: "Element Plus el-tag组件type属性统一约束规范"
usage_scenario:
    - "TS2322 报错提示 el-tag type 类型不匹配"
    - "模板中动态绑定 el-tag type 出现空字符串分支导致编译失败"
keywords:
    - "el-tag"
    - "type"
    - "TS2322"
    - "Element Plus"
    - "空字符串"
source: "auto"
---

Element Plus 的 el-tag 组件 type 属性仅接受预定义字符串字面量：'success' | 'warning' | 'info' | 'primary' | 'danger'，不支持空字符串 '' 或其他任意字符串。（来源：TypeScript 编译错误 TS2322）

Element Plus 的 el-tag 组件 type 属性不接受空字符串 ''，必须为预设值之一（如 'success'/'warning'/'info'/'primary'/'danger'）。（来源：TypeScript 编译器）
