---
title: "prod排班表存在tenant_id=0脏数据与物业归属错位"
usage_scenario:
    - "排查大屏今日值班人员可见性异常"
    - "值守模块按租户过滤取数或数据治理"
    - "生产库向测试库同步业务表数据前盘点"
keywords:
    - "fire_duty_schedule"
    - "tenant_id为0"
    - "video_camera脏数据"
    - "租户口径错位"
    - "跨库数据同步"
source: "auto"
---

生产库（anwei-fire-remote-monitor-prod）2026-10 实测：fire_duty_schedule 中"安为科技大厦"（property_id=1009）的排班记录 tenant_id=0，而 fire_property 表里该物业实际归属 tenant_id=1（系统租户），存在两级错位脏数据；同期另有 tenant_id=178（广州越秀金桥养老院）的正常排班。后果：任何按 TenantUtils.execute(tenantId) 带租户条件过滤的排班查询（如 selectCurrentDuty、buildCurrentDutyList 的企业分支）都查不到这批 tenant_id=0 的记录，只有走 executeIgnoreWithResult 的系统租户全量分支才可能命中，但结果里的租户归属又对不上任何真实租户。排查"某物业有排班但大屏/移动端今日值班不显示"时应先核对 fire_duty_schedule.tenant_id 与 fire_property.tenant_id 是否一致。

补充（摄像头数据同步任务实测）：tenant_id=0 并非排班表独有，video_camera 表中同样存在 tenant_id=0 的历史脏数据（id=14 大华录像机_视频通道_6、id=45 广州越秀养老院值班室，均为 deleted=1 软删除）。因此 tenant_id=0 应作为整个 eiot/值守域的通用脏数据形态对待：任何跨环境数据同步或按租户过滤取数前，都要先单独核查 tenant_id=0 的记录，不能假定它只出现在排班表。适用边界：值守排班域取数、eiot 业务表（含 video_camera）跨环境数据同步与数据治理；不适用：system_tenant/system_users 等框架权限表自身的归属判定。
