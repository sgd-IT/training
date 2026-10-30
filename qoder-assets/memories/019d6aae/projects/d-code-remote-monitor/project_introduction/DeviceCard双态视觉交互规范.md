---
title: "DeviceCard双态视觉交互规范"
usage_scenario: []
keywords:
    - "DeviceCard"
    - "双态设计"
    - "CSS变量"
    - "悬停变色"
    - "视觉状态"
source: "user"
---

DeviceCard.vue 组件采用双态视觉设计：默认状态下整体为灰色系（标题、按钮、图标、数值、Tab、元数据等均使用灰阶色值），仅在鼠标悬浮或点击激活时切换为彩色状态（青色、橙色、黄色等），所有状态切换均通过 CSS 自定义属性控制，并配备 0.35s 平滑过渡动画。
