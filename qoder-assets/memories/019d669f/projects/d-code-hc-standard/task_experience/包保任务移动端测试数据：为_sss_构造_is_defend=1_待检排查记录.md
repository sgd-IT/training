---
title: "包保任务移动端测试数据：为 sss 构造 is_defend=1 待检排查记录"
usage_scenario:
    - "验证移动端包保任务显示与操作"
    - "清理或复用排查任务测试数据"
    - "再构造包保任务测试数据"
keywords:
    - "包保任务测试数据"
    - "is_defend=1"
    - "sss账号877"
    - "check_user_id过滤"
    - "克隆造数"
source: "user"
---

2026-09-28 在测试库 192.168.250.160 为 APP 端"包保任务"显示造数记录（克隆母本 0ad84c3ede3a48c2a8dca121662dd1ab→插入 c0de0003c0de0003c0de0003c0de0003 挂 877/sss，is_defend=1、check_status=0）【账号沿革·220 日志坐实】袁志明同手机号 18571056138：697（2025-03-07 建，登录名=老号 15871056138）→858（2026-01-19 建 46 秒后删）→862（2026-01-29 15:15:10 建）；2026-01-29 15:11/15:14 system1 两次删除 697，15:15:33~15:16:29 编辑 9 条包保配置时检查人员仍为 697（漏迁移），导致 7-28 第二批任务派给死账号。【停用真相与双通道机制·重大更正】9 条包保配置 status=1 的真因="风险单元停机"级联：液氯气化仓库（334b8aa659464ade8d5bda20c68fc8af）有停机记录 3 条（base_risk_unit_log，驼峰列名）：1-28 短停、7-26 起"停车检修"计划 9-15 恢复、9-22 起"检修"计划 10-05 恢复（当前在停机范围）。机制全景：①停机登记/恢复（BaseRiskUnitLogServiceImpl 与 BaseRiskUnitServiceImpl.updateStatus，入口 PUT /base/riskUnit/changeStatus=风险单元页"工作状态"开关）会级联 updateStatusByUnitId 批量改该单元全部排查任务，启（status=0）时另将 task_start_time/task_next_time 置 CURDATE()→次日 00:10（sys_job 104，cron 0 10 0 * * ?）生成新批次；恢复时还会把停机记录 activationTime 改写为操作时刻（终结停机范围、防回切）。②另有自动同步（DataSyncTask.syncRiskUnit @Scheduled 每天 6:00/23:30 → autoSyncUnitStatus：autoActivateExpiredUnits/autoDeactivateActiveUnits 按停机记录时间段自动切"风险单元"状态并推平台）——**只动风险单元表、不碰排查任务表**：故 10-05 后单元会自动变"开"，但任务仍停用、仍需人工补动作（删旧增新/SQL）才会重新派任务，这是系统自动化缺口。③若检修已实际结束可立即切"开"一步到位（不必等 10-05）。7 月批 9 条待检（挂 697）无需迁移。库中不存在 679 账号。
