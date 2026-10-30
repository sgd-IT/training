---
title: "ZBBJ推送级联数据关联模式"
usage_scenario:
    - "实现ZBBJ报警推送逻辑时确定数据来源和映射路径"
    - "排查ZBBJ字段缺失问题时追溯级联链路"
    - "新增类似级联推送功能时参考现有设计"
keywords:
    - "级联关系"
    - "设备关联"
    - "pushContent解析"
    - "targetcode生成"
    - "place_no生成"
---

项目采用级联数据关联模式：
- `em_early_warning_info_push_record` 表的 `pushContent` 字段（如"4号库房湿度"）用于匹配 `em_device_info` 表的 `device_name`；
- 通过匹配到的设备记录获取 `device_code`（生成ZBBJ指标编码targetcode）、`store_num`（生成仓库编码place_no）、`storeroom_num`（生成库房编码）；
- `warning_type` 字段（0=湿度/1=温度）决定指标类型targettype及单位；
- `pushContent` 中的数值描述（如"实时值：【85.0】"）用于提取alarmvalue等动态字段。
