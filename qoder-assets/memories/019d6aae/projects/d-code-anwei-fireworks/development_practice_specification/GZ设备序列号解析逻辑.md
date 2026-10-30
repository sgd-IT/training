---
title: "GZ设备序列号解析逻辑"
usage_scenario:
    - "开发GZ省平台设备数据推送生成标准编码时"
    - "排查GZCodeGenerator.parseDeviceSeq解析异常时"
    - "贵州推送equip_code生成逻辑改造时"
keywords:
    - "GZCodeGenerator"
    - "parseDeviceSeq"
    - "设备序列号"
    - "设备编码"
source: "auto"
---

GZ平台设备序列号解析逻辑由GZCodeGenerator.parseDeviceSeq()方法实现：
- 首先尝试将整个字符串解析为整数
- 如果失败，则提取字符串末尾的数字（如"XWAX_WD_101" → 101）
- 最终返回的序列号用于生成标准设备编码（如520123920003010101）
