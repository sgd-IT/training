---
title: "包保三页菜单落点与嵌入型组件的 person 依赖约束"
usage_scenario:
    - "调整包保相关页面的菜单入口或可见性"
    - "排查包保页面新增修改报 TypeError"
    - "把弹窗嵌入组件改造为独立菜单页"
keywords:
    - "包保管理菜单结构"
    - "props person 依赖"
    - "弹窗嵌入组件转独立菜单"
    - "sys_menu 2549 2562"
    - "implementperson implementtask"
source: "auto"
---

平台对接(sys_menu 2503)下的包保相关页面落点为：目录"包保管理"(2549，原名"包保责任人")下挂 包保责任人(platement/implementperson, 2562)、包保履职(platement/implementtask, 2568)、从业人员(platement/practitioners, 2574)。implementperson 与 implementtask 两个组件都设计成可被从业人员页弹窗嵌入（practitioners/index.vue 以 <person :person="form"> 方式引入 implementperson），因此它们内部大量逻辑直接读取 this.person（reset/created/getList 用 person.phone、basicPractitionersName 等预填并做人员过滤），props 默认值曾误写为 () => {} 实际返回 undefined。后果：这两个页面作为独立菜单访问时 this.person 为 undefined，新增/修改会抛 TypeError；把此类"弹窗嵌入组件"放成独立菜单前必须先做 props 空值容错（const person = this.person || {}）并去掉隐式人员过滤。菜单可见性改动需重新登录或刷新才会生效（RuoYi 动态路由在登录时构建）。

## Notes
- 【2026-09-22 补充】正文关于编辑表单字段的描述已过时：implementperson 的"从业人员"和"危险源编码"两项现已全部改为下拉框选择（不再是远程搜索必填姓名输入或手工输入框），其中危险源编码的数据源是风险对象档案 listRiskobj（需 base:riskobj:list 权限），因此该页面还额外产生了对"风险对象"模块接口的权限依赖；给普通账号开放包保责任人菜单时必须同时授予风险对象列表权限，否则下拉静默加载失败。嵌入从业人员页弹窗的 props.person 预填行为仍保留不变，上述空值容错约束依然有效。
