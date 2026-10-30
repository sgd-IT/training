---
title: "Vue组件下线时同文件具名工具函数阻碍死代码清理"
usage_scenario:
    - "下线某页面使用的浮层或弹窗组件"
    - "清理无人调用的Vue组件文件"
    - "删除组件后排查残留死逻辑"
keywords:
    - "SFC死代码"
    - "默认导出与具名函数混挂"
    - "auto-components.d.ts残留"
    - "双通道推送消费点"
    - "组件下线收尾"
source: "auto"
---

Bug class: 前端单文件组件（SFC）停用后残留死代码、无法安全删除文件

Root cause: 一个 .vue 文件同时 `export default` 组件本体又 `export function` 导出纯工具函数时，调用方常写成 `import Comp, { helper } from '.../index.vue'`。删除该页面的 <Comp> 挂载后，helper 仍被引用，于是文件的模板/setup 状态/样式全部成为无人调用的死代码却删不掉；同时自动导入声明文件（如 auto-components.d.ts）里还留有一条指向该组件的类型注册，构成第二处残留。

Fix pattern: 分两步收尾 —— ①先把仍被引用的纯函数从 SFC 迁到独立 utils 模块（无 Vue 依赖的 .ts），把各调用方 import 改为只指向 utils；②确认全局检索该组件名零命中后，再删除整个 SFC 文件及其在自动导入声明中的条目。删除前用「按组件名 + 按 ref 变量名 + 按事件回调函数名」三路 grep 反查消费点，尤其要覆盖轮询回调与 WebSocket 回调两条独立推送通道，否则会留下对已 null 的 ref 做可选链调用的静默死逻辑。

Reusable lesson: Don't 在组件确认无人使用后直接删除 .vue 文件 because 同文件导出的工具函数会连带编译失败、且自动导入声明残留会让类型检查报未定义；instead 先迁移仍被引用的函数再删文件。Applies when 下线某个 Vue/React 单文件组件或其浮层入口、或组件文件内混挂业务工具函数时；does not apply to 纯展示组件（文件内无任何具名导出）——那种可直接整文件删除。
