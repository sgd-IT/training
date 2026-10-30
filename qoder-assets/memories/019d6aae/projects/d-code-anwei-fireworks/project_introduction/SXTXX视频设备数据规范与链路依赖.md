---
title: "SXTXX视频设备数据规范与链路依赖"
usage_scenario: []
keywords:
    - "SXTXX"
    - "device_type=1"
    - "device_info表"
    - "视频设备规范"
    - "链路依赖"
source: "user"
---

SXTXX视频设备信息推送功能必须严格遵循以下规范：
- 数据源：`device_info` 表中 `device_type = 1` 的设备（非`camera`表）
- 文件格式：`企业编码_SXTXX_yyyyMMddHHmmss.json`，含`datas`数组，字段见表13（ID、AREA_CODE、COMPANY_CODE等16项）
- 上传频率：每5分钟全量上传
- 关键依赖：SBDY需先注册`device_type=1`设备，SXTXX才能正常推送，否则省平台校验失败
