---
title: "AEP回调链路设备匹配与自动注册的结构性约束"
usage_scenario:
    - "排查AEP推送数据未入库或属性丢失"
    - "设计AEP设备接入与迁移顺序"
    - "修改AepCallbackServiceImpl消息处理分支"
keywords:
    - "AepCallbackServiceImpl"
    - "getDeviceByDn"
    - "addDeleteDevice触发源"
    - "DevicePropertyHandler过滤"
    - "deleteDeviceInfo逻辑删除"
source: "auto"
---

AEP 回调链路存在三条结构性约束（经代码核对验证）：

1. **设备匹配只认 dn**：AepCallbackServiceImpl 通过 deviceInfoService.getDeviceByDn(aepDeviceId) 定位设备，eiot_device_info.dn 必须与 AEP 平台设备 ID 完全一致；设备别名、序列号均不参与匹配。dn 不一致时报文被丢弃并打日志 `[AEP推送] 设备不存在, aepDeviceId=xxx`。

2. **产品维度后置生效**：匹配到设备后，保活时间、物模型、告警策略全部按该设备的 productKey 取配置。若产品物模型不含 AEP payload 字段，属性数据在 DevicePropertyHandler 处被过滤丢弃；若 keep_alive_time 为 0，设备永远判不了离线。因此"设备挂哪个产品"与"dn 是否对齐"是两个独立的必核项。

3. **addDeleteDevice 的触发源在 AEP 侧**：该消息只在 AEP 平台发生设备新增/删除动作时推送，本地系统删除设备不会让 AEP 重推。自动注册产出的产品名为 `AEP产品-{productId}` 且 tenant_id=0、keep_alive_time=0、物模型为空、设备地址与经纬度为空，均需人工补齐。

4. **删除分支的类型误用隐患**：处理 delete 消息的代码写作 deviceInfoService.deleteDeviceInfo(Long.valueOf(aepDeviceId))，把 AEP 的 deviceId 直接当本地设备主键 id 使用。长数字 ID 会抛 NumberFormatException 并被外层 catch 吞掉（仅日志），短数字 ID 存在误删本地同 id 设备的风险。故触发自动注册应选"新增设备"而非"删除后重加"。

5. **deleteDeviceInfo 为逻辑删除**：实现是 deviceInfoMapper.deleteById 加清 Redis 设备缓存，deleted 标记位生效，软删记录仍占用唯一索引。历史告警 JOIN 仍可显示设备名称，但统计口径会先剔除该设备。
