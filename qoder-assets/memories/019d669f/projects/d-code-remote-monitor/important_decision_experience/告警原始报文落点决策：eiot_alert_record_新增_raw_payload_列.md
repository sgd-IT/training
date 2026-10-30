---
title: "告警原始报文落点决策：eiot_alert_record 新增 raw_payload 列"
usage_scenario:
    - "实施B方案统一告警落库时"
    - "设计告警详情页原始报文展示时"
    - "排查告警解析异常需回溯原文时"
keywords:
    - "raw_payload"
    - "原始报文落库"
    - "报文追溯"
    - "告警取证"
source: "user"
---

结论：告警原始报文的落点选定为"随告警记录入库"——eiot_alert_record 新增 raw_payload 列（TEXT），落库时写入原文：UDP/TCP 存整帧 hex，AEP 存 Controller 收到的原始 JSON body。
权衡理由：告警为事件级低量数据，TEXT 列成本可忽略；相比仅打日志，可随告警记录检索，支撑详情页展示、工单取证与解析问题回溯。
被拒替代：仅打日志（日志轮转后不可查、无法按告警记录检索）；拼入 details（污染面向人读的文本字段）。
适用与失效条件：适用于 B 方案统一告警链路；配套改造：AEP 需将 Controller rawBody 直传 AlarmParseContext（否则存下的是有损 DTO 序列化），UDP/TCP 策略需对所有告警帧填充 rawData（当前仅解析失败帧携带）；若告警量级跃升至遥测级别需重新评估存储方案。
