---
title: "BMapGL 地图初始化需防组件卸载后执行"
usage_scenario:
    - "Vue 组件中使用百度地图 GL API 初始化地图时报 Cannot read properties of undefined (reading 'clientWidth')"
    - "组件切换或关闭后地图相关定时器仍在运行导致 DOM 访问异常"
keywords:
    - "BMapGL"
    - "Vue生命周期"
    - "setTimeout清理"
    - "clientWidth错误"
source: "auto"
---

百度地图 GL（BMapGL）在组件卸载后仍执行 `initMap` 中的 `setTimeout` 回调，导致尝试访问已移除 DOM 元素的 `clientWidth` 报错。必须在组件 `onUnmounted`/`onBeforeUnmount` 中设置 `mapDestroyed = true` 并清除 `mapInitTimer`，且 `initMap` 开头需检查 `if (mapDestroyed) return`。（来源：Vue 组件生命周期与 BMapGL 集成）
