---
title: "RuoYi-Vue-Plus中inventory路由子路由注入机制"
usage_scenario:
    - "When implementing nested routing for dynamically loaded menu items"
    - "When adding child routes to an existing parent route in a RuoYi-Vue-Plus project"
    - "When troubleshooting 404 errors for nested route paths"
keywords:
    - "RuoYi-Vue-Plus"
    - "nested routing"
    - "rewriteRoutes"
    - "permission.ts"
    - "inventory route"
source: "auto"
---

The anwei-fireworks-retail-admin project uses RuoYi-Vue-Plus's dynamic menu system, which loads routes from the sys_menu database table. To implement nested routing for sub-pages like /store/inventory/records and /store/inventory/overview, child routes must be injected into the rewriteRoutes array (the processed route list) during the generateRoutes function in permission.ts, not into the sidebarRoutes or constantRoutes. The injection must occur after filterAsyncRouter processing but before setRoutes is called, targeting the specific inventory route by path match and adding children with proper redirect configuration.

## Notes
- 该注入机制在'进出货管理'菜单提级为顶级菜单后依然有效，因为permission.ts匹配的是相对路径'inventory'，与菜单层级无关。
