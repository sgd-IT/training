---
title: "管理后台顶栏搜索框缺失根因定位与appStore.search初值修复"
usage_scenario:
    - "排查管理后台顶栏某图标不显示"
    - "定位appStore布尔开关未渲染问题"
    - "恢复ToolHeader搜索入口"
keywords:
    - "ToolHeader"
    - "appStore.search"
    - "RouterSearch"
    - "顶栏图标不显示"
    - "app.ts state初值"
source: "auto"
---

## Task description
- Core requirement: 用户反馈"管理后台右上角少了个搜索"，需恢复顶栏搜索图标。
- Task background: 管理后台 layout 顶栏组件 ToolHeader.vue 用条件渲染控制各图标显示，搜索项依赖 appStore.search；vue-element-plus-admin 模板的 store/modules/app.ts 定义 AppState 接口含 search 字段。

## Execution process
1. 定位 ToolHeader.vue（src/layout/components/ToolHeader.vue）第87行 `{search.value ? <RouterSearch isModal={false} /> : undefined}`，search 取自 computed(() => appStore.search)。
2. 排查 src/store/modules/app.ts：AppState 接口声明了 search: boolean，但 state 初始化对象漏赋 search 初值 → appStore.search 为 undefined → 条件恒假 → RouterSearch 从不渲染。
3. git log 确认该仓库仅一个初始提交，此缺失自项目初始化即存在（非人为删除）。
4. 修复：在 state 初始化中补 `search: true`，位置放在 screenfull 与 size 之间，与接口字段顺序一致。

## Related files
- /anwei-fire-remote-monitor-vue/src/store/modules/app.ts

## Notes
- ToolHeader 里其他图标（screenfull/size/locale/message）均通过 getter（如 getScreenfull）读取，唯独 search 直接读 state 字段，属模板遗留的不一致；最小修复只需补 state 初值，无需新增 getter/action。
- Setting 面板 InterfaceDisplay.vue 无 search 开关项，语言包也无对应 key，说明该开关未接入设置面板。

## Task overview
已完成：根因为 app.ts 的 state 初始化漏给 search 赋值导致其恒为 undefined；补上 search: true 后管理后台右上角恢复显示搜索图标（放大镜），点击展开输入框可按菜单标题/路径模糊匹配跳转路由，刷新页面即生效。
