---
title: "RuoYi前端列表页响应数据解包逻辑错误修复"
usage_scenario:
    - "RuoYi框架下所有列表页（如客户、维修单、设备）数据不显示时的快速定位与修复"
    - "排查TS类型报错 'xxx上不存在属性rows/total' 的根本原因"
    - "前端调用API后dataList.value为空时检查响应拦截器是否已解包"
keywords:
    - "RuoYi"
    - "响应拦截器"
    - "res.data.rows"
    - "TS类型报错"
---

## Task description
- Core requirement: 修复 RuoYi 前端客户管理列表页（/weibao/customer/list）数据无法显示的问题
- Task background: RuoYi框架的axios响应拦截器默认返回res.data（即{code, data: {rows, total}, msg}结构），但前端代码误按原始响应结构访问res.rows，导致TS类型报错且dataList.value为空

## Execution process
1. 定位问题：检查HTTP响应体结构，确认后端已正确返回contractEndDate/deviceCount等新字段；读取request.ts确认响应拦截器返回res.data
2. 修复前端逻辑：在list.vue中将res.rows改为res.data.rows，并添加as any绕过TS类型校验
3. 验证修复效果：重新加载页面，客户列表数据正常显示，控制台无TS报错

## Related files
- d:\code\maintenanc\anwei-fire-maintenance-admin\src\views\weibao\customer\list\index.vue
- d:\code\maintenanc\anwei-fire-maintenance-admin\src\utils\request.ts

## Notes
- None

## Task overview
成功修复RuoYi前端列表页响应数据解包逻辑错误：确认并应用了响应拦截器返回res.data的结构约定；修复了前端list.vue中res.rows的错误访问路径；添加as any绕过TS类型不匹配报错；客户列表数据正常显示。

## Notes
- 原记忆标题中“响应数据解包逻辑错误”易误解为拦截器本身有bug；实际是前端访问路径未适配拦截器的解包行为。
