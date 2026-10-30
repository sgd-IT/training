---
title: "设备上报落库与告警触发的三处结构约束：物模型过滤、规则引擎通路、dn通配"
usage_scenario:
    - "为新协议组件规划数据落库与告警配置时"
    - "排查设备报文已接收但前端无数据或告警不触发时"
    - "新建产品级规则绑定监听条件时"
keywords:
    - "DevicePropertyHandler物模型过滤"
    - "eiot_rule_info告警通路"
    - "eiot_device_alert_config空表"
    - "dn留空通配"
    - "标识符归一化"
source: "auto"
---

设备上报数据要落库并触发告警，受三处结构约束（本轮连生产/测试库实测确认）：

1. DevicePropertyHandler 只保留 identifier 命中所属产品 eiot_thing_model 的属性，未定义的键被静默丢弃。协议组件解析器吐出的原始键（如大华 UDP 的 data_analogValue/data_subType/data_alarmType）与平台标准标识符词汇不一致时，属性既不写缓存也不写历史——表现为"报文进来了但前端看不到数据"。正确做法是在组件侧把原始键归一化为平台标准词表（dev_fire_alarm/dev_fault/fire_status/last_temperature 等），TCP 组件的 ThingModelPropertyMapper 即此模板。

2. 告警触发的权威通路是规则引擎：eiot_rule_info(定义 listener+action) + eiot_alert_config(引用 rule_info_id/message_template_id) → AlertMessageEventListener → 落 eiot_alert_record → AlarmRealtimeNotifyService 推 firewatch-alert-new。而 eiot_device_alert_config（DeviceAlertChecker 依赖）在生产库是空表，走它是死路。规划新协议接入的告警时应复用规则引擎路径。

3. RuleManager 解析 device listener 时，config 里 dn 字段留空会被转成 pk/# 的产品级通配，匹配该产品下全部设备；填具体 dn 则只绑单台设备。新建产品级规则应让 dn 为空串。

适用边界：为任何协议组件（UDP/TCP/AEP）做数据落地与告警配置时。不适用：纯展示层改造。
