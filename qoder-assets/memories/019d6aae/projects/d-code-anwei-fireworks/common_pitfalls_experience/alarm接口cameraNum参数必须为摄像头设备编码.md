---
title: "alarm接口cameraNum参数必须为摄像头设备编码"
usage_scenario: []
keywords:
    - "cameraNum"
    - "摄像头设备"
    - "报警接口"
    - "device_code"
source: "user"
---

报警接口 `/algorithmAlarm/getAlgorithmAlarm` 的 `cameraNum` 参数必须传**摄像头设备的 device_code**，而非音柱设备的 device_code。该参数用于查询摄像头设备以获取企业/仓库信息，若传入音柱编码（如100），将因 device_type 不匹配导致查不到记录（SQL WHERE device_code=? AND access_status=? 返回0行）。（来源：日志SQL执行结果）
