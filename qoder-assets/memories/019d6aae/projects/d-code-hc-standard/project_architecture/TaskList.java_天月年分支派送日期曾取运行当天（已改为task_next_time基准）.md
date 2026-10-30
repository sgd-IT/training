---
title: "TaskList.java 天月年分支派送日期曾取运行当天（已改为task_next_time基准）"
usage_scenario:
    - "核对排查任务派送日期是否锚定配置"
    - "改动TaskList周期递推逻辑前确认现状"
    - "评估能否实现每周固定某天派送"
keywords:
    - "TaskList"
    - "task_next_time基准"
    - "alignToMonday"
    - "work_start_time"
    - "getDayOfBeforeDawn"
    - "MCP时间UTC偏移"
    - "待检派送两层定位"
    - "riskTaskRecord菜单权限"
source: "auto"
---

结构性事实（代码级）：ruoyi-quartz 的 TaskList.java 中，天/月/年周期的 risk_task_record 生成分支**从未读取 base_risk_task.work_start_time 作为日期来源**。2026-10-21 逐行复核确认现状为：

```java
record.setPlanTime(今天 + checkCycle 天/月/年 → 00:00:00);          // getTimeOfBeforeDawn(type, checkCycle)
record.setCreateTime(今天 + " " + item.getWorkStartTime());         // getDayOfBeforeDawn(3,0) 拼 work_start_time
taskHandleMapper.updateRiskTask(id, 今天 + checkCycle 天/月/年 → 00:00:00);
```

判读口径（务必区分两层）：
- **时刻层**：work_start_time 确实生效，决定 create_time 的 HH:mm:ss；未配置时后端兜底 "00:00:00"，因此线上记录看起来都在凌晨。
- **日期层**：work_start_time 完全不参与，日期一律取"job 运行那一天"（getDayOfBeforeDawn(3,0)），不是 task_next_time 那一天。周一 00:10 漏跑、周二补跑，记录就成周二派送，整体后移一天。
- 结论："派送时间填了却不生效"是误判；真实缺陷是"日期不锚定配置"。

配套事实链：
1. APP「待检」列表用 create_time <= NOW() 作派送闸门（BaseRiskTaskRecordMapper.xml），因此记录在 create_time 到点后才出现在待检列表。
2. BaseRiskTaskServiceImpl.java 新增任务时把 work_start_time/work_end_time 空值兜底为 "00:00:00"；前端 riskTask/index.vue 现已有 el-time-picker（value-format="HH:mm:ss"）入参，但线上 base_risk_task 全部 142 条 work_start_time 仍为 "00:00:00"（存量未回填）。
3. 小时周期分支的 create_time = planTimeEnd - checkCycle，即按班次逐段倒推（如 8 小时周期当日三条记录 create_time 为 00:00 / 08:00 / 16:00，plan_time 顺延一个周期）。
4. 系统只有 小时/天/月/年 四种周期单位，**没有"周"单位**（前端下拉与平台 RiskTaskSaveEntity.checkCycleUnit 均只认 1~4），因此不存在"固定每周几派送"的配置能力；7 天周期只能靠初始锚点碰巧落在某天。
5. 天/月/年分支的 check_count 恒为"当天（第1次）"，不像小时分支会累加；is_defend = work_type==0 ? "0" : "1"。

【时间字段判读口径（易踩坑）】MCP 查询返回的 datetime 值会被序列化成带 Z 后缀的 UTC 形式，数值比库内真实值偏移 8 小时。例如返回 "2026-10-10T16:00:00.000Z" 实际库里存的是 "2026-10-11 00:00:00"。判断 task_next_time / plan_time / create_time 是否已到期时，必须先用 DATE_FORMAT(col,'%Y-%m-%d %H:%i:%s') 取字符串原值比对，或直接与同一会话的 NOW() 做 SQL 内比较，禁止凭 JSON 返回值心算，否则会把"已派发"误判成"尚未到派发时间"。

【"任务没派发"的两层定位】第一层看任务侧：base_risk_task.status 是否为 0（启用）、deleted 是否为 0、task_next_time 是否已到（按上述口径换算）；第二层看记录侧：base_risk_task_record 是否已生成对应班次记录、其 create_time 是否 <= NOW()、check_user_id 是否为实际在岗人员的账号 ID 串。两层都正常而现场说没收到，通常是检查人员配置与实际值班人不一致，而非派发机制故障。

【管理后台与 APP 的可见性差异（2026-10-14 实测）】
1. 后台「风险管理→排查记录」（menu_id 2496，base/riskTaskRecord/index，perms base:riskTaskRecord:list）走 selectBaseRiskTaskRecordList，**不含 create_time <= NOW() 闸门**，因此未到派发时刻的后续班次记录在后台即刻可见，而 APP 要到点才显示——两侧不一致时用此解释，勿据后台有记录就断言 APP 应可见。
2. 该页搜索条件只有任务ID、应检人员、状态、同步状态、计划时间范围，**不支持按风险单元名称过滤**（前端表单与 SQL 均无 riskUnitName 条件）；要定位某单元记录须改用应检人员姓名或计划时间筛。按风险单元筛只能用「排查任务」页（menu_id 2490），其搜索区有 riskUnitId 下拉。
3. APP 端列表共四个页签：全部 getType=-1、待检 0、已检 1（后端映射为 check_status in (1,2,3)）、逾期 4，逾期记录不会消失只是换栏。
4. 「排查记录」页的同步按钮权限标识 base:riskTaskRecord:sync 未配置进 sys_menu 按钮权限，故除超管外任何角色看不到该按钮；线上这两个菜单仅授权给 role_id=2「系统管理角色」（账号 system、system1），admin 因超管身份天然全可见，其他角色需手工勾选菜单才可见。
5. 排查记录推送平台由 DataSyncTask.syncRiskTaskRecord 按 06:00 / 23:30 两批执行，完成时间在两批之间者 sync_status 仍为 0 属正常待批，不是推送故障。

## Notes
- 【2026-10-22 代码已改造，上文"日期一律取 job 运行那一天"不再成立】TaskList.java 天/月/年分支的递推基准已改为 item.getTaskNextTime()，并用 while(planDate<=now) 跳过积压轮次只补最近一轮；plan_time/create_time 不再以运行当天为起点。引用本条判断派送日期前，须先确认所查环境部署的是改造前还是改造后的包。
- 【新建任务锚点也已变】BaseRiskTaskServiceImpl.insertBaseRiskTask 现对非"小时"且非"1天"的周期调用 DateUtils.alignToMonday 把 task_start_time 写为本周一 00:00、task_next_time 写为本周一+周期；1 天与小时周期仍取次日 00:00。因此"首次派送日=创建次日、星期由创建日决定"只对 1 天/小时周期及未部署新包的旧环境成立。
- 【insert 的 mapper 坑（保留有效）】BaseRiskTaskMapper.xml 的 insert 原先 task_next_time 列名与取值都复用 taskStartTime 条件，导致 task_next_time 无法独立写入，需改为独立的 <if test="taskNextTime != null">。
- 【TaskHandleMapper 的 DATE_FORMAT（保留有效）】task_next_time 须经 DATE_FORMAT(a.task_next_time,'%Y-%m-%d %H:%i:%s') 输出，否则 String 映射带 .0 尾巴致 parseDate 返回 null 触发 NPE。
- 【派送时刻仍未强制】前端 el-time-picker 仍无默认值，用户不填则后端兜底 "00:00:00"，故"日期已是周一但时间是凌晨"仍会出现，需引导填写 08:00:00 或另改前端默认值。
