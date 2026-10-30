---
title: "PC管理台新增更新日志页面与登录提醒弹窗"
usage_scenario:
    - "为管理台新增面向全体用户的说明类页面"
    - "设计登录后一次性提醒弹窗的挂载位置"
    - "评估复用公告或站内信能力"
keywords:
    - "更新日志页面"
    - "changelog静态数据源"
    - "登录后弹窗App.vue"
    - "remaining.ts菜单渲染"
    - "公告站内信复用评估"
source: "auto"
---

## Task description
- Core requirement: 在 PC 管理后台（anwei-fire-remote-monitor-vue）新增一个让使用者看到每次功能更新的页面，并提供登录后提醒入口。
- Task background: 项目此前无任何更新日志/changelog 页面；框架自带 system_notice 通知公告与 system_notify_message 站内信两套现成能力，需先判断是否复用。

## Execution process
1. 框架能力盘点结论（决定不复用的依据）：通知公告读取接口带 @PreAuthorize('system:notice:query')，普通使用者角色无该权限读不到，且无"已读"概念、前端无面向使用者的展示页；公告的 websocket 推送仅 views/infra/webSocket 测试页处理了 notice-push，正式布局无人监听；站内信虽有已读/未读数，但内容是模板化短消息，撑不起"版本号 + 日期 + 变更条目"的日志形态。最终采用前端静态数据方案，零后端改动、零数据库表。
2. 新建 src/views/system/changelog/data.ts：CHANGELOG 数组作为唯一数据源（version/date/title/items[{type,text}]，type 为 feat/opt/fix），CHANGE_TYPE_LABEL 做类型中文映射；发版时只需在数组最前面追加一条。初版内容按近期 git 提交整理出 V0.9.0~V1.2.0 四条（版本号属暂定）。
3. 新建 src/views/system/changelog/index.vue：顶部概览区 + 按版本倒序的卡片列表，条目带「新功能/优化/修复」彩色标签。
4. 新建 src/views/system/changelog/VersionUpdateDialog.vue：受控 el-dialog（v-model），展示最新一条的前 5 个条目，关闭时把最新版本号写入缓存标记已读，提供"查看完整更新"跳转。
5. src/hooks/web/useCache.ts：CACHE_KEY 新增 CHANGELOG_READ_VERSION。
6. src/router/modules/remaining.ts：注册 /changelog 静态路由（component: Layout + 唯一子路由 path: ''）。
7. src/layout/components/UserInfo/src/UserInfo.vue：用户下拉菜单增加常驻入口，push('/changelog')。
8. 弹窗挂载位置的关键决策：最初考虑挂 Layout.vue，但实测登录后超管/monitor_role/fire_watcher 跳 /duty-screen、其他角色跳 /monitoring-center，两者都是不经过 Layout 的全屏路由，挂 Layout 永不触发；故改挂 App.vue，用 watch(route.path) + versionChecked 单次标志，跳过登录类路径、校验 wsCache.get(CACHE_KEY.USER) 存在后比较已读版本决定是否弹。
9. 命名口径修正（用户反馈）：web 系统不存在"要不要更新安装"的语义，全部用户可见文案由"版本更新/当前版本"统一改为"更新日志/最新更新"，涉及路由 title、下拉菜单项、页面徽标、注释与缓存 key 说明。
10. 菜单可见性（用户追问）：确认 permission store 中 `this.routers = cloneDeep(remainingRouter).concat(routerMap)`，即侧边栏菜单 = remaining 静态路由 + 后端动态菜单合并渲染，且 useRenderMenuItem 会把父路由 + 唯一显示子路由合并成单个菜单项；故只需去掉 /changelog 父子路由的 hidden: true 即可出现在菜单，无需动 system_menu 表和角色分配。副作用：静态路由 concat 在前，该项出现在侧边栏最上方。
11. 验证：node node_modules\eslint\bin\eslint.js 逐文件通过；vue-tsc --noEmit 过滤本次文件无报错（全量仅剩 LockPage.vue 未使用 resetRouter、store/modules/app.ts 缺 collapse/fixedMenu 两处存量错误）。

## Related files
- /src/views/system/changelog/data.ts
- /src/views/system/changelog/index.vue
- /src/views/system/changelog/VersionUpdateDialog.vue
- /src/hooks/web/useCache.ts
- /src/router/modules/remaining.ts
- /src/layout/components/UserInfo/src/UserInfo.vue
- /src/App.vue

## Notes
- SearchReplace 插入路由时误吞了相邻 /user 路由 children 的开括号与其子块缩进，连续三次修补才恢复；教训：向数组中间插入元素时 original_text 不要以 `{` 结尾，插入后必须立即 Read 复核边界行。
- remaining.ts 编辑期间 GetProblems 返回"Language server is not ready or still initializing"，此时报出的语法错误可能是语言服务初始化中的假象，应以 eslint/vue-tsc 实跑结果为准。
- PowerShell 下 npx 被执行策略拦截，前端静态校验须用 node 直调 node_modules 下的 bin。

## Task overview
已完成：更新日志页面 + 登录后一次性弹窗 + 用户下拉入口 + 侧边栏菜单入口全部落地，文案统一为 web 场景的「更新日志」，后端与数据库零改动；ESLint 与 vue-tsc 校验通过。遗留：data.ts 内 V0.9.0~V1.2.0 版本号与措辞为暂定，待用户自行校准；若希望该项排在业务分组中间而非侧边栏首位，需改走后端 system_menu 方案。
