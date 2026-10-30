---
title: "线上AEP烟感接入核对与自动注册迁移方案"
usage_scenario:
    - "核对AEP推送设备能否匹配到本地设备"
    - "评估删除本地设备改由AEP自动注册的方案"
    - "规划AEP链路迁移与验证顺序"
keywords:
    - "AEP烟感接入"
    - "dn匹配"
    - "设备自动注册"
    - "软删除"
    - "keep_alive_time"
    - "迁移方案"
source: "auto"
---

## Task description
- Core requirement: 判断线上已有设备"展厅烟感"（id=5112，产品为网管子设备 YYBAw4BGDSN7fYSr，dn=0101001）能否承接 AEP 推送；并评估"删除本地烟感设备、改由 AEP 自动录入产品和设备"方案的可行性
- Task background: 线上环境已存在设备与产品，AEP 链路的匹配与配置一致性是最易翻车点；需给出可执行的接入顺序与验证路径

## Execution process
1. 确认硬匹配规则：AepCallbackServiceImpl 用 deviceInfoService.getDeviceByDn(aepDeviceId) 匹配设备，即 eiot_device_info.dn 必须等于 AEP 控制台设备 ID，别名与序列号不参与匹配；不匹配时日志打 `[AEP推送] 设备不存在, aepDeviceId=xxx`
2. 确认第二核对点：保活、物模型、告警均按设备的 productKey 生效，产品需 keep_alive_time=86400 且物模型包含 AEP payload 字段，否则属性数据被 DevicePropertyHandler 过滤丢弃
3. Grep DeviceInfoServiceImpl.deleteDeviceInfo 实现，确认走 deviceInfoMapper.deleteById，即 MyBatis-Plus 逻辑删除 + 清 Redis 缓存（首次以 `public void deleteDeviceInfo` 精确正则搜索返回 0 匹配，改为宽松关键字才命中）
4. 纠正用户预期：addDeleteDevice 仅在 AEP 平台侧发生设备增删时推送，本地删除设备不会触发 AEP 重录，触发动作必须在 AEP 控制台做
5. 给出推荐顺序：AEP 侧触发新增 → 线上自动建产品与设备 → 手工补配置 → 推数验证 → 最后清理旧设备；不建议先删本地设备（会出现空窗期，且新 dn 与旧 dn 天然不冲突可共存）
6. 列出自动注册必补三项：产品 keep_alive_time 默认 0 须改 86400；物模型为空须导入 aep_smoke_thing_model.json；设备 tenant_id=0 须分配租户；地址/经纬度为空影响大屏地图
7. 指出隐患分支：删除消息处理里 `deviceInfoService.deleteDeviceInfo(Long.valueOf(aepDeviceId))` 把 AEP deviceId 当本地 id 使用，长 ID 抛 NumberFormatException 被外层 catch（仅打日志），短数字 ID 有误删风险，故触发注册应选"新增设备"而非"删除重加"

## Related files
- d:/code/remote-monitor/anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/aep/AepCallbackServiceImpl.java
- d:/code/remote-monitor/anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/device/DeviceInfoServiceImpl.java

## Notes
- 结论为方案设计与代码核对，未落盘任何改动；deleteDeviceInfo 的 Long.valueOf 隐患尚未修复
- 软删记录仍占唯一索引，将来复用同一 dn 录新设备可能被挡

## Task overview
部分完成（仅分析与方案交付，未执行改动）：明确了 dn 硬匹配这一唯一拦路项、产品侧 keep_alive_time 与物模型两项必配项，纠正了"删本地设备可让 AEP 自动重录"的错误预期，输出了先注册后清理的安全迁移顺序与三段日志验证路径，并标记了删除分支的类型误用隐患待修。
