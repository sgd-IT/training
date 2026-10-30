---
title: "AEP设备自动注册时从addDeleteDevice消息提取设备名称"
usage_scenario:
    - "当AEP平台推送addDeleteDevice消息时，需要正确提取并设置设备显示名称"
    - "修复设备自动注册后显示名称不可读的问题"
keywords:
    - "AEP"
    - "addDeleteDevice"
    - "deviceName"
    - "设备名称"
    - "自动注册"
source: "auto"
---

## Task description
- Core requirement: 从AEP addDeleteDevice消息的deviceList中提取deviceName作为设备的显示名称，而不是使用deviceId
- Task background: 当前AEP设备自动注册流程中，设备的显示名称（name字段）和设备标识（dn字段）都使用AEP的deviceId，导致设备名称不可读；需要改为使用AEP消息中携带的deviceName作为显示名称

## Execution process
1. AepCallbackServiceImpl.java: 在handleAddDeleteDevice方法中，设备注册成功后，遍历deviceList获取每个DeviceItem的deviceName
2. 调用updateDeviceInfo接口，将获取到的deviceName设置为设备的显示名称（name字段）
3. 确保updateDeviceInfo请求中设置了所有必需字段（dn、serialNo、productKey）以通过验证

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\aep\AepCallbackServiceImpl.java

## Notes
- None

## Task overview
成功修改了AEP设备自动注册逻辑，使设备的显示名称（name字段）正确地使用AEP addDeleteDevice消息中deviceList里的deviceName，而不是与设备标识（dn）相同的deviceId。
