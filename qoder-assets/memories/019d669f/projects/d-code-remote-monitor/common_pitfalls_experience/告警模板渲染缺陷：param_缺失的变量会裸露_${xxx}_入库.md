---
title: "告警模板渲染缺陷：param 缺失的变量会裸露 ${xxx} 入库"
usage_scenario:
    - "排查告警文本出现占位符时"
    - "改造告警通知模板渲染时"
    - "B方案统一落库文本时"
keywords:
    - "占位符裸露"
    - "getFormatContent"
    - "模板渲染"
    - "静默坏数据"
source: "auto"
---

Bug 类：告警通知模板中的 ${xxx} 占位符以字面量形式落库（如"发生时间：${event_time}"）。
根因：Message.getFormatContent()（module-eiot-api 的 Message DTO）只遍历 param 中已存在的 key 做替换；模板引用了但 param 缺失的变量原样保留，且全程无日志无告警，属静默坏数据。param 来自规则脚本返回值（设备台账信息 + msg.data 字段原样摊平）；subDeviceName/subDeviceAddr 仅在 msg.data 含 sub_device_code 时由 AlertConfigServiceImpl.addAlertRecord 补齐。
证据：'【消防告警通知】'系列 94 条记录中 45 条裸露——42 条整段全裸（当时脚本尚无设备信息）、3 条仅缺 ${event_time}（设备上报未带该字段）。
修复模式：渲染器改为扫描模板全部 ${xxx}，缺失变量替换为空并 log.warn（或拒绝发送）；模板变量契约化，只消费统一 AlarmMessage 一等字段。注意同一 param 也喂给短信/企微/钉钉监听器，共用同一渲染缺陷。
