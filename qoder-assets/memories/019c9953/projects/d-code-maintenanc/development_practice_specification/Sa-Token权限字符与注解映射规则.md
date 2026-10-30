---
title: "Sa-Token权限字符与注解映射规则"
usage_scenario:
    - "配置新菜单时填写正确的权限字符"
    - "排查用户无法访问页面或接口的权限问题"
    - "新增按钮级权限时确定对应的权限字符"
keywords:
    - "Sa-Token"
    - "@SaCheckPermission"
    - "权限字符"
    - "菜单权限"
---

项目采用Sa-Token权限框架，后端Controller接口方法通过@SaCheckPermission注解声明权限字符，如`weibao:standardItem:list`对应列表查询接口，菜单配置中的权限字符需与注解值严格一致才能实现页面和接口级权限控制。
