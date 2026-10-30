---
title: "告警工单id语义与AlarmOrderRespVO空置字段、AI闲置列的结构事实"
usage_scenario:
    - "以orderId入参新增告警相关接口"
    - "排查VO字段有定义却恒为空"
    - "评估落库AI结论是否需要新建列"
keywords:
    - "工单id等于告警记录id"
    - "alertRecordId未赋值"
    - "ai_analysis闲置列"
    - "convertToRespVO空字段"
    - "UserTenantResolver租户过滤"
source: "auto"
---

告警工单链路的 id 语义与 VO 空字段结构事实（读 FireAlarmOrderServiceImpl + MCP 查列结构核实）：

- 工单 id 就是告警记录 id：FireAlarmOrderServiceImpl.getAlarmOrder(id) 内部走 alertRecordMapper.selectById(id)，orderNo 形如 "ALERT-"+记录id；确认火警/标记误报等动作也按该 id 直接操作 eiot_alert_record。因此任何以 orderId 入参的新接口都可以安全地把 id 当 alertRecordId 用。
- AlarmOrderRespVO.alertRecordId 历史上全仓无任何赋值点（convertToRespVO 从不 set），接口出口恒为 null，而前端 mapAlarmToDisplay 一直在取 item.alertRecordId —— 消费方拿到的是空值而非真实关联 id。现已在 convertToRespVO 补 vo.setAlertRecordId(record.getId())。同类陷阱：判断"VO 有字段就一定有值"不可靠，必须回查是否有 set 落点。
- fire_alarm_order 表存在闲置列 ai_analysis(TEXT) 与 ai_suggestion(varchar(500))，DO 与 RespVO 都带对应字段但写入链路从未赋值；要落 AI 结论可直接复用这两列，无需新增 DDL。测试库现状：fire_alarm_order 0 行、eiot_alert_record 33455 行、eiot_device_info 13 行——工单展示数据实际全部来自告警记录表。
- 租户安全：getAlarmOrder 已内含 UserTenantResolver 的登录租户过滤（admin 返回 null 时保留看全部），跨模块调用它做只读取数不需要再叠一层 executeWithTenantAware。

适用边界：处置中心/移动端告警工单相关的取数与改造。不适用：摄像头原始告警表 eiot_device_alert_record 链路。
