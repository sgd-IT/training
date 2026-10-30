---
title: "prod值守类角色配置现状：无fire_watcher且duty_person零权限"
usage_scenario:
    - "核查或开通值守角色大屏权限"
    - "区分test与prod角色差异"
    - "配置值班人员角色菜单授权"
keywords:
    - "system_role"
    - "monitor_role"
    - "duty_person"
    - "fire_watcher缺失"
    - "dashboard:query权限"
source: "auto"
---

生产库 system_role 实测（2026-10）：不存在 test 库中的 fire_watcher 角色。值守相关角色实际为：monitor_role（id=162，tenant_id=1 系统租户，已授 eiot:firewatch:dashboard:query，菜单 3001/3101）、duty_person（id=169，名称"值守人员"，属碧桂园租户 174，在 system_role_menu 无任何授权记录，无法访问大屏与值班接口）、各企业租户仅有 tenant_admin（多数已授 dashboard:query）。安为相关租户为 165 安为科技 / 166 广东安为安全科技有限公司 / 167 安为园区。做角色权限核查或开通时必须先确认所连库实例，不能拿 test 的角色 code 套 prod。
