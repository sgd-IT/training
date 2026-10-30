---
title: "AEP烟感产品映射、保活时间与设备dn匹配配置"
usage_scenario:
    - "核对AEP推送deviceId与本地设备dn是否一致"
    - "配置或修改AEP产品keep_alive_time与物模型"
    - "排查AEP设备未入库或告警不可见问题"
keywords:
    - "AEP"
    - "烟雾探测器"
    - "product_key"
    - "keep_alive_time"
    - "dn匹配"
    - "addDeleteDevice"
    - "自动注册"
    - "物模型导入"
source: "auto"
---

AEP NB smoke detector (device ID 15) is registered under the product_key '17339615', named 'AEP产品-17339615', with keep_alive_time = 86400 seconds. This product is hosted in the 'anwei_fire_remote_monitor_test' MySQL MCP instance. The device's AEP-side identifier shown in AEP console push records is 99015381863555073280140, whose tail segment 863555073280140 is the SN — use it to map a console report back to internal device id 15 / product_key 17339615. Its thing model definition file is anwei-fire-remote-monitor/doc/aep/aep_smoke_thing_model.json, which is the authoritative source for payload field names and AEP property ids.

设备匹配与自动注册口径（同一知识点的延伸）：
- AEP 推送报文在 AepCallbackServiceImpl 中只用 getDeviceByDn(aepDeviceId) 匹配设备，即 eiot_device_info.dn 必须等于 AEP 平台的设备 ID；别名、序列号不参与匹配。dn 对不上时日志打 `[AEP推送] 设备不存在, aepDeviceId=xxx`。
- addDeleteDevice 消息只在 AEP 平台侧发生设备新增/删除时推送；本地删除设备不会触发 AEP 重推。
- 自动注册产出物需人工补三项：产品名 `AEP产品-{productId}`（tenant_id=0）、keep_alive_time 默认为 0（须改 86400）、物模型为空（须导入 aep_smoke_thing_model.json，否则属性数据被 DevicePropertyHandler 过滤丢弃）、设备 tenant_id=0（须分配租户，否则告警不可见）。
- deleteDeviceInfo 实现为 MyBatis-Plus deleteById 逻辑删除（deleted 标记 + 清 Redis 缓存），软删记录仍占唯一索引。
