---
title: "消防告警通知文本由规则引擎模板链路写入eiot_alert_record"
usage_scenario:
    - "排查消防告警通知文本的来源与写入方"
    - "B方案统一告警落库时盘点存量链路"
    - "修改告警模板或规则脚本前定位改动点"
keywords:
    - "eiot_alert_record"
    - "消防告警通知"
    - "规则引擎模板链路"
    - "AlertConfigServiceImpl.addAlertRecord"
    - "sub_device_id"
source: "auto"
---

更正（此前判断有误）：eiot_alert_record 中"【消防告警通知】"格式的 94 条记录并非外部系统写入，而是本仓库链路产生：eiot_alert_config（报警配置，如 id 18"消防主机火警"，关联 message_template_id=15、rule_info_id=34）→ eiot_channel_template（id 15"火警模板"，模板正文存数据库、后台可编辑，sms_template_code 为 fire_alarm_notify）→ eiot_rule_info（规则脚本：设备台账信息 + msg.data 字段原样摊平）→ 规则动作 AlertService → MessageService.sendMessage → MessageEvent → AlertMessageEventListener → AlertConfigServiceImpl.addAlertRecord 落库（details 取 Message.getFormatContent()），sub_device_id 由该链路注入（全表唯一）。此前判为"外部写入方"的原因是模板正文存在数据库而非代码，全库搜索文案自然搜不到。B 方案统一时该链路（规则引擎-报警配置-消息模板）同样需要纳入盘点。
