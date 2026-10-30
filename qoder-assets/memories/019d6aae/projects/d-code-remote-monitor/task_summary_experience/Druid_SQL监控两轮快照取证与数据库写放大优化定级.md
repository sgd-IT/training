---
title: "Druid SQL监控两轮快照取证与数据库写放大优化定级"
usage_scenario:
    - "分析 Druid 或数据源 SQL 统计页找性能主因"
    - "判定高频轮询与无效写库的优化优先级"
    - "评估设备状态写库节流的真实收益范围"
keywords:
    - "Druid SQL监控"
    - "weburi.json字段"
    - "RequestTimeMillis非调用次数"
    - "进程内计数随重启清零"
    - "组件配置轮询惊群"
    - "设备状态写库节流"
    - "写后全字段回查"
source: "auto"
---

## Notes
- 写放大代价链实测拆解（DeviceInfoServiceImpl）：一次 updateDeviceState = ①535 行 UPDATE state+online_time（1 次写）→ ②537 行调 getDeviceInfo 回查整台设备，而 getDeviceInfo（281-283 行）直接走 deviceInfoMapper.selectById、**没有 @Cacheable**，故每次写必带 1 次全字段 SELECT → ③523 行 @CacheEvict(DEVICE_ID, key="#deviceId") 清 Redis 设备缓存，于是下一条消息在 DeviceStateCheckHandler.handle() 55 行 getDeviceInfoFromCache（读方法 309 行带 @Cacheable）必然穿透回库。即"一次状态刷新 = 1 写 + 最多 2 查"，按设备节流省下的是写+查两笔而非只掉 UPDATE。
- 归因自我更正：此前称 1,284 次设备查询"很大一部分"由写库造成属过头结论；有证据的只有第②步 192 次回查（约 15%），第③步穿透上限 192 次且取决于消息间隔，剩余约 1,092 次来源未取证，不得归因于本链路。
- 顺带发现（P2，不在 B1 范围）：第②步回查仅为拿 productKey + dn 去清 iot:device 的 pk:dn 键，而调用方 handle() 手里已有 device 对象含这两个字段，可传参省掉这次全字段回查；当前先不动，B1 收口后再评估。
- shouldRefreshOnlineTime（DeviceStateCheckHandler 120 行）全仓 grep 仅两处命中（TODO 注释 + 方法定义），无任何调用点，现为死代码，对线上零影响；但其 123 行写成 lastTime - now，恒为负导致第二次起永远返回 false，一旦接上 B1-2 会立即让设备状态全线冻结（编译通过、日志正常、Druid 数字反而变好，属最难发现的一类）。正确顺序是先改减法方向再接线。
- 概念口径：put 到内存 Map 是"记账"不是"修改"；三层须分清 —— 内存 Map（进程内一个数字，重启即失）、方法返回值（true/false 只是建议）、真正副作用（deviceApi.updateDeviceState → UPDATE + 清缓存），只有第三层动业务。

## Notes
- 【Druid 字段含义必须逐列核对，勿把耗时当次数（2026-10-29 二次踩坑）】同一项目再次出现同类误读：用户贴出的 `master 1 1,102 1,102 …` 与 `/admin-api/…alarm-trend 1 1,196 1,196 …` 两组数字，先后被当成"调用次数极高"来设计优化方案（一度推导出"加缓存/合并接口"这一无效动作）。实际前者是 Druid SQL 监控行（DataSource=master、ExecuteCount=1、总耗时/最慢 1,102ms、尾部数组是耗时直方图），后者是 URI 监控行（RequestCount=1、**RequestTimeMillis=1,196**）。取数方式：直接 GET 后端 `/druid/weburi.json` 与 `/druid/sql.json`（dev/test 配置 stat-view-servlet 无鉴权），字段名大写（URI / RequestCount / ErrorCount / RequestTimeMillis / JdbcExecuteCount；SQL 侧 ExecuteCount / TotalTime / MaxTimespan），按 URI 或 SQL 关键字过滤后即可与表内数据对账。
- 【Druid 计数与 infra_api_access_log 天然不同源】Druid 是进程内累计、随应用重启清零，且包含 401/预检等不入库请求；访问日志按 request_url 落库。两者数量级对不上时优先怀疑口径不同，而不是数据丢失——本轮生产库该接口全史仅 58 次、测试库 216 次，而 Druid 显示 1 次请求 1,196ms，正是两种维度。
- 【Druid 单次高耗时先判冷热】ExecuteCount=1 的秒级耗时基本是启动后首次执行的冷态成本（同句暖态实测仅几十毫秒），据此排 P0 前应先重跑计时确认，否则会把冷启动误诊为稳态瓶颈。
