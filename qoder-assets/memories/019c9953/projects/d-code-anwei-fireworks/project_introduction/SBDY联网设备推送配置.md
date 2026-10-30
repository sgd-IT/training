---
title: "SBDY联网设备推送配置"
usage_scenario:
    - "实现或调试SBDY设备推送功能时确认数据源和筛选逻辑"
    - "排查SBDY推送缺失设备问题时核对access_status和deleteflag状态"
    - "对接ZBBJ报警数据时理解deviceName与设备表的级联匹配依据"
keywords:
    - "SBDY"
    - "em_device_info"
    - "设备推送"
    - "ZBBJ映射"
    - "GZCodeGenerator"
---

SBDY（联网设备信息）推送功能负责向省级平台同步企业联网设备基础信息，数据源为`em_device_info`表；筛选条件：`device_type != 1`（排除摄像头）、`access_status = 0`（待推送）、`deleteflag = 0`（未删除）；核心字段映射包括：`deviceId`→`id`、`uploadCode`→`company_code`、`deviceCode`经GZCodeGenerator生成规范18位编码→`code`、`deviceName`→`name`和`equip_location`、`deviceType`映射为`equip_type`（3→"01"温度/4→"02"湿度）、`storeNum`→`place_no`（15位仓库编码）。
