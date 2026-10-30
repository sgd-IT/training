---
title: "GZ视频设备流水号范围不一致陷阱"
usage_scenario:
    - "GZ平台推送视频设备信息时报'视频设备流水号必须在1-999之间'异常"
    - "解析device_code生成videoSeq时因范围不匹配抛IllegalArgumentException"
    - "MAC地址作为device_code时无法直接提取合规videoSeq"
keywords:
    - "GZCodeGenerator"
    - "videoSeq"
    - "流水号范围"
    - "device_code"
source: "auto"
---

GZCodeGenerator.genVideoCode() 方法要求视频设备流水号（videoSeq）必须在 1-999 范围内，而 parseDeviceSeq() 从 MAC 地址等非标准 device_code 中提取的数字可能超过 999（如 '0A:0C:E8:63:14:97' 提取为 1497），导致 IllegalArgumentException。正确做法是：当 device_code 不含有效序号时，改用企业内设备顺序递增编号（1,2,3...）作为 videoSeq。（来源：GZCodeGenerator）
