---
title: "eiot_device_info.line_type字段业务语义与全栈实现规范"
usage_scenario: []
keywords:
    - "line_type"
    - "总线"
    - "多线"
    - "eiot_device_info"
    - "全栈实现"
source: "auto"
---

设备表 `eiot_device_info` 中 `line_type` 字段为整型，取值含义如下：
- `0` 表示「总线」
- `1` 表示「多线」
- 其他值或 null 视为「未知」

后端EiotDeviceInfoDO/DeviceInfoSaveReqVO/DeviceInfoRespVO均为Integer类型。前端DeviceCard.vue中LINE_TYPE_MAP为Record<number, string>：{0:'总线',1:'多线'}，lineTypeTagType中0→primary(蓝)，1→success(绿)。FireDeviceMapper.selectDeviceList已加d.line_type和d.create_time。device-screen loadData已映射lineType和createTime。SQL迁移文件：20260716_add_line_type_to_eiot_device_info.sql。
