---
title: "漏铝配置前缀与必需@Value及连接池属性名清单"
usage_scenario:
    - "核对铝加工本地化部署yml漏铝配置完整性"
    - "排查漏铝服务启动占位符解析失败"
    - "判断hikari或redis连接池配置为何不生效"
keywords:
    - "monkey.leak前缀"
    - "dmpk配置"
    - "无默认值@Value清单"
    - "铝加工本地化配置"
    - "dynamic.hikari属性名"
    - "无效配置项"
source: "auto"
---

anwei-internet-java 的漏铝（铝液泄露）功能配置集中在两个 @ConfigurationProperties 前缀：`monkey.leak.*`（MonkeyLeakProperties，字段含 videos[castWellId/host/port/gatewayNum]、consumeURL、personCacheURL、setValueURL、resetButtonStatusURL、leakAlarmNormal、leakAlarmDemo、resetEncoding、preSignalDelayEnable、preSignalDelay、isStorePhoto）和 `dmpk.*`（DMPKEntity，字段 isopen/token/apiUrl/deviceCode/dmpks[castWellId/repeatCount/deviceCode]），丹唛派克音柱播报由 dmpk 段控制。

代码中以下 @Value 占位符均**无默认值**，application.yml 缺失任一项即 Spring 创建 Bean 失败、服务无法启动：monkey.leak.resetEncoding、monkey.customer.id、monkey.customer.name、monkey.customer.castLength、monkey.customer.ignoreCode、monkey.customer.startNum、monkey.customer.networkAlarmTime、monkey.customer.openAfterCastAlert、monkey.voice_url、monkey.data.file.path、monkey.data.download.address、monkey.data.network.address、monkey.data.upload.realtime、monkey.data.upload.noNetwork、monkey.rtsp.url、monkey.debugging.peopleImg、monkey.debugging.time、monkey.sms.sign、monkey.swagger.enable、spring.profiles.active、server.port。

MonkeyLeakProperties 中 preSignalDelayEnable 与 isStorePhoto 为包装类型 Boolean，leakAlarmNormal / leakAlarmDemo 为 Integer，yml 未配时为 null，而 AlarmUtil、MonitorCenterServiceImpl、AluminumLeakageAlarmRecordServiceImpl、JiagoVideoLeakageController 中以 `if (getXxx())` / `getXxx() > 1` 形式拆箱使用，缺失不崩启动但在漏铝报警记录创建路径抛 NPE（且被异步分支 catch 吞掉，接口仍返回成功）。DMPKEntity.isopen 为原始 boolean，缺 dmpk 段时静默不播报；VoiceProperties.enable 虽也是 Boolean 但读取处有 null 判断，不配不报错。

无效配置（本仓库无任何读取点，写了也不生效）：`monkey.local.*`（customerId / dataComing）、`spring.kafka.*`（pom 无 kafka 依赖）、`spring.redis.open`、`spring.redis.jedis.pool.*`（Boot 2.1.5 默认 Lettuce 且无 jedis 依赖，需写 spring.redis.lettuce.pool.*）、`monkey.province.interface.*`。企业 ID 生效键是 `monkey.customer.id`，但漏铝链路实际用的是 casting_well_info 行上的 customer_id。

连接池正确写法：dynamic-datasource-spring-boot-starter 3.3.1 只认 `spring.datasource.dynamic.hikari.min-idle` / `.max-pool-size`（解包其 spring-configuration-metadata.json 核实），写在 `spring.datasource.hikari.minimum-idle` / `maximum-pool-size` 或 `spring.datasource.dynamic.minimum-idle` 均静默不生效，实际跑默认 10 连接。
