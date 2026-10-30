---
title: "列表页 state.query 需声明为 Record 类型否则动态挂字段报 TS2339"
usage_scenario:
    - "给列表页查询条件包新增筛选字段"
    - "排查前端 TS2339 属性不存在报错"
    - "参照项目先例统一字典对象类型写法"
keywords:
    - "state.query类型声明"
    - "TS2339"
    - "Record<string"
    - "any>"
    - "yt-crud共享引用"
    - "空间管理页先例"
source: "auto"
---

前端列表页 state.query 的类型口径：写成 `query: {}` 会被 TypeScript 推断成"不允许任何属性的封闭空对象"，后续凡往查询条件包上挂字段（如 tenantId、productKey）全部报 TS2339 Property does not exist on type '{}'。项目已有正确先例——空间管理页写 `query: {} as Record<string, any>`，一个 as 断言即可解除同区域内所有该类报错（包括历史遗留的 productKey 那条）。由于 yt-crud 的搜索表单与页面 state.query 是同一对象引用，外部筛选条件直接挂在这个包上即可随查询一起提交，因此该字典对象必须一开始就用 Record<string, any> 形式声明，而不是遇到一次报错补一次。
