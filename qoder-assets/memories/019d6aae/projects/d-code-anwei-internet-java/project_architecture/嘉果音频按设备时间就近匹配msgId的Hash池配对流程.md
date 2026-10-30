---
title: "嘉果音频按设备时间就近匹配msgId的Hash池配对流程"
usage_scenario:
    - "修改lyxl push或音频上传接口的关联逻辑时"
    - "排查报警记录音频缺失或挂载错位问题时"
    - "变更mon_aluminum_leakage_alarm_record落库时机时"
keywords:
    - "lyxl_file_msgid_pool"
    - "就近匹配hdel消费"
    - "lyxl_well_msgid窗口复用"
    - "lyxl_pending_alarm唯一入库点"
    - "audio报警配对"
source: "auto"
---

嘉果漏铝监控音频与报警记录的配对机制（2026-10-24 已重构，commit 467f9d8）：

1. 窗口复用改用 Redis 索引：`lyxl_well_msgid:{铸造井ID}`，String 类型，TTL 120 秒且不续期。为空则新建 UUID 并写入，否则复用。取值必须 `redisUtils.get(key, String.class)`——RedisUtils 用 Gson 序列化，不带 class 取出来是带引号的 "uuid"，直接拼 key 会错。不续期是防止设备卡在 status=0 时同一条记录被无限复用。
2. 再 `hset("lyxl_file_msgid_pool:"+deviceSn, field=设备time, value=msgId, TTL 10h)`。
3. `/api/lyxl/file` 先按 time 精确 hget，未命中再 hgetAll 就近匹配（时间差最小），`hdel` 只消费命中的 field。
4. 入库延迟到音频到达：videoEventUp 只写 Redis `lyxl_pending_alarm:{msgId}`（TTL 10h），`setEventVideo` 里的 saveOrUpdate 是唯一入库点。
5. videoEventUp 创建前用 `getVideoAlarmRecord(msgId)`（查库+PENDING 兜底）做幂等判断，复用命中时直接 return，所以 PENDING 里首次写入的 alarmTime 不会被后续 push 覆盖。

修复前（757d3d8）窗口复用查的是数据库，而记录只在 PENDING 里，永远命中不了：每次 push 新建 msgId、alarmTime 漂到事件末尾（实测偏差最长约 59 秒）、Redis 堆 N 条 PENDING 并在 10h 后静默丢失。真正消除列表"暂无音频"行的始终是"无音频不入库"，不是窗口复用。副作用：sendIpVoice 在 Controller 里独立调用，仍按每次 push 重复播报。

涉及代码：JiagoVideoLeakageController.receiveDeviceStatus/receiveAudioFile、AluminumLeakageAlarmRecordServiceImpl.videoEventUp/setEventVideo/getVideoAlarmRecord。
