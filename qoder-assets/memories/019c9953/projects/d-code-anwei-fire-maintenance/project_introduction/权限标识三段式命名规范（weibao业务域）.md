---
title: "权限标识三段式命名规范（weibao业务域）"
usage_scenario:
    - "新增接口权限注解时构造标准权限标识"
    - "配置菜单权限字段时遵循统一格式"
    - "审查或迁移权限逻辑时验证标识结构一致性"
keywords:
    - "权限标识"
    - "weibao"
    - "三段式"
    - "SaCheckPermission"
    - "权限控制"
---

项目权限标识采用英文冒号 `:` 分隔的三段式格式：`模块:资源:操作`，**所有标识必须以 `weibao:` 开头**，体现业务域隔离，例如 `weibao:contract:list`、`weibao:customer:query`；常用操作包括 list、query、add、edit、remove、export 等，该格式用于 `@SaCheckPermission` 注解及菜单表 `sys_menu` 中的权限字段配置。
