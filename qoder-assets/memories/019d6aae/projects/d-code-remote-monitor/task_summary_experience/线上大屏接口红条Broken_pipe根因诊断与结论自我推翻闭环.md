---
title: "线上大屏接口红条Broken pipe根因诊断与结论自我推翻闭环"
usage_scenario:
    - "排查线上接口偶发500是否为真故障"
    - "评估大屏轮询与超时导致的连接断开"
    - "线上MySQL慢查询阈值调整受阻时"
keywords:
    - "Broken pipe假500"
    - "访问日志duration口径"
    - "单一IP轮询风暴"
    - "long_query_time SUPER权限"
    - "大屏statistics接口耗时"
    - "全局异常处理器降噪"
source: "auto"
---

## Task description
- Core requirement: 用户就线上值守大屏顶部红色错误条"接口加载失败: /eiot/firewatch/dashboard/statistics，请检查后端服务是否正常及权限配置"提问"这是什么问题"，后续追问完整堆栈含义、质疑"改SQL+加索引"结论的日志依据，并要求开启慢 SQL。
- Task background: 前端 alarm-screen/index.vue 的 silentGet 捕获异常后仅按接口名推入 loadErrors 显示红条，不区分超时/500/403；后端 FireDashboardServiceImpl.buildStatistics() 串行执行十几次聚合查询，日期条件写作 DATE(FROM_UNIXTIME(alert_time/1000)) = CURDATE()；生产库为腾讯云 CDB gz-cdb-dnqlqqwx:60477。

## Execution process
1. 查 infra_api_access_log 按 result_code 聚合：dashboard/statistics 近 7 天成功 27393 次（平均 3 秒、最长 50 秒），失败 51 次（平均 261 秒、最长 8034 秒）；确认非宕机类故障而是偶发长耗时。
2. 查 infra_api_error_log 近 10 条异常全部为 AsyncRequestNotUsableException: ServletOutputStream failed to write: Broken pipe，无 SQLSyntaxErrorException，据此排除缺表/缺列/权限成因。
3. 用户提供完整堆栈后逐层解读：栈顶为 AbstractMessageConverterMethodProcessor.writeWithMessageConverters → Jackson flush → LifecycleServletOutputStream.write，全链无 Controller/Service/Mapper 帧，证明业务已算完、是写响应时发现连接已被客户端断开；StandardServletAsyncWebRequest 只是 Spring 6 对 DispatcherServlet 的默认 async 生命周期包装，不代表业务用了异步接口，WsFilter 仅是 Tomcat 过滤器挂在链上。
4. 读 framework/spring-boot-starter-web 的 GlobalExceptionHandler.defaultExceptionHandler：对 Exception.class 兜底会 log.error + createExceptionLog 写 infra_api_error_log + 再返回一次响应（必然再次失败），这是 Broken pipe 被记成假 500 并污染错误日志的直接原因。
5. 本机 mysql.exe 实测 getSafeDays() 1399ms、今日计数 1260ms，并 SHOW INDEX 确认 eiot_alert_record 仅有 PRIMARY 与 idx_tenant_alert_time(tenant_id, alert_time, deleted)，共 17334 行。
6. 按 user_ip 复核推翻"全局 SQL 慢"结论：500 全部集中在单一出口 IP 219.131.130.21（成功 27022 次均值 3 秒 / 失败 53 次均值 268 秒），其余 20+ 个 IP 同一接口全部成功且均值 1 秒、最长 14 秒；该 IP 含 5 种 UA（Chrome 155/152/154Mac/137、Edge 155），其中 Chrome 155 承载 23539 次即常驻大屏。
7. 按接口维度对比同 IP 与其他 IP 调用量，发现该大屏存在轮询风暴：remote-console/valve/command 74962 次（约 8.6 秒一次）、recentUnprocessedAlerts 58342 次（10 秒）、dashboard/statistics 26999 次（26 秒）、getDeviceWithProperty 19248 次（31 秒）、property/list 8232 次；结合浏览器同域 HTTP/1.1 六连接上限解释 duration 被拉长机制。
8. 尝试 SET GLOBAL long_query_time = 1 失败：ERROR 1227 Access denied; you need SUPER 权限，腾讯云 root 亦不具备，只能走控制台参数设置；同时确认 slow_query_log 本已 ON、log_output=FILE、slow_query_log_file=/data1/mysql_root/data/20537/slow_query.log、long_query_time=10、Slow_queries 累计 114020、Uptime 约 49 天；performance_schema.events_statements_summary_by_digest 返回空、mysql.slow_log 为 0 行，故服务端无法量化单条 SQL 耗时。

## Related files
- 本轮未修改任何文件，全部为只读排查（读取对象：anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/alarm-screen/index.vue、module-eiot/module-eiot-biz/.../FireDashboardServiceImpl.java、dal/mysql/AlertRecordMapper.java、framework/spring-boot-starter-web/.../GlobalExceptionHandler.java、src/config/axios/service.ts 与 config.ts）

## Notes
- 首答把"静态代码推断（索引失效、串行聚合）"与"日志证据"混为一谈并给出改 SQL+加索引的动作项，被用户"从日志哪里看出来的"一问证伪：infra_api_access_log 的 duration 覆盖整个请求生命周期、包含响应体写出网络的时间，客户端网络差时 Tomcat 阻塞在写响应上全部计入 duration，因此该字段不能反推 SQL 耗时。
- 本机直连云端做 SQL 计时的数字含公网 RTT，不可作为线上单条 SQL 耗时的依据。
- 判定"接口是否真的有问题"必须补一层按来源 IP 的横向对比；单看某接口的 500 次数与最大耗时会把特定客户端链路问题误判成服务端全局缺陷。
- 项目既有记忆已记录 idx_tenant_alert_time 列序够用、不应以重建索引为动作项，动手前未召回该记忆导致提出相反建议。

## Task overview
未完成修复动作，结论经两轮自我推翻后收敛为分层判定：① 客户端层为主因——该出口共享带宽且常驻大屏同时跑 5 组独立轮询，挤占浏览器 6 个同域连接槽使 dashboard/statistics 排队超 30 秒，触发前端 axios timeout 断开；② 链路层——失败 100% 集中于单一出口 IP；③ 服务端层——buildStatistics 串行十几条聚合且日期条件包裹索引列属真实隐患但未被线上数据证实为主要瓶颈，量化需先在腾讯云控制台调低 long_query_time（SET GLOBAL 被 SUPER 权限拒绝）。唯一有明确日志证据支撑的改动是给 GlobalExceptionHandler 增加 AsyncRequestNotUsableException/ClientAbortException 前置分支做降噪，已向用户征询但未获执行指令；大屏轮询降频与 HTTP/2 复用为待验证方向。
