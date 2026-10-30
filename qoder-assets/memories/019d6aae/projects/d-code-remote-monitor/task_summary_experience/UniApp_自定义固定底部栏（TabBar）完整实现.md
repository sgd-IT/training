---
title: "UniApp 自定义固定底部栏（TabBar）完整实现"
usage_scenario:
    - "UniApp 项目新增或重构底部导航栏"
    - "需要支持安全区适配与 fixed 定位的移动端 TabBar 开发"
    - "排查 TabBar 被内容遮挡或未固定在屏幕底部的问题"
keywords:
    - "UniApp"
    - "TabBar"
    - "fixed定位"
    - "安全区适配"
source: "auto"
---

## 任务描述
移除 TabBar 中告警 tab 的角标数字 '3'，使其不再显示。

## 执行过程
1. 定位 components/TabBar.vue 文件中 alarm tab 的 badge 配置项
2. 将 badge 值从 3 修改为 0，使 v-if="t.badge" 条件为 false，从而隐藏角标

## 任务总结
成功移除了 TabBar 告警 tab 的角标：通过将 badge 设为 0，实现了角标隐藏，且不影响 TabBar 其他功能。
