---
title: "大屏页面右上角布局统一为duty-screen标准"
usage_scenario:
    - "未来进行全站UI组件统一改造时参考此流程"
    - "新页面开发需复用标准右上角布局时快速套用"
    - "排查页面右上角样式/功能不一致问题时定位改造方案"
keywords:
    - "右上角布局"
    - "duty-screen"
    - "导航下拉菜单"
source: "auto"
---

## 任务描述
将所有大屏页面（device-screen、data-center、video-surveillance、ai-analysis、ops-workbench、remote-console、large-screen、monitoring-center、alarm-screen）的右上角按钮布局统一为/duty-screen的标准结构：Grid导航下拉菜单（含全部页面入口）、Setting按钮、全屏按钮、用户按钮（动态昵称）。

## 执行过程
```mermaid
graph TD
    A[需求:统一右上角布局] --> B[分析duty-screen基准结构]
    B --> C[对比各页面现状差异]
    C --> D[逐页改造：模板替换导航下拉+用户按钮]
    D --> E[脚本新增navRoutes路由映射和handleNavCommand逻辑]
    E --> F[样式统一header-icon-btn透明风格]
    F --> G[验证所有页面编译无错误]
```

## 任务总结
成功实现9个页面右上角统一：所有页面均采用Grid导航下拉菜单（含8个标准入口）、Setting按钮、全屏按钮和动态用户按钮；脚本层通过useUserStore获取昵称，navRoutes统一管理路由映射；样式层全面应用header-icon-btn透明无边框规范，确保视觉与交互一致性。
