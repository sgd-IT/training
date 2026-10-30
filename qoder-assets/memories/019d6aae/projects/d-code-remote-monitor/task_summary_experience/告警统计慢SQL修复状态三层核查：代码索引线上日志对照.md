---
title: "告警统计慢SQL修复状态三层核查：代码/索引/线上日志对照"
usage_scenario:
    - "核对性能修复是否真正落地生效"
    - "排查缓存或降噪改动无线上效果"
    - "跨环境比对索引差异与优化收益"
keywords:
    - "慢SQL修复核查"
    - "缓存命中率判定"
    - "覆盖索引收益边界"
    - "轮询降频验证"
    - "假500降噪"
source: "auto"
---

## Task description
- Core requirement: 用户贴出三条 eiot_alert_record 上的 SQL 采样统计后追问"你再看看有没有修复"，随后要求核对"测试库的修改"。需回答：这三条 SQL 是否已被改动、此前各项性能修复是否生效、测试库与生产库有何差异。
- Task background: 三条 SQL 分别对应 AlertRecordMapper 的 countByHourInRange（按小时分布）、countByTenantInRange（高发单位 TOP5）、selectRecentUnprocessedAlerts（大屏未处理告警轮询）；此前提出的问题是 (deleted = 0 OR deleted IS NULL) 的 OR 写法、FROM_UNIXTIME 包列、第三条无 LIMIT 且带 4 张 LEFT JOIN。

## Execution process
1. 代码侧核查：AlertRecordMapper.java 三个方法原封未动；后端仓库 git status 中无该文件；_sql 目录内无任何为该表建二级索引的脚本。
2. 前端侧核查：duty-screen / remote-console / alarm-order / alarm-screen / large-screen 均已改用 useVisiblePolling（60s/30s），但 dashboard/index.vue 仍是裸 setInterval(30s)、monitoring-center 仍是裸 setInterval(60s)，KeepAlive 下切路由不停表。
3. 框架侧核查：GlobalExceptionHandler 的 isClientDisconnect + clientDisconnectHandler 降噪分支已存在并已提交。
4. 服务侧核查：Redis 缓存 + Semaphore 并发闸门只挂在 getDashboardStatistics（loadStatisticsCached，新鲜期 70s、TTL 300s、许可 2），getDutyScreenStatistics 未套同一方法。
5. 生产库直查：eiot_alert_record 索引仍只有 PRIMARY(id) 与 idx_tenant_alert_time(tenant_id, alert_time, deleted)；近 3 天 recentUnprocessedAlerts 24,381 次、dashboard/statistics 11,802 次，错误数已由数十降至 2 / 28。
6. 缓存有效性判定：statistics 近 6h 共 1,101 次，耗时 <300ms 仅 6 次、908 次落在 1~10 秒；按小时看 606035a 提交前后均值均无断崖下降 → 缓存基本未命中。
7. 单实例频率反推：单一出口 IP 6h 内拉 recentUnprocessedAlerts 2,527 次 ≈ 每分钟 7 次，若为 60s 轮询则说明约 7 个页面实例并行，总请求量并未因降频下降。
8. 测试库比对：多一条生产没有的 idx_alert_cover(alert_time, deleted, alarm_kind, level, read_flg, tier, tenant_id)；三条 SQL 全部选中它，前两条 Extra 出现 Using index（不回表），30 天窗口扫 1,910 行实测 8.91ms；recentUnprocessedAlerts 驱动表 rows 降到 1、其余关联表 eq_ref 主键命中。
9. 收益上限判定：GROUP BY HOUR(FROM_UNIXTIME(...)) 是表达式分组，任何索引都消不掉 Using temporary; Using filesort；handle_status 不在该索引内仍需逐行判断，且无 LIMIT。生产仅 13,705 行 / 5.5MB，全表扫本就几毫秒，故不建议照搬到生产（且两库列类型不一致、DDL 不可照抄）。

## Related files
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/mysql/AlertRecordMapper.java
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireDashboardServiceImpl.java
- /anwei-fire-remote-monitor/framework/spring-boot-starter-web/src/main/java/com/enjoyiot/framework/web/core/handler/GlobalExceptionHandler.java
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/dashboard/index.vue

## Notes
- "修复了没有"必须拆成代码层/DB 层/线上表现层三层分别取证，只看其中一层会得出相反结论（本轮代码有缓存、线上却零命中）。
- 平均值会被极端值支配：recentUnprocessedAlerts 均值 1,475ms 由 2 次超长请求（最长 7,921,649ms）拉出，典型值在 300~500ms（2,516/2,555 次落在 100ms~1s），不能据均值判定普遍慢。
- 缓存是否生效可用命中率反证：按新鲜期推算应有约 2/3 请求命中（几十毫秒级），实际 <300ms 占比不足 1%，据此只能给出"版本未发布"或"缓存路径未走通（Redis 连不上/反序列化异常被吞）"两种候选，靠应用日志中的缓存命中 debug 行区分，不可直接断言。
- MCP 的受限 SQL 解析器不接受索引提示：IGNORE INDEX(idx_alert_cover) 与 IGNORE INDEX (idx_alert_cover) 两种写法均报 Parsing failed，无法用它做"去掉某索引"的 A/B 对照实验。

## Task overview
核查完成、未做任何改动：三条统计 SQL 一行未改，生产索引未变；Broken pipe 降噪已生效（错误数明显下降），大屏统计缓存与并发闸门代码已提交但线上命中率接近零（待确认生产包版本或 Redis 路径），值守大屏统计入口本就未接缓存，dashboard 首页与监控中心仍是裸定时器；测试库独有的 idx_alert_cover 对三条 SQL 确有去回表效果但受表达式分组与缺 handle_status 限制，且生产表规模下收益接近零，结论是不建议同步到生产。后续优先级建议：先确认生产版本与缓存命中日志，再只对第三条 SQL 加 LIMIT 并拆 OR，最后给 duty-screen 统计套同一缓存方法——均未获执行指令。
