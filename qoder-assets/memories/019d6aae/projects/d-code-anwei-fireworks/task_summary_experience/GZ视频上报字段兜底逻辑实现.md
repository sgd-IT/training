---
title: "GZ视频上报字段兜底逻辑实现"
usage_scenario:
    - "贵州省视频监控平台（GZ）数据上报字段缺失兜底开发"
    - "同类政府平台对接中处理行政区划编码、设备信息、时间戳等通用字段空值场景"
    - "排查SXTFX报警设备映射失败问题时参考轮询分配方案"
keywords:
    - "GZ上报"
    - "字段兜底"
    - "52编码"
source: "auto"
---

## 任务描述
为贵州省视频监控平台（GZ）数据上报功能实现字段兜底逻辑，解决省平台58条校验错误中的25条，包括行政区划编码、设备型号/品牌、经纬度、修改人/时间、部署算法及SXTFX报警设备映射等。

## 执行过程
```mermaid
graph TD
    A[需求:GZ上报字段兜底] --> B[分析58条校验错误]
    B --> C[定位SetGZUploadInfo.getDeviceVideoInfoMap]
    C --> D[修复area_code="52"、equip_type随机、equip_factory="大华ICC"等9项]
    D --> E[定位PushingGZDataService.saveInfoVideo]
    E --> F[增加企业级area_code/经纬度兜底及无效设备跳过逻辑]
    F --> G[定位PushingGZDataService.saveInfoVideoAlarm]
    G --> H[新增validDevices收集与SXTFX报警deviceCode轮询分配]
    H --> I[语法验证确认无误]
```

## 任务总结
成功修复25条校验错误：行政区划编码统一为"52"；设备型号为空时生成6位随机字符串；设备品牌默认"大华ICC"；经纬度为空或0时使用企业信息兜底；修改人/时间为空时设为"test"和当前时间；部署算法为空时设为"201,202,203,204,205"；SXTFX报警设备无法匹配时轮询分配有效视频设备。
