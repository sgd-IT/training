---
title: "氯化锌排查任务派发核查与管理员可见性及APP规则说明交付"
usage_scenario:
    - "现场反馈某单元排查任务未派发时的取证流程"
    - "回答管理员能否在后台自助查看排查记录"
    - "为一线班组编写双预防APP使用规则说明"
keywords:
    - "氯化锌排查任务核查"
    - "check_status逾期标记"
    - "排查记录后台可见性"
    - "APP使用说明产出"
    - "定时任务派发取证"
source: "auto"
---

## Task description
- Core requirement: 核查风险单元"氯化锌生产装置"今天与昨天的排查任务是否按时派发，回答"管理员能否直接在系统平台上看到"，并针对一线使用者"很迷糊"产出可转发的 APP 使用规则说明
- Task background: 双重预防排查记录由 job 104（TaskList.addRiskTaskRecord，cron 0 10 0 * * ?）每日生成，job 105（TaskList.updateRiskTaskRecordStatus，cron 0 5/10 * * * ?）按 plan_time 刷逾期；APP 待检闸门为 create_time <= NOW()；数据源为 MCP 只读服务 tgr-gz-vue 的 mysql_query

## Execution process
1. 用 information_schema.COLUMNS 先取列名，纠正 base_risk_unit 名称列为 risk_unit_name（非 name），筛出唯一生效单元 b24cefde82e1404f8d5482c9adc34415（status=0、deleted=0），另两条同名记录已删除
2. 核 base_risk_task：该单元 27 条任务全部启用未删，其中 6 条 8 小时周期任务应检人为"文茂华,全正华,冯中卫"（check_user_id 830,832,720），8 条日周期任务
3. 核 base_risk_task_record：10-13 与 10-14 各生成 26 条（日周期 8 + 8 小时任务 6×3 班），数量齐全；所有 datetime 统一用 DATE_FORMAT 取字符串并与同会话 NOW() 比较，规避 UTC 序列化偏移
4. 定位 10-13 中班（第3次）6 条 check_status=4、create_by 为空：Grep TaskHandleMapper.xml 确认 updateRiskTaskRecord 语义为 `set check_status=4 where check_status=0 and plan_time <= NOW()`，即到期未做被自动标逾期，非派发失败
5. 核 sys_job_log（无 job_id 列，按 invoke_target/job_name 过滤）：job 104 在 10-13 00:10、10-14 00:10 均成功；job 105 每 10 分钟正常
6. 核推送：DataSyncTask.syncRiskTaskRecord 为 06:00 / 23:30 两批，10-13 完成的 20 条全部 sync_status=1；10-14 完成的 20 条中 8 条（06:43、08:43 完成）sync_status=0 属未到批次而非故障
7. 管理员可见性核查：查 sys_menu 确认"风险管理(2327)→排查任务(2490)/排查记录(2496)"两页面已配置；查 sys_role_menu 确认仅 role_id=2「系统管理角色」（账号 system、system1）获授权，admin(user_id=1) 因超管全可见；读 riskTaskRecord/index.vue 确认该页无风险单元搜索条件，只能按应检人员/计划时间筛，而 riskTask/index.vue 有 riskUnitId 下拉
8. APP 端代码级核实以支撑说明文案：ActivityRiskManagement.java 四页签（全部-1/待检0/已检1/逾期4）；ActivityRiskManagementDetails.java 中"正常/异常"按钮仅在 checkStatus=="0" 显示，故逾期后手机端无法补做；提交携带经纬度且 check_time 不得大于当前时间，点异常跳隐患上报；RiskManagementAdapter.java 卡片不显示"第N次"，班次只在详情页
9. 产出《风险排查任务使用说明》可直接发工作群（任务几点来/为何一天多条/看不到原因/怎么做/多人共担归属/逾期后果/完成后去向），并列出四处建议补充的界面提示（空态提示、逾期不可补做红字提醒、截止时间变色的临期提示、到点推送提醒）

## Related files
- ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/TaskList.java
- ruoyi-quartz/src/main/resources/mapper/quartz/TaskHandleMapper.xml
- ruoyi-system/src/main/resources/mapper/base/BaseRiskTaskRecordMapper.xml
- ruoyi-ui/src/views/base/riskTaskRecord/index.vue
- other1/app/src/main/java/com/sckj/other1/activity/ActivityRiskManagementDetails.java

## Notes
- 本任务为纯诊断与说明产出，未修改任何代码或配置文件
- "没派发"类反馈的正确结论顺序应为：先证成派发链路正常（任务侧+记录侧+job 日志三处取证），再归因于机制性认知缺口（零提醒、分班次到点可见、逾期换栏），最后才考虑改检查人员配置；上一轮把"人员配置不一致"当根因是过早收敛
- 后台列表无到点闸门、APP 有闸门，两侧记录数天然不一致，这是解释"后台有手机上没有"的关键，不可当作数据异常
- 面对"用户很迷糊"这类体验反馈，先用 AskUserQuestion 式分类（规则类 vs 操作类）收口范围比直接给改造方案更有效；用户取消选项框后改为文字分类框架更被接受

## Task overview
已完成：确认两天 52 条记录全部按时落库、派发与推送链路无故障，真实问题仅为 10-13 中班 6 条无人完成并被自动标逾期；确认管理员可通过"风险管理→排查记录/排查任务"自助查看并给出权限现状与两处限制（记录页不能按单元搜、sync 按钮权限未配菜单）；基于 APP 代码级核实交付了可转发的一线使用说明与四项界面改进建议。结论均为库内与代码实测支撑，无待办改造项。
