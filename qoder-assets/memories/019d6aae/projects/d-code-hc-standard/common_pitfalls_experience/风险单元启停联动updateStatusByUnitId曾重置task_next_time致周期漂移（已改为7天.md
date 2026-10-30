---
title: "风险单元启停联动updateStatusByUnitId曾重置task_next_time致周期漂移（已改为7天任务归一周锚）"
usage_scenario:
    - "定位排查任务派送星期逐日后移的根因"
    - "改造风险单元启停或调整autoSyncUnitStatus频率时"
    - "设计固定周期锚点避免启停重置基准"
keywords:
    - "autoSyncUnitStatus"
    - "updateStatusByUnitId"
    - "WEEKDAY归一周锚"
    - "定时任务提频执行"
    - "派送星期漂移"
    - "base_risk_unit_log"
source: "auto"
---

Bug类（历史）：停机检修到期后风险单元自动恢复"启用"，但该单元下排查任务仍"停用"，凌晨派单job(TaskList.addRiskTaskRecord，取数条件 status=0 AND task_next_time<=NOW)跳过，任务长期不派发。

根因（历史）：autoSyncUnitStatus()(DataSyncTask 每天06:00/23:30调用)中 autoActivateExpiredUnits/autoDeactivateActiveUnits 两条SQL只更新 base_risk_unit 表；而手动开关 updateStatus 和新增停机记录 insertBaseRiskUnitLog 都会调用 updateStatusByUnitId 联动任务，多条状态写入口不对称。

修复模式：新增 selectExpiredStopUnitIds/selectInWindowUnitIds(条件与自动启停SQL完全一致，须在执行前查询捕获待处理单元)，先循环调用 updateStatusByUnitId 联动任务、再更新单元，方法加 @Transactional；"任务先行"保证中断可自愈(下一轮job重查仍包含该单元，重复执行幂等)。

【联动已生效，但引入新的副作用（2026-10-21 实测）】updateStatusByUnitId 现被三个入口调用（人工 changeStatus、新增停机记录、自动启停），联动本身不再缺失；真正的问题是它在 status=0（启用）分支里无条件写 `task_start_time = CURDATE(), task_next_time = CURDATE()`：

```xml
<if test="status != null and status == 0">task_start_time = CURDATE(), task_next_time = CURDATE(),</if>
```

后果：每次单元"停用→启用"都把周期基准打回当天，次日 00:10 job 以"运行当天"为新起点重新递推，7 天任务的派送星期随之永久漂移。实测液氯气化仓库 10 条操作负责人任务（cycle=7天）轨迹为：周四(01-29~07-23) → 周二(07-28~09-22) → 周一(09-28~10-05) → 周五(10-09~10-16)，各段内部严格 7 天，段间间隔出现 5/6/4 天，三次重置点与 base_risk_unit_log 的 activationTime、base_risk_unit.update_time 逐一对齐（如 10-08 11:02:58 启用 → 10-09 起变周五）。

诊断口径：判断"周期任务为什么不在固定那天"，不要先看 cron，要先比对 task_start_time 是否等于某个单元启停时刻、以及相邻记录间隔是否 ≠ check_cycle；两者吻合即锁定为本重置逻辑。若业务要求"固定星期几派送"，必须同时改两处：生成侧以 task_next_time 为锚点递推（而非运行当天）、启用侧不再无条件重置 next_time（或新增锚定字段让 task_start_time 真正参与计算）。

## Notes
- 【2026-10-22 已按上述修复模式改造，`task_next_time = CURDATE()` 的无条件重置已不存在】BaseRiskTaskMapper.xml 的 updateStatusByUnitId 现改为条件 CASE 写法：仅对 check_cycle_unit='天' AND check_cycle=7 的任务把 task_start_time 归一为 DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)（本周一）、task_next_time 归一为本周一+7（下周一）；其余周期（1天/月/季/半年/小时）在启用时完全不碰时间字段，只更新 status。
- 【OGNL 比较口径保留有效】该 update 的 status 参数是 @Param("status") String，XML 里 `<if test="status != null and status == 0">` 依赖 OGNL 把 "0" 转数字比较，能工作但易误读为类型错误，改动时保留原写法。
- 【存量数据仍需一次触发才归一】代码上线不会自动修正已有锚点；线上 10 条 cycle=7 任务的 task_next_time 仍是 2026-10-23（周五），必须在「风险单元」页对该单元做一次"停用→启用"才会重排为下周一。注意启停会经 PlatementRiskCheckUnitApi.saveEnable 同步推送平台状态，操作前确认平台侧影响。
- 【设计取舍】启用时对 7 天任务做的是"有意的一次性跳变归一"（周五→下周一），不是漂移；归一后才稳定。诊断时不要把这一次跳变误判为修复失效。
