---
title: "告警中心页面 emoji 全量 SVG 替换"
usage_scenario:
    - "移动端 Vue 项目中批量替换 emoji 为 SVG 的标准化流程"
    - "需在已有 textIcon 数据结构上扩展 SVG 支持时的模板+数据+样式三端协同改造"
keywords:
    - "emoji替换"
    - "SVG图标"
    - "告警中心"
    - "icon-location.svg"
source: "auto"
---

## 任务描述
移动端告警中心页面中剩余 emoji 图标（⏰、⚠ 和 📍）全部替换为 SVG 图标。

## 执行过程
```mermaid
graph TD
    A[需求:替换告警中心emoji] --> B[Grep定位alarm-center/index.vue中⏰、⚠和📍]
    B --> C[创建icon-clock.svg和icon-location.svg并存入static/]
    C --> D[SearchReplace将⏰和📍替换为<image src="/static/icon-clock.svg">和<image src="/static/icon-location.svg">]
    D --> E[读取index.vue确认fault项结构]
    E --> F[扩展模板：新增v-if="c.svg"分支渲染SVG]
    F --> G[更新categories中fault项：textIcon→svg字段指向icon-fault.svg]
    G --> H[补充.cat-svg样式确保尺寸一致]
```

## 任务总结
成功清除告警中心全部 emoji：⏰ 替换为自建 icon-clock.svg，📍 替换为自建 icon-location.svg，⚠ 复用首页 icon-fault.svg；模板增强支持 svg 字段优先渲染，样式同步适配 28rpx 尺寸。
