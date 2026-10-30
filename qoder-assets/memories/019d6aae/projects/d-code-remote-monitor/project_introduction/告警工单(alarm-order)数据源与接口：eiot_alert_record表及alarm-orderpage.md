---
title: "告警工单(alarm-order)数据源与接口：eiot_alert_record表及/alarm-order/page"
usage_scenario:
    - "为告警中心列表扩展分页或搜索入参"
    - "排查告警工单数据显示异常确认数据来源"
    - "改造level/alarm_kind/tier字段模型时盘点消费点"
    - "核对告警编号与位置字段的真实来源"
keywords:
    - "告警工单"
    - "alarm-order/page"
    - "eiot_alert_record"
    - "FireAlarmOrderServiceImpl"
    - "AlarmOrderPageReqVO"
    - "keyword模糊搜索"
    - "pageNo pageSize分页"
    - "orderNo告警编号"
    - "mapAlarmTypeToLevel"
source: "auto"
---

小程序告警中心的真实数据源为数据库表`eiot_alert_record`，通过后端接口`GET /eiot/firewatch/alarm-order/page`提供分页数据。

## 关键实现细节
- 字段映射规则（LEVEL_MAP/STATUS_TAB/STATUS_DOT）统一前后端语义。
- 动态Tab计数确保与后端数据实时一致。
- 交互逻辑修正分类标签、按钮状态并实现下拉刷新。
- 所有逻辑严格对齐`eiot_alert_record`表结构和`AlarmOrderRespVO`定义。

## 相关优化
- 针对该接口的性能瓶颈（'全部状态'查询无法命中索引），已提出复合索引优化方案（如`idx_tenant_alert_time`）。详情请参阅新记忆：'移动端告警中心数据查询接口定位与索引优化'。

## Notes
- 【字段语义已演进（2026-10-08 实测）】本条"字段映射统一前后端语义"的描述停留在改造前状态：该链路历史上用同一列 `level` 同时算两个出口（mapLevelToAlarmType→alarmType、mapLevel→alarmLevel），导致热成像等摄像头报警一律显示为"火警"；`AlarmOrderRespVO.alarmCategory` 在该链路曾恒为 null。现已按 alarm_kind/tier 新模型做读侧双读改造（详见《消防告警字段语义定稿》与 AlarmSemantics 相关记忆），排查时勿再假设 level 承载类型语义。
- 【分页接口已具备类型筛选能力（2026-10 核实）】AlarmOrderPageReqVO 已有 alarmType 入参，FireAlarmOrderServiceImpl 在 StrUtil.isNotBlank(alarmType) 时经 mapAlarmTypeToLevel 换算后以 wrapper.apply 过滤 eiot_alert_record.level，因此 PC 处置中心页新增"报警类型筛选"只需前端传参，无需后端改动；前端下拉选项值域须与 mapAlarmTypeToLevel 支持的取值对齐（fire/fault/risk/offline/false_alarm/info），与 alarm-record 页既有筛选保持同源。
- 【页面命名口径（2026-10 变更）】该页面（views/eiot/firewatch/alarm-order/index.vue）对外名称已由"火警处置中心/火警处置"统一改为"处置中心"，菜单名来源是 router/modules/remaining.ts 中该路由的 meta.title（仓内无 system_menu 相关 SQL 记录）；目录名与路由 path 仍保留 alarm-order 未改。
- 【分页入参与关键字口径（2026-10 补充）】AlarmOrderPageReqVO 继承 PageParam，天然支持 pageNo/pageSize；历史上只有 alarmType/alarmLevel/orderStatus/propertyId/deviceType/alarmTime 区间等条件，**没有关键字参数**，因此小程序告警中心只能一次拉大 pageSize（曾用 100）再做本地过滤。现已新增可选 keyword 参数（name/details 模糊 + 设备名子查询 + 编号归一 id 等值），keyword 为空时不进该条件，PC 后台共用接口不受影响。
- 【展示字段来源】AlarmOrderRespVO 无 alarmNo 字段，前端展示的"告警编号"实际取 orderNo（= "ALERT-"+记录id）或 id 兜底；alarmLocation 与标题同源取 record.name，alarmDescription 取 record.details，deviceName 来自关联设备表——写搜索条件时不要误以为有独立的编号列。
