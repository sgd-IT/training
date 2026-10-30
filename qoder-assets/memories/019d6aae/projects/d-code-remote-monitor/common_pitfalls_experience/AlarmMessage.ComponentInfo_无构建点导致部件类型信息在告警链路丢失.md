---
title: "AlarmMessage.ComponentInfo 无构建点导致部件类型信息在告警链路丢失"
usage_scenario:
    - "设计依赖部件类型或子设备的定级规则"
    - "判断告警消息某字段是否真有数据"
    - "排查subDeviceName显示为空的根因"
keywords:
    - "ComponentInfo未填充"
    - "部件类型码丢失"
    - "子设备台账缺失"
    - "字段构建点核查"
    - "国标26875部件"
source: "auto"
---

Bug class: 依赖"消息体里已定义的结构字段"作为功能可行性前提，实际该字段从未被填充。

Root cause: AlarmMessage.ComponentInfo（部件类型/名称/描述）在类中定义且注释明确，但全仓 grep setComponentInfo / new ComponentInfo / ComponentInfo.builder 均为 0 匹配——只有读取方（AlarmWebSocketMessage、AlarmRecordService 拼 subDeviceName）而无任何构建点；同时库内无子设备台账表（eiot_device_info 仅 13 台主设备）。因此报文层能解析出的 GB/T 26875 部件类型码（DahuaAduParser 字典：40-44 感烟、30-37 感温、23 手报、96 水流指示器、99 压力开关）在落库前即丢失，无法回溯区分火警来自哪类部件。

Fix pattern: 以"检索是否存在构造/赋值点（setXxx / new Xxx / Xxx.builder）"而非"字段是否声明"来判定数据链路可用性；结论须同时核对落库表列与上游台账实体三层。

Reusable lesson: Don't assume a declared DTO field carries data just because it exists and is read somewhere, because an unpopulated field silently yields null-derived business rules; instead verify the write side (construction/assignment sites) plus persistence columns plus ledger tables before committing a rule that depends on it. Applies when judging whether protocol-parsed attributes (component type, sub-device info, addresses) can support downstream logic in this repo's eiot alarm chain; does not apply to fields proven to be built by a specific parser or strategy class.

## Notes
- 【2026-10-08 复核仍成立】写侧三改造后复查：ComponentInfo 依然零构建点，AlertRecordDO 也无部件类型字段（新加的 alarm_kind/tier 均不承载部件信息）。该缺失正是第3步"一级事件档位一律兜底Ⅳ档"的直接依据——判定原料（部件类型/视频覆盖/多信号）不可得时不猜档位。若要按部件分档，需先补协议层到 ComponentInfo 的赋值链路。
