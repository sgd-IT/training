---
title: "方法级安全下 Controller 间委托调用触发被调方 @PreAuthorize 双检"
usage_scenario:
    - "排查改了权限注解仍然403"
    - "设计移动端或对外接口入口"
    - "审查Controller间注入委托调用链"
keywords:
    - "方法级安全"
    - "PreAuthorize二次校验"
    - "Controller委托注入"
    - "直调Service"
    - "CGLIB代理调用"
source: "auto"
---

Bug class: App 控制器注入 admin 控制器做“薄转发”时，被委托方法的 @PreAuthorize 会被静默执行两次——只改 App 侧的权限注解无法放开权限，非超管账号依旧 403。
Root cause: @EnableMethodSecurity 会为含 @PreAuthorize 方法的 Bean 生成 CGLIB 代理；跨 Bean 调用（含 Controller→Controller 的 @Resource 注入后调用）走的是代理，会触发被调方的方法级安全校验；只有类的自调用（this.xxx）不触发。
Fix pattern: 需要放开权限的入口不要委托到带 @PreAuthorize 的 Bean，改为直调其下层的 Service；权限口径只写在入口方法自身。判断存量代码是否有此隐患：看注入字段类型是否为带权限注解的 Controller。
Reusable lesson: 遇到“改了权限注解仍然 403”时，先排查调用链上还有哪一层带 @PreAuthorize；适用于本仓库所有启用方法级安全的模块，不适用于调用链上全是无注解纯服务的场景。
