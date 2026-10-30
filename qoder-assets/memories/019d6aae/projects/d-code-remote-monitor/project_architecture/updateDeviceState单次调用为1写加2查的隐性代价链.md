---
title: "updateDeviceState单次调用为1写加2查的隐性代价链"
usage_scenario:
    - "评估设备状态写库节流的真实收益"
    - "排查设备离线显示延迟或状态不更新"
    - "改造 updateDeviceState 或其调用方前"
keywords:
    - "updateDeviceState写放大"
    - "CacheEvict设备缓存穿透"
    - "getDeviceInfo无Cacheable"
    - "eiot_device_info状态链路"
    - "离线消息MQ回灌写库点"
source: "auto"
---

后端 anwei-fire-remote-monitor 中 eIoT 设备状态写入链路（DeviceInfoServiceImpl.updateDeviceState）的单次代价不是一次 UPDATE，而是"1 写 + 最多 2 查"：

1. 方法体先 UPDATE eiot_device_info 的 state 与 online_time。
2. 紧接着调 getDeviceInfo(deviceId) 回查整台设备，目的是取 productKey + dn 去清另一套缓存（iot:device 的 pk:dn 键）。getDeviceInfo 直接走 deviceInfoMapper.selectById，**没有 @Cacheable**，因此每次写成功后必然多一条全字段 SELECT。
3. 方法上有 @CacheEvict(DEVICE_ID, key = "#deviceId")，清掉 Redis 里的设备缓存；于是下一条消息在 DeviceStateCheckHandler.handle() 里走 getDeviceInfoFromCache（该读方法带 @Cacheable）时必然穿透回库，形成第三笔 DB 访问。

结构后果：对这条链路做"按设备节流"能同时压掉写、回查、穿透三笔，收益不止是 UPDATE 数量；反之任何"每收到 state 消息就写一次"的改动都会以三倍系数放大 DB 访问量。

可优化点（未实施）：第 2 步的回查纯属冗余——调用方 handle() 手里已有完整 device 对象，productKey 与 dn 都在其中，把这两个值作为参数传入即可省掉每次写后的全字段回查。

概念区分：往进程内 Map 里 put 时间戳只是"记账"（重启即失、无业务副作用），方法返回值 true/false 只是给调用方的建议，只有 deviceApi.updateDeviceState 这一层才是真正改数据库与缓存的副作用点；判断节流逻辑影响面时须分清这三层。

补充（经源码核查确证）：该链路同时承载"设备失联通知"。DeviceStateCheckTask.sendDeviceOfflineMessage 判定某设备超过 keepAlive×1.1 无消息后，组装 type=state、identifier=offline 的 ThingModelMessage 投递 MQ，被 DeviceStateCheckHandler.handle() 接收并走同一个 updateDeviceState 写库点。因此作用在该写库点上的任何节流条件都直接决定界面离线显示的延迟，而非只影响心跳的冗余写；保活周期短于节流窗口的设备会被窗口拦下多次。

补充（反直觉结构事实，经源码核查）：调用点与 SQL 产生点不可混为一谈。handle() 里每条状态消息都会调用 updateSubDeviceState，但该方法首行即 `if (DeviceInfo.NODE_TYPE_GATEWAY == device.getNodeType())` 判定，非网关设备直接 return、一行 SQL 都不发；只有网关设备才会遍历其下透明传输子设备逐台调 updateDeviceState 产生真实写入。因此把节流包在 updateSubDeviceState 上是净收益为零的改动（非网关侧本来就空转，网关侧反而让子设备状态延迟一个窗口）。正确的包裹层级是"主设备写 + 子设备级联"同在节流块内，语义为主设备写了子设备才跟。遗留待验证项：线上 nodeType 为网关的设备数量未知，故子设备这条写入路径的实测量级无法评估。
