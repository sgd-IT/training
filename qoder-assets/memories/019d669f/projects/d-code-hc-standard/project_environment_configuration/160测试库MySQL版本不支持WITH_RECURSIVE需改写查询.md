---
title: "160测试库MySQL版本不支持WITH RECURSIVE需改写查询"
usage_scenario:
    - "在160测试库编写递归查询菜单或树结构"
    - "同一条SQL在两库间复用前的兼容性评估"
    - "遇到ERROR 1064语法报错定位版本差异"
keywords:
    - "WITH RECURSIVE不支持"
    - "ERROR 1064"
    - "MySQL版本差异"
    - "跨库SQL兼容"
    - "菜单祖先链查询"
source: "auto"
---

hc-standard 项目的两个同名 schema `tgr-gz-vue` 实例在 SQL 方言能力上不一致：测试库 192.168.250.160（主机名 WINDOWS-3D9R3V5）的 MySQL 版本不支持 CTE 递归语法，执行 `WITH RECURSIVE t AS (...)` 会直接报 ERROR 1064 (42000) 语法错误；正式库 220.203.23.144（anwei1-0001）支持同一条语句并正常返回结果。因此查 sys_menu 祖先链/树形层级等需要递归的场景，若目标含 160，必须改用逐层 parent_id 普通查询（如先按 menu_id 取父级、再按父级取名）或分多条 SELECT，不能依赖递归 CTE；跨库复用的脚本或交付 SQL 在提交前应按此差异做兼容性检查。
