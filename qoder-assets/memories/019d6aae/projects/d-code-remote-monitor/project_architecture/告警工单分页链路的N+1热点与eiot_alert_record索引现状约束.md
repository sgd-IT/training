---
title: "告警工单分页链路的N+1热点与eiot_alert_record索引现状约束"
usage_scenario:
    - "为告警中心或工单分页接口新增筛选搜索入参"
    - "优化告警工分页或大屏统计接口响应耗时"
    - "核对或解释线上表索引是否需重建"
    - "在doPageFromAlertRecord结果转换循环中新增字段填充"
keywords:
    - "二级索引补建"
    - "配置表索引"
    - "索引列序"
    - "eiot_product"
    - "product_key"
source: "auto"
---

告警工单分页链路 FireAlarmOrderServiceImpl.doPageFromAlertRecord() 存在两条只能靠实测发现的性能结构约束：

1. 结果转换循环是隐性 N+1 热点：方法先经 MyBatis-Plus selectPage 取一页记录（该步本身命中索引、耗时可接受），随后在 for 循环里对每条记录依次调用 fillDeviceType()（内部三次查询：eiot_device_info → product by productKey → category by categoryId）和 tenantApi.getTenantName(record.getTenantId())。每页 20 条即约 80 次额外远程调用，是接口响应慢的主因；任何在该循环内新增逐条查询的改动都会线性放大延迟，改造方向应为按 deviceId/tenantId 去重后批量查询再用 Map 回填。**推论：调大 AlarmOrderPageReqVO.pageSize 会线性放大该热点**——移动端一次拉 100 条即约 400 次额外查询，因此减小每页条数（如改成分页 10 条 + 上拉加载）本身就是降低单次请求耗时的有效手段。

2. eiot_alert_record 上的 idx_tenant_alert_time 实际列序为 (tenant_id, alert_time, deleted)，等值条件 deleted=0 位于排序列之后，无法参与索引前置过滤，EXPLAIN 表现为 type=ref 但 rows 仍达数千；对该表做"租户 + 未删除 + 时间倒序"分页时，索引应把两个等值列放在时间列之前。同时该表数据高度倾斜（单一养老院租户占九成以上记录、其中绝大多数长期停留在待处理状态），tenant_id 区分度极低，仅靠该列过滤收益有限。

3. 该链路的历史结论易过期：库表索引可能已被他人落地，改动前必须先 SHOW INDEX 核对线上实际列序，不能沿用记忆中的"尚未建索引"判断。

## Notes
- 第 2 条中"索引应把等值列放在时间列之前"的建议已被后续实测推翻：线上 idx_tenant_alert_time 列序即为 (tenant_id, alert_time, deleted)，对默认查询 WHERE tenant_id=? AND deleted=0 ORDER BY alert_time DESC 的 EXPLAIN 显示 type=ref 且 Extra 无 Using filesort（排序由索引天然有序承担），且该表 deleted=0 接近全量、deleted 前置过滤几乎不减少扫描行。结论是当前索引已够用，不应再以重建列序为动作项；rows 偏大属单租户热区的数据分布结果，不是列序缺陷。该链路性能优化只应聚焦第 1 条的应用层 N+1。
- tenantApi.getTenantName 不是 RPC 也不是缓存读：其实现 TenantApiImpl → tenantService.getTenant → tenantMapper.selectById，每次都是真实数据库单条查询，因此循环内调用它等价于循环内查库，批量化时应按租户 ID 去重后再取值。
- 【新增可选条件的成本口径（2026-10 实测）】为该接口增加 name/details 的 LIKE '%kw%' 模糊条件走不了索引、需全表扫描，但在当前表规模下实测 test/prod 均约 210ms，可接受；设备名在关联表 eiot_device_info，用 device_id IN (SELECT id FROM eiot_device_info WHERE name LIKE ?) 子查询即可，租户拦截器会同步给子查询注入 tenant_id 不会越权。告警编号（orderNo = "ALERT-"+id）应按数字解析后走 id 等值匹配，不要对它做模糊匹配。
- 【N+1 判据推广（2026-10-20 全仓体检）】判断标准只看"循环体内有没有数据库会话往返"，与有没有 distinct 无关：先把 tenantIds/deviceIds 去重再在循环里逐个 getXxx(id)，只是把查询次数从"按行"降为"按去重后条数"，系数变小但不是修复。同类模板已在设备/空间/摄像头/远程控制台等多处列表复制，且注释普遍写着"避免 N+1"而实际未避免——排查时不能信注释，要按上述判据扫代码。验收标准是数据库往返次数收敛为 N→1。
- 【"约 80 次"的精确构成与各端分页条数（git show 旧版本 + 前端实测核实）】80 = 每条 4 次单查 × 本页 20 条。4 次中 fillDeviceType 只占 3 次（设备 selectById → 产品 getByProductKey → 分类 selectById），第 4 次是**直接写在 for 循环体内**的 tenantApi.getTenantName，不在函数里，只数函数内的 3 个会误算成 60。分页条数按端不同：PC 端报警记录页 alarm-record/index.vue 的 queryParams.pageSize 为 20（80 次口径绑定此页），移动端告警中心 pageSize 为 10（同代码约 40 次），框架 PageParam 默认 PAGE_SIZE 亦为 10，实际条数以各端显式传参为准。80 还是上限值：fillDeviceType 有三段提前 return（deviceId 空 / 设备或产品查不到 / productKey 空），命中则该条只剩 1 次租户查询。
