---
title: "物业表同名记录跨租户并存与property_id关联面边界"
usage_scenario:
    - "页面出现同名单位重复条目需定位数据源"
    - "清理或迁移物业记录前盘点引用表"
    - "排查排班按租户过滤取不到数据"
keywords:
    - "fire_property同名跨租户"
    - "property_id关联表清单"
    - "eiot_space无property_id"
    - "测试生产归属不一致"
    - "排班tenant_id三态混杂"
source: "auto"
---

物业（联网单位）维度的两个结构事实（2026-10-23 经 MySQL MCP 实测核验）：

1. fire_property 允许同名记录跨租户并存，去重判据不能靠 name。测试库 anwei_fire_remote_monitor 中"广东安为安全科技有限公司"同时存在 id=1001（tenant_id=1，2026-04-20 建、有地址、挂 243 条排班）与 id=1018（tenant_id=166，2026-10-20 建、无地址、全业务表零引用）；生产库同 schema 只有 id=1001 且 tenant_id=166。即同一逻辑项目在测试库被建了两次，成因是早期挂在系统租户（tenant_id=1）下、生产后来改了归属而测试库未同步。system_tenant 本身无重名（仅 1 安为科技值守中心 / 165 安为科技 / 166 广东安为安全科技有限公司 / 167 安为园区 / 168 南城碧桂园湾区国际）。凡按名称匹配单位的链路（大屏今日值班卡、所属单位下拉、设备单位名 GROUP_CONCAT）都会因此出现重复条目。

2. 物业的关联面只在 fire_* 域，eiot_* 域不通：经 information_schema.COLUMNS 按 column_name='property_id' 全库扫描，持有该列的表为 fire_alarm_order、fire_daily_statistics、fire_device_maintenance、fire_duty_schedule、fire_guard_audit、fire_hazard、fire_maintenance_contract、fire_maintenance_plan、fire_patrol_record、fire_patrol_task、fire_repair_order、fire_transmission_device 及 eiot_remote_operation_log；eiot_space 与 eiot_device_info 均无 property_id（对 eiot_space 加该条件会直接报 Unknown column 'property_id'），设备与空间的归属只靠 tenant_id 隐式关联。结论：清理或迁移物业记录时，引用盘点只需覆盖上述 fire_* 表 + eiot_remote_operation_log，不必担心设备/空间侧产生孤儿数据。

3. 排班行的 tenant_id 与物业归属相互独立：fire_duty_schedule 中同一 property_id=1001 的记录 tenant_id 分布为 0/1/166 三态混杂（116/1/126），因为 FireDutyScheduleServiceImpl 走 TenantUtils.executeIgnore 并按 getTenantPropertyIds() 的物业归属过滤，不消费排班行自身的 tenant_id。改物业归属无需同步排班表的 tenant_id；反之任何绕过物业归属、直接用 TenantContextHolder 过滤排班的写法都会查不到这批数据。
