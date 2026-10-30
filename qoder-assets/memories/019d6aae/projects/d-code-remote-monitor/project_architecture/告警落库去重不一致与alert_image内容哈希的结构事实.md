---
title: "告警落库去重不一致与alert_image内容哈希的结构事实"
usage_scenario:
    - "排查大屏告警重复入库或重复弹窗根因"
    - "判断两条告警是否同一事件重复上报"
    - "排查一级告警弹一次后不再提醒的原因"
keywords:
    - "alert_image"
    - "FileUtils.generatePath"
    - "reportCameraAlert"
    - "notifyAlarmSaved"
    - "alertShownIds"
    - "一级弹窗重复提醒"
source: "auto"
---

摄像头/设备告警落库与去重的结构性事实（经 2026-09-28 数据库实测与代码核查确证）：

1. eiot_alert_record.alert_image 存的是文件 URL，而 URL 路径由 framework/common FileUtils.generatePath 用 DigestUtil.sha256Hex(content) 即图片内容的 SHA-256 哈希生成——同一张图片内容必然得到完全相同的 URL。因此"两条告警记录 alert_image 相同"是判定"上游对同一事件重复上报（携带同一张抓拍图）"的可靠依据，而非后端双写。

2. 三条告警入库路径的幂等保护并不一致（易踩坑，排查重复弹窗须逐条核对）：
   - DeviceAlertConfigServiceImpl.reportCameraAlert（摄像头 /open/camera-alert/report 与 /receiveCameraAlarm 两个端点汇聚于此）：insert 前无任何查重，每次调用直接 insert + AlarmRealtimeNotifyService.notifyAlarmSaved 推 firewatch-alert-new → 上游发两次 = 两条记录 = 两次推送。
   - AlarmRecordService.saveAlarms（AEP 实时路径）：5 分钟内存去重窗口（ConcurrentHashMap，key=deviceId:alarmType:sourceType）。2026-10-10 实测确证存在 check-then-act 并发竞态：AEP eventReport/dataReport 并发双推、@Async 双线程同时通过检查导致双入库（实测两行仅差 3ms）。修复口径已确立：判定与占位必须原子合一（禁止先查后写）、落库失败需释放占位；具体实现以代码为准（由用户手写）。
   - addDeviceAlertRecord（规则引擎路径）：有"同设备同名未终态不重复入库"查库保护（selectActiveAlert）。

3. 前端 duty-screen/index.vue 的 alertShownIds(Set) 按 alertId 去重，无法拦截同一事件产生的两个不同 id；且处置动作 onHandleNow 会 alertShownIds.delete(id)，配合 fetchRecentAlerts 每 10 秒轮询未处置告警，构成二次放大路径。useWebSocket 来自 @vueuse/core，单条消息只触发一次 watch，不会自身重复。

4. 反向陷阱（2026-10-13 实测确证）：同一 alertShownIds 的"弹过即永久标记"语义使一级全屏弹窗**只弹一次**——15 秒自动关闭后即使告警仍未处置、轮询持续把它拉回列表，也因去重命中而不再入队，表现为"服务重启/页面不重挂就再无提醒"，只有跳转页面重挂载清空内存态才恢复。修复模式：一级告警改用 heavyAlertLastShownAt 记录上次弹出时间 + isHeavyRepeatDue 判到期（距上次 ≥ HEAVY_REMINDER_INTERVAL 60s 且不在弹窗/队列中即重新入队），alertShownIds 语义收窄为仅服务通知匣（二三级只推一次），duty-screen 与 monitoring-center 同构。教训：内存去重集合要区分"一次性提示"与"需持续催办"两类语义，前者用 shown-set，后者必须带时间戳周期重评。
