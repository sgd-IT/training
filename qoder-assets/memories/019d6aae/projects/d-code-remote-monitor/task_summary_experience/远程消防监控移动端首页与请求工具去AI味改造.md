---
title: "远程消防监控移动端首页与请求工具去AI味改造"
usage_scenario:
    - "对uni-app项目进行‘去AI味’工程化改造时参考整体流程"
    - "消防/安防类系统UI配色与图标选型标准制定"
    - "uni-icons在真实项目中的规模化替换实践"
keywords:
    - "去AI味"
    - "uni-icons"
    - "消防配色"
source: "auto"
---

## 任务描述
为远程消防监控移动端首页（index.vue）及请求工具（request.js）去除AI生成风格，转向真实工程代码风格。

## 执行过程
```mermaid
graph TD
    A[需求:去AI味] --> B[第一轮：精简结构]
    B --> C[request.js：删getToken封装、简化错误处理]
    C --> D[index.vue：删空方法/Toast占位、删loadDemoData、缩写变量名、合并CSS属性]
    D --> E[第二轮：专业视觉重构]
    E --> F[替换全部emoji为uni-icons组件]
    F --> G[告警类型配色切换为消防行业深色调：火警#d84315/故障#e65100/监管#1565c0/屏蔽#2e7d32]
    G --> H[头部渐变改为#c62828→#8e0000，卡片阴影/圆角/字号整体收敛]
```

## 任务总结
成功实现代码风格转型：request.js采用轻量Promise封装，index.vue使用uni-icons替代emoji、建立等级关联的标签配色体系、UI细节全面克制化，整体符合消防监控系统专业、稳重、可维护的工程规范。
