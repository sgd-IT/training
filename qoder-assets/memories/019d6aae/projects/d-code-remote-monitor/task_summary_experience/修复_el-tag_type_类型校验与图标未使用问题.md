---
title: "修复 el-tag type 类型校验与图标未使用问题"
usage_scenario:
    - "Element Plus el-tag 组件 type 属性报 TS2322 错误时的修复方案"
    - "Vue 模板中通过字符串名使用图标（<component :is=\"...\">）却误导入导致 TS6133 的清理方法"
keywords:
    - "el-tag"
    - "TS2322"
    - "图标导入"
source: "auto"
---

## 任务描述
修复 camera-manage/index.vue 中的 TypeScript 类型错误（TS2322）和未使用声明警告（TS6133）。

## 执行过程
```mermaid
graph TD
    A[定位错误源] --> B[确认 camera-manage/index.vue 为唯一问题文件]
    B --> C[分析 el-tag type 非法空字符串 '' 引发 TS2322]
    C --> D[修正 cameraTypeTag 返回值数组，替换 '' 为 'primary']
    D --> E[移除 CircleCheckFilled/CircleCloseFilled 冗余 import]
    E --> F[删除未使用的 handleCopyAdd 函数及 copyData 变量]
```

## 任务总结
成功修复所有 TS 错误：el-tag type 严格限定为 Element Plus 支持的字面量（'success'|'warning'|'info'|'primary'|'danger'）；动态组件字符串引用无需显式 import 图标；彻底清理未使用代码避免 TS6133。
