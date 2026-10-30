---
title: "维保系统业务角色SQL初始化"
usage_scenario:
    - "在同类RBAC系统中新增业务角色时快速生成标准SQL脚本"
    - "排查角色数据导入失败问题时核对字段顺序和data_scope取值"
    - "参考role_key命名规范和备注编写方式"
keywords:
    - "角色初始化"
    - "sys_role"
    - "data_scope"
    - "wb_roles.sql"
---

## 任务描述
为智慧消防维保系统新增5个业务角色（维保单位管理员、技术主管、维保技术员、客户管理员、客户用户），需符合现有RBAC权限体系。

## 执行过程
```mermaid
graph TD
    A[需求:新增5个业务角色] --> B[搜索sys_role表结构和样例数据]
    B --> C[定位ry_vue.sql确认字段定义与data_scope取值含义]
    C --> D[读取wb_menu.sql了解菜单体系]
    D --> E[设计角色权限矩阵：role_key命名规范、data_scope匹配业务场景、排序与备注说明]
    E --> F[生成wb_roles.sql：使用Oracle语法、完整字段顺序、sysdate()时间函数、清晰注释]
```

## 任务总结
成功生成wb_roles.sql脚本，包含5条INSERT语句：严格遵循sys_role表字段顺序（role_id, role_name, role_key, role_sort, data_scope, ...），data_scope按业务需求精准配置（1/2/4/5），role_key采用wb_前缀统一标识维保业务角色，每条记录含角色职责说明作为remark，支持Oracle数据库直接执行。
