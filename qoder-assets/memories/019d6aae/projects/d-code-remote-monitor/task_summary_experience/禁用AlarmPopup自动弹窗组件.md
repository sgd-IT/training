---
title: "禁用AlarmPopup自动弹窗组件"
usage_scenario:
    - "大屏系统中启用AlarmPopup告警弹窗组件"
    - "排查告警弹窗未生效问题时快速定位启用状态"
    - "同类Vue项目中对内嵌告警组件执行安全启用操作"
keywords:
    - "AlarmPopup"
    - "自动弹窗"
    - "注释禁用"
source: "auto"
---

## 任务描述
前端远程值守大屏中，临时禁用 AlarmPopup 自动弹窗组件（8秒倒计时告警提示），保留 AlarmPopupPage 详情弹窗功能。

## 执行过程
```mermaid
graph TD
    A[需求:禁用AlarmPopup自动弹窗] --> B[全局搜索定位相关文件]
    B --> C[确认AlarmPopup为内嵌组件、无独立路由]
    C --> D[分析duty-screen/index.vue中5处引用位置]
    D --> E[SearchReplace注释模板标签、import语句、解构赋值、checkNew调用、cleanup清理]
    E --> F[验证无编译错误]
```

## 任务总结
成功注释 duty-screen/index.vue 中 AlarmPopup 全链路：模板、导入、逻辑解构、触发调用、卸载清理共5处，实现零侵入式临时禁用，不影响 AlarmPopupPage 详情弹窗及其他功能。
