---
title: "摄像头告警去重选型：查库+10秒窗口而非Redis且不加锁"
usage_scenario:
    - "为低频告警上报设计落库前幂等去重方案"
    - "在Redis SETNX与查库间权衡去重存储"
    - "评估check-then-insert竞态是否需要加锁"
keywords:
    - "幂等去重"
    - "查库vs Redis"
    - "reportCameraAlert"
    - "不加锁权衡"
    - "IdempotentRedisDAO"
source: "auto"
---

Conclusion: 摄像头告警（reportCameraAlert）短时重复上报去重采用"查库 + ±10秒时间窗"方案（Mapper 按 deviceId+alertImage+alert_time 窗口 selectOne LIMIT 1，命中则 return 不 insert 不推送），并明确不加并发锁。

Rationale (trade-off): 摄像头单设备日均仅数条告警、eiot_alert_record 约 3 万行级，带等值条件的简单 select 毫秒返回；查库有项目内既有先例（selectActiveAlert / addDeviceAlertRecord 的 LambdaQueryWrapperX 风格），零新增组件、重启不丢窗口。上游两帧实测间隔 0.37~7.8 秒，check-then-insert 撞进毫秒级真并发的概率极低，即使发生也只是退回现状（2条），不会比不修更差。

Rejected alternatives:
- Redis SETNX（框架 IdempotentRedisDAO.setIfAbsent 已存在）：被否决——该 DAO 的 key 前缀 idempotent:%s 是给 HTTP 请求防重放语义设计的，全后端仓库无任何业务使用先例；且需处理"先占坑后落库、落库失败白占窗口须 delete 回滚"的经典坑，多引入一个故障域。
- 内存 ConcurrentHashMap（AlarmRecordService.dedupCache 风格）：被否决——重启后窗口失效。
- synchronized/分布式锁：被否决——低频场景过度设计，偏离项目现状。

Applicable / expiry conditions: 结论在"单设备低频告警、表规模数万行级"下成立。若告警上报频率大幅上升或表涨至百万级，需重新评估：届时可考虑给 device_id 加普通索引、或改走 Redis 原子占坑方案。窗口取 10 秒的依据是上游同事件两帧最大间隔 7.8 秒留余量；若上游行为变化需重测该间隔再调窗口。

## Notes
- 【规模条件已实测确认】2026-09-28 经 MCP 查 anwei_fire_remote_monitor 库：eiot_alert_record 精确 31,563 行（租户 5 个、主设备 13 台、摄像头 24 路），本决策"数万行级"前提成立，暂不需改走 Redis 方案。
- 需注意的数据画像：其中 29,347 条（93%）为 level="1" + alarm_type=2 的热成像单一来源，即"高频刷屏"实际集中在单个算法来源而非全部设备均匀分布。做聚合/限流类改造时，压测样本应以该来源为主，否则低估真实压力；同时该集中度也意味着按来源维度做合并窗口比按设备维度更有效。
- 【边界警示（2026-10 补充）】本"不加锁也行"的结论仅对摄像头低频链路成立，禁止套用到 AEP 烟感链路：AEP 侧已被同事件 3ms 双推实锤过，两个 @Async 推送即两个线程，check-then-act 必然撞竞态，只能走原子占位（putIfAbsent + 比较替换）。两条链路结论不同是频率证据不同，不是写法偏好不同。
