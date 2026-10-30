---
title: "修复DB列存反+毫度存储导致的经纬度规范不符问题"
usage_scenario:
    - "处理政府/行业平台对接时因DB列命名与实际地理含义不符引发的坐标错误"
    - "适配V1.3等要求经纬度为标准度数（非毫度）且需交换字段的规范"
    - "同类坐标标准化需求（需同时处理Double/String入参、防错边界）"
keywords:
    - "DB列存反"
    - "毫度÷1000"
    - "normalizeCoord"
    - "V1.3规范"
---

## 任务描述
修复SXTXX和JCXX数据推送中经纬度字段不符合V1.3规范（9,6格式）的问题，根源是数据库longitude/latitude列值互换且以毫度（×1000）存储。

## 执行过程
```mermaid
graph TD
    A[发现JSON经纬度异常：26427.0/105227.0] --> B[分析DB存储逻辑：列名与实际含义相反]
    B --> C[确认规范要求：标准度数，9位总长6位小数]
    C --> D[设计修复策略：÷1000 + 交换读取字段]
    D --> E[新增normalizeCoord/normalizeCoordStr双签名方法]
    E --> F[JCXX处：longitude←normalizeCoord(getLatitude()), latitude←normalizeCoord(getLongitude())]
    F --> G[SXTXX两处：longitude←normalizeCoordStr(getLatitude()), latitude←normalizeCoordStr(getLongitude())]
```

## 任务总结
成功修复坐标输出：在SetGZUploadInfo.java中新增标准化方法，统一将DB毫度值÷1000并交换经纬度字段映射，使SXTXX和JCXX输出符合V1.3规范（如longitude:105.227, latitude:26.427）。
