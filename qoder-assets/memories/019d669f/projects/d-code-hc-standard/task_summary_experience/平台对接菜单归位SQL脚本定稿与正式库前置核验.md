---
title: "平台对接菜单归位SQL脚本定稿与正式库前置核验"
usage_scenario:
    - "整理待上正式库清单中的SQL为可执行脚本时"
    - "编写跨环境通用的sys_menu迁移语句时"
    - "核验正式库菜单结构是否满足变更前置条件时"
keywords:
    - "菜单迁移SQL"
    - "move_platform_menus"
    - "sys_menu parent_id"
    - "component定位跨库"
    - "正式库前置核验"
source: "auto"
---

## Task description
- Core requirement: 用户要求从《待上正式库变更清单.md》中直接整理出可在正式库执行的 SQL，由用户本人负责执行。
- Task background: 清单 #2 记录了"平台对接类菜单归位"需求——将服务记录(serviceRecord)、第三方单位(thirdPartyUnit)、装置开停车(deviceCtrl)、大检修(overhaul) 四个菜单从"基础管理"(path=base, menu_id=2000) 移到"平台对接"(path=platement) 目录下，排序接在已有三项之后(order_num=4~7)。脚本文件 move_platform_menus_to_platement.sql 此前已存在于仓库但需补全执行前/后自检语句。

## Execution process
1. 用命令行 mysql.exe 直连正式库 220.203.23.144/tgr-gz-vue 执行只读核验查询：确认 path='platement' AND menu_type='M' AND parent_id=0 的根目录有且仅有一条(menu_id=2503)，以及 4 个目标 component 各只有 1 条记录（即每条 UPDATE 预期影响 1 行共 4 行）。
2. 更新 ruoyi-admin/src/main/resources/sql/move_platform_menus_to_platement.sql：补全 SET NAMES utf8mb4、执行前自检 SELECT（BEFORE + ROOT）、4 条 UPDATE（以 component 定位、用嵌套子查询 `(SELECT t.mid FROM (SELECT menu_id AS mid FROM sys_menu WHERE path='platement' AND menu_type='M' AND parent_id=0 LIMIT 1) t)` 动态取目标 parent_id）、执行后自检 SELECT（AFTER）。
3. 在聊天中以完整可复制的 SQL 代码块形式交付给用户，附带执行要点说明（备份 sys_menu、重新登录刷新路由、预期影响行数）。

## Related files
- /ruoyi-admin/src/main/resources/sql/move_platform_menus_to_platement.sql

## Notes
- 正式库 menu_id 与测试库不同，脚本必须以 component 字段而非 menu_id 作为 WHERE 条件，以 path='platement' 动态定位目标 parent_id——这种写法保证同一脚本在两库间通用无需修改。
- MySQL 不允许 UPDATE 的子查询直接引用被更新的表（sys_menu），需用派生表包装（SELECT ... FROM (SELECT ...) t）绕过限制。
- 重复执行该脚本不会报错（幂等），因为 UPDATE 条件匹配后设置的值与实际结果一致。

## Task overview
已完成：move_platform_menus_to_platement.sql 脚本定稿并通过正式库前置条件核验（2503 唯一、4 菜单各 1 条），SQL 以可复制格式交付用户，等待用户在正式库执行后反馈结果以更新清单状态。
