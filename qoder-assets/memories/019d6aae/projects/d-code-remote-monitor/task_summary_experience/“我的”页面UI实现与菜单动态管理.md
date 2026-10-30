---
title: "“我的”页面UI实现与菜单动态管理"
usage_scenario:
    - "开发类似个人中心页（含用户信息+多组功能菜单）时快速复用结构与样式"
    - "需要为菜单项添加角标、附加文案或跳转标识的场景"
    - "根据产品需求动态增删菜单配置项的维护流程"
keywords:
    - "我的页面"
    - "菜单卡片"
    - "uni-icons"
    - "SCSS scoped"
source: "auto"
---

## 任务描述
实现移动端“我的”页面UI，严格对照设计稿，支持顶部用户信息展示、两组菜单卡片（含角标/附加文字）、版本信息及底部TabBar，并支持后续菜单项动态删减。

## 执行过程
```mermaid
graph TD
    A[需求:实现“我的”页面] --> B[读取pages/profile/index.vue原始结构]
    B --> C[重写template：构建user-header、menu-card×2、version-text]
    C --> D[重写script：注入userInfo模拟数据 + menuGroup1/menuGroup2数组 + onMenuTap占位方法]
    D --> E[重写style：采用SCSS scoped，实现渐变头像区、圆角图标容器、阴影卡片、响应式间距]
    E --> F[用户追加需求：删除2个带“跳转”标记的菜单项]
    F --> G[SearchReplace精准移除patrol/device两项配置对象]
```

## 任务总结
成功交付高保真“我的”页面：深蓝渐变用户头像区、8+4结构菜单卡片（含角标与附加文案）、uni-icons统一图标体系、SCSS模块化样式；所有菜单点击有toast反馈；支持通过修改data数组灵活增删菜单项。
