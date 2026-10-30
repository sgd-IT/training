---
title: "Vue动态路由component路径必须严格匹配views下真实文件路径"
usage_scenario:
    - "菜单点击后页面空白，检查发现component路径与实际.vue文件位置不符"
    - "使用import.meta.glob动态路由时，数据库菜单表component字段配置错误"
    - "前端路由加载失败且控制台无报错，怀疑路径映射问题"
keywords:
    - "import.meta.glob"
    - "动态路由"
    - "component路径"
    - "菜单配置"
---

前端使用 `import.meta.glob('./../../views/**/*.vue')` 动态加载路由组件，因此 `sys_menu.component` 字段必须精确对应 `src/views/` 下的实际 `.vue` 文件路径（不含 `src/views/` 前缀），且路径中不能存在实际不存在的目录层级（如 `deviceAsset`）。例如：文件 `src/views/weibao/device/index.vue` 对应的 component 值必须为 `weibao/device/index`，若误配为 `weibao/deviceAsset/index` 则路由无法加载，页面空白。（来源：Grep、CallMcpTool、Read）
