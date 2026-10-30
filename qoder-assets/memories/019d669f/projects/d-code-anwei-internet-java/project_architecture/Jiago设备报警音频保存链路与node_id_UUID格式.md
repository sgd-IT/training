---
title: "Jiago设备报警音频保存链路与node_id UUID格式"
usage_scenario:
    - "When diagnosing Jiago device audio saving logic"
    - "When resolving node_id format confusion in casting well lookup"
keywords:
    - "Jiago"
    - "audio"
    - "alarm"
    - "OSS"
    - "video_url"
    - "node_id"
    - "UUID"
source: "user"
---

声音漏铝报警按用户要求仅在 videoUrl 非空白时写入 mon_aluminum_leakage_alarm_record；空白地址直接返回、不抛异常。状态上报通过原铸造、复位及次数判断后，仅把完整报警对象暂存 Redis 的 lyxl_pending_alarm:<msgId>，TTL 10小时；Jiago 时间到 msgId 的 Hash 仍作为附件索引。setEventVideo 取得非空地址后重新读取记录以保留最新消警状态，使用现有 saveOrUpdate 新增暂存记录或更新历史记录，保存成功后清理暂存，保存失败仍报错。新旧上传接口、附件补传通过 getVideoAlarmRecord 兼容数据库与暂存查询。报警时间保留上报时刻，入库发生在音频到齐后；原音柱播报和消息推送时机不变。这不是完整事件去重，同 msgId 音频落库串行保护仅覆盖单实例。

铸造井 node_id 位于 tb_casting_well_info，格式随部署而异，不能假设一定是 UUID 或设备 SN。当前 resolveCastingWell 先兼容有效 node_id，再匹配 tb_casting_well_info.equip_code（2026-09-20 起替代原 em_aluminum_device_info 映射，支持多编码分隔录入）。设备适配通过在铸造井录入 equip_code 完成，不以修改 node_id 作为常规适配方式。
