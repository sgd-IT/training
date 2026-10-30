---
title: "定时上报循环内return中断整轮与MQTT不受HTTP推送开关约束"
usage_scenario:
    - "排查部分人员始终未被定时任务上报"
    - "本地联调前确认对外推送是否真被关闭"
    - "改造逐条遍历的外部数据上报逻辑"
keywords:
    - "循环内return中断整轮"
    - "定时上报静默漏数"
    - "MQTT无推送开关"
    - "PLATEMENT_PUSH_ENABLED局限"
source: "auto"
---

Bug class：定时批量上报任务被单条脏数据静默中断，以及"以为已关闭对外推送"实则另一通道仍在真推。

Root cause（两处，均在 hc-standard 项目实测确认）：
1. ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/LocationTask.java 在遍历定位系统返回数组时写了 `if (obj.getJSONObject("position") == null) { return; }` —— 用 return 而非 continue，只要数组里有一条记录不带 position（卡未定位/数据异常），整个方法立即退出，排在后面的所有人员本轮全部漏报；该任务每 10 秒执行一次，表现为"部分人员永远进不了园区表"却无任何错误日志。
2. PlatementBaseApi.PLATEMENT_PUSH_ENABLED=false 只拦截 HTTP 侧 platement API 调用，拦不住 MqttSendMessage 直连园区 broker（tcp://111.4.80.39:8096，topic person/location）的发布，本地联调时 MQTT 报文仍会真实写入园区数据。

Fix pattern：循环体内的单条数据校验一律用 continue 跳过并计数打日志，只有全局前置条件（接口整体返回空、鉴权失败）才允许 return；同时按通道分别核对开关覆盖面，对无开关的通道（MQTT/WebSocket）在联调期直接停掉对应 sys_job 定时任务。

Reusable lesson：不要因"某类人员一直同步不上去"就先去改匹配字段口径，因为一条 null 数据即可让整轮静默中断，先检查循环里的 return/break；适用于改造任何逐条处理的定时上报任务与判断对外推送是否已被关闭；不适用于业务语义上确实需要"一处失败即整体放弃"的事务型批处理。另注意：首次给离线人员补报需持久化"已建档"标记，否则每次重启都会全量重推一遍。
