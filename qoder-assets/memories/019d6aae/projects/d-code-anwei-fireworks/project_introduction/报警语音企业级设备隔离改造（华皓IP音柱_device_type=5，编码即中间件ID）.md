---
title: "报警语音企业级设备隔离改造（华皓IP音柱 device_type=5，编码即中间件ID）"
usage_scenario:
    - "报警语音播报功能开发与调试"
    - "音柱设备添加与配置"
    - "语音播报链路问题排查"
keywords:
    - "华皓IP音柱"
    - "device_type=5"
    - "device_code"
    - "中间件设备ID"
    - "语音播报"
    - "企业级设备隔离"
source: "user"
---

## 报警语音企业级设备隔离改造

### 背景
报警语音播报按企业隔离，每家企业查询自己关联的语音设备进行播报，不再使用全局配置的设备码。

### 设备类型映射
- device_type=5 → 华皓IP音柱（SpeakerService.startTTSBroadcast）
- device_type=6 → 丹唛派克音响（DanMaiPaiKe.SendInfo）

### 关键设计
- **设备编码（device_code）= 中间件设备ID**：用户在前端添加音柱时，设备编码填的就是华皓或丹唛派克中间件的设备ID（如华皓音柱编码=100）
- **getVoiceDeviceIds() 使用 getDeviceCode()** 获取中间件设备ID（非 getUploadDeviceId()）
- **仓库/库房无需关联**：音柱设备只需关联企业（company_code），无需关联仓库或库房
- **播报链路**：alramInfoService.save() → sendAlarmVoiceForSingle() → sendAlarmVoice() → 按 company_code 查询 device_type=5/6 的音柱 → 用 device_code 调中间件播报
