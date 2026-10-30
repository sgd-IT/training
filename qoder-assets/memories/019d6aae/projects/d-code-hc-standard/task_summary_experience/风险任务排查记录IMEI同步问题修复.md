---
title: "风险任务排查记录IMEI同步问题修复"
usage_scenario:
    - "当排查记录因 mobileMe 字段长度不符导致园区平台同步失败时"
    - "当需要为同步 SQL 添加数据质量兜底校验逻辑时"
    - "当诊断和修复与 mobileMe 字段相关的派送时间逻辑问题时"
keywords:
    - "IMEI"
    - "mobileMe"
    - "同步失败"
    - "长度校验"
    - "SQL兜底"
source: "auto"
---

## Task description
- Core requirement: 修复风险任务排查记录中 mobileMe 字段同步失败的问题
- Task background: 园区平台要求 IMEI 必须为15-17位，但刘林提交的记录中存储了14位错误值 '86133306029426'，导致参数验证失败；sync SQL 使用 COALESCE 逻辑优先使用记录自身值，未做长度校验，因此无法回退到用户表中的正确值

## Execution process
1. 数据库查询定位：通过 MCP 查询确认刘林的记录中 mobile_me = '86133306029426'（14位），而其 sys_user 表中 mobile_me = '861333060296426'（15位）
2. 代码分析定位：追踪 ApiRiskController.updCheckStatus() 中 SecurityUtils.getMobileMe() 的调用链，确认记录创建时捕获了错误的 IMEI 值
3. SQL 逻辑修复：修改 BaseRiskTaskRecordMapper.xml 中的 getSyncRecordList 和 getRecordListByDateRange 查询，将 COALESCE 替换为 CASE WHEN LENGTH(r.mobile_me) BETWEEN 15 AND 17 THEN r.mobile_me ELSE (select u.mobile_me ...) END，增加长度兜底校验

## Related files
- d:\code\hc-standard\ruoyi-system\src\main\resources\mapper\base\BaseRiskTaskRecordMapper.xml

## Notes
- 现有70条错误记录需手动重置 sync_status 为 '0'，以便下次同步时应用新逻辑
- 未来应确保移动端 APP 在提交前对 IMEI 进行格式校验，避免源头写入错误数据

## Task overview
成功修复了风险任务排查记录的 IMEI 同步问题：通过数据库查询和代码追踪准确定位了14位错误 IMEI 的来源；修改了两个核心同步 SQL 查询，增加了对 mobile_me 长度的强制校验，确保在记录值不合法时自动回退到用户表的正确值；提供了 SQL 脚本用于批量修复现有失败数据。
