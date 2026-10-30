---
title: "漏铝链路易误改约束：RedisUtils取值须带Class与alarm_time入库语义"
usage_scenario:
    - "新增或修改Redis字符串键读写代码时"
    - "调整漏铝msgId复用或音柱去重逻辑前"
    - "排查报警时间与消警保护期基准漂移"
keywords:
    - "RedisUtils Gson序列化"
    - "get带Class参数"
    - "sendIpVoice独立调用不受复用"
    - "alarm_time入库时间语义"
    - "PENDING唯一落库点"
source: "auto"
---

漏铝告警链路两处经代码核实、易被误改的结构性事实：

一、RedisUtils 读写不对称（monkey-service/src/main/java/com/monkey/general/common/utils/RedisUtils.java）：set(String key, Object value, long expire) 走 valueOperations.set 前对 value 做 JSON 序列化，String 也会被写成带双引号的形态；而 get(String key) 直接返回原始字符串，取回值是 "\"uuid\"" 这类带引号内容，与任何字面量比较都不相等。只有 get(String key, Class<T> clazz) 这一重载会执行 fromJson 还原干净值。因此凡以 set(key, String, ttl) 写入的键（如漏铝窗口索引 lyxl_well_msgid:{castingWellId}），读取必须成对使用带 Class 的重载；该约束在 hset/hget 按字段存取的路径上不存在。

二、msgId 复用范围不含音柱播报：JiagoVideoLeakageController.receiveDeviceStatus 内 sendIpVoice 是与 videoEventUp 并列、每次 push 都独立执行的调用，不依赖 msgId 是否复用，也不受铸造井窗口索引影响。因此把窗口复用从查库改为查 Redis 索引后，报警记录数与消息推送次数收敛为 1，但丹唛派克音柱在一次物理事件内仍会响 N 次；若要一起去重，需在新建 msgId 的分支上另加播报标记，不能假定复用逻辑已覆盖播报。

三、alarm_time 语义由"何时入库"决定而非"何时发生"：videoEventUp 只在 action_status 为空或 0 时把记录暂存进 Redis PENDING（前缀 lyxl_pending_alarm:，TTL 10 小时），全链路唯一落库点是音频上传后的 setEventVideo → saveOrUpdate。由于就近匹配挑的是离音频上传时间最近的 field，若窗口复用失效则入库记录的 alarm_time 等于事件末尾那次 push 的服务器时间；同时配对用的 field 来自设备时钟 dto.getTime()，两个时钟源不同步时会再错开一段。videoOffAlarm 的 30 秒消警保护期同样以 alarm_time 起算，基准漂移会连带影响消警时机。
