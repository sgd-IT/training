---
title: "告警配置条件编辑器属性下拉依赖物模型导致事件参数规则只能SQL插入"
usage_scenario:
    - "设计告警配置界面改造方案时"
    - "排查事件类规则无法在界面创建时"
    - "以SQL插入事件类告警规则时"
keywords:
    - "AlertConfig组件"
    - "条件编辑器"
    - "物模型properties"
    - "事件参数不可选"
    - "eiot_device_alert_config"
source: "auto"
---

告警配置入口在设备详情页/产品编辑页的"告警配置"tab（两处共用 components/AlertConfig/index.vue 组件）；条件编辑器的属性下拉数据源 = ThingModelApi.getThingModelByProductKey 返回的物模型 properties。事件（Event）参数（如 sub_type/data_subType/alarm_type/event_value）不在属性列表中，界面上无法选择。UDP/TCP 用传产品（FfGbt8CE75Aw0001 GBT26875消防设备-TCP、DhUdpF7xQm2Kp931 大华UDP消防终端）没有物模型记录，属性下拉为空。结论：事件类告警规则当前只能由开发直接 SQL 插入 eiot_device_alert_config —— 实测液压液位报警即按此方式插入两条同名规则（TCP 侧条件键 sub_type、UDP 侧条件键 data_subType，status=0 生效）。要让运营在界面自助配置，需先给产品补物模型或让条件编辑器支持"事件参数"来源。
