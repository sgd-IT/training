---
title: "双控排查记录同步SQL结构约束与推送结果实测口径"
usage_scenario:
    - "改造排查记录上报范围或字段取值"
    - "诊断排查记录长期未同步或推送失败"
    - "评估历史排查记录重推修正方式"
keywords:
    - "BaseRiskTaskRecordMapper两条SQL"
    - "saveRecord单条限制"
    - "check_time非空硬条件"
    - "sync_result三种取值"
    - "mobileMe回查sys_user"
    - "彭亮账号缺失"
    - "同名库数据分布"
source: "auto"
---

hc-standard 双控排查记录（base_risk_task_record）经 PlatementRiskCheckUnitApi.saveRecord 逐条推送到园区接口 /openapi/check_unit_data/troubleshooting（该接口只接受单个 JSONObject、不支持数组），因此"上报哪些记录、每个字段取什么值"完全由 BaseRiskTaskRecordMapper.xml 的两条查询决定——getSyncRecordList（DataSyncTask 定时用）与 getRecordListByDateRange（PushDataServiceImpl.rePushRiskTaskRecord 手动补推用）。状态枚举映射与过滤条件的修复经过见权威条目《排查记录上报状态语义错位及修复》，此处只记结构约束与本轮实测补充：

1. 两条 SQL 必须同改：定时任务走前者、/push/riskTask 的第【7/8】步走后者（后者无 sync_status 过滤只有日期范围），只改一处会造成定时与补推行为不一致。

2. 推送资格硬条件是 check_time 非空：getSyncRecordList 为 sync_status!=1 AND deleted=0 AND check_time >= NOW()-2 月 LIMIT 200，每天 06:00 与 23:30 各跑一次。故 check_status=4（已逾期）中 check_time 为空的记录永远进不了自动同步池，本地删除靠 deleted=1 排除。

3. 落库结果口径（2026-09-23 于 220 正式库实测）：sync_result 成功落 'true'、失败落平台错误原文、未推送为空。BaseRiskTaskRecordServiceImpl.syncRiskTaskRecord 对每条 saveRecord 后无条件落 sync_status=1/2；若 saveRecord 抛异常会中断当轮循环，该条保持 sync_status=0 且 sync_result 为空——按此特征区分"从未被取到""被平台拒绝""推送中途中断"三种卡住形态。

4. mobileMe 回查陷阱（本轮定位到的失败根因）：平台返回"参数验证失败: {mobileMe=IMEI码不能为空}"时，判别点不是字面的 IMEI 缺失，而是 mapper 的回退逻辑——记录 mobile_me 为空时按 nick_name = create_by 回查 sys_user.mobile_me；若 create_by 在 sys_user 中根本没有对应账号（220 库不存在"彭亮"，仅有彭亨清/彭穗娟）则回查落空、mobileMe 传空被拒。同为 mobile_me 为空但能回查到 IMEI 的记录（全正华/冯中卫/李家洲）全部同步成功。此类记录每日重试仍会失败，须补 sys_user.mobile_me 才能解。

5. 数据分布须区分同名库（2026-09-23 复核）：160 测试库（MCP hc-standard-test 连 WINDOWS-3D9R3V5）31613 条、最新停在 2026-03-12；220 正式库（MCP tgr-gz-vue 连 anwei1-0001）43585 条且每日新增约 26 条。220 侧配置类五表 base_risk_obj/unit/measure/task/event 最新创建均停在 2026-07-18 17 时初始化批次，base_risk_task 老周期任务仍在被引用（update 至 2026-09-22）。is_defend 列标识包保责任人任务的排查记录（实体字段名 isDefend，直接按 isDefend 查列会报 Unknown column）。

6. MCP 时间读数：返回的是 ISO UTC 串，需 +8 小时还原库内北京时间，或用 DATE_FORMAT 取原始字符串核对，否则会误判"未来时间"与数据空档。
