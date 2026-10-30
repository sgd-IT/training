---
title: "fireworks 模块权限标识符命名规范"
usage_scenario:
    - "当为 fireworks 模块新增后端接口并配置权限时"
    - "当为前端按钮添加 v-hasPermi 权限控制时"
    - "当为监管模块新增接口（如 /retail-options）配置权限时"
keywords:
    - "permission identifier"
    - "fireworks:retail:list"
    - "fireworks:person:list"
    - "fireworks:inventory:list"
    - "fireworks:regulator:retail-options"
    - "@SaCheckPermission"
source: "auto"
---

fireworks 模块所有后端接口的权限标识符（perms 字段）均以 `fireworks:` 开头，后接业务模块名和操作类型，例如 `fireworks:retail:list`、`fireworks:person:add`、`fireworks:inventory:remove`；权限标识符必须与后端 Controller 的 `@SaCheckPermission` 注解值严格一致。
