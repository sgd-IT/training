---
title: "NavBar组件theme与border外观属性契约及滚动联动用法"
usage_scenario:
    - "开发带深色或纯色头图的移动端页面"
    - "实现导航栏随页面滚动变色需求"
    - "评估是否需要修改共享NavBar组件"
keywords:
    - "NavBar theme属性"
    - "border描边"
    - "transparent导航栏"
    - "onPageScroll联动"
    - "状态栏避让"
source: "auto"
---

移动端 components/NavBar/NavBar.vue 原生提供三个外观属性，无需改动共享组件即可适配深色头图页面：

- theme：light（白底黑字，默认）| dark（深蓝 #16255f 底白字）| transparent（完全透明覆盖在头图上，文字白色）
- border：布尔，控制底部描边
- 组件内部已按 --status-bar-height + 44px 处理状态栏与胶囊避让

结构性事实（2026-10 全仓 Grep 核实）：仓库内此前没有任何页面使用过 theme="dark" 或 theme="transparent"，各页面一律沿用默认 light。因此"深头浅身"类页面（如 pages/alarm-detail/index.vue）的通行做法是：头图区用 transparent 让导航栏融入纯色头图，配合页面 onPageScroll 生命周期在滚动超过约 180px 时把 theme 切为 light、同时打开 border，形成透明↔白底联动。该模式仅依赖 NavBar 既有能力，不涉及组件源码修改，也不会影响其他页面。
