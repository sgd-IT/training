---
title: "AlarmSemantics中alarmKind到level与tier的映射规则"
usage_scenario:
    - "修改或评审摄像头告警定级逻辑"
    - "理解告警记录level与tier字段生成原理"
    - "新增消费方判断二三级事件是否带档位"
keywords:
    - "cameraEventToKind"
    - "deriveLevel唯一来源"
    - "tier档位实测分布"
    - "alarm_type默认值0"
    - "列注释失真"
source: "auto"
---

摄像头告警的最终等级（level）由 eventType 经过两层映射确定：
1. eventType → alarmKind：在 DeviceAlertConfigServiceImpl.reportCameraAlert 中，通过 cameraEventToKind 方法将 eventType 映射为 alarmKind（例如 eventType=4 → KIND_RISK）。
2. alarmKind → level：在 AlarmSemantics.deriveLevel 方法中，根据 alarmKind 决定最终的 level（例如 KIND_RISK → "2"）。
该链路是告警推送逻辑（如 firewatch-alert-new 消息的 level 字段）的唯一来源，任何对告警等级的修改都必须遵循此链路。

第三维 tier 由 AlarmSemantics.defaultTierForKind(kind) 决定——仅 KIND_FIRE 返回兜底档位（Ⅳ档），其余 kind 一律返回 null；摄像头链路的火警类事件在此基础上显式改写为 Ⅲ档（视频源自带画面复核），离岗/通道占用归入隐患二级后 tier 为 NULL。因此"tier 有值 ⇔ level=1"是这条定级链路的固有不变量，新增消费方不得假设二三级事件也带档位。

## Notes
- 【Ⅲ档断言已复核为真（2026-10 生产库 MCP 实测）】此前"AEP 烟感落 tier=4 未见改写痕迹"属误判：那条走的是 W1 规则引擎/AEP 数据通道（AlarmRecordService.resolveTier），本就不经 reportCameraAlert，不能用来证伪 W3。摄像头链路实测确实写 Ⅲ：烟火事件报警 kind=1/level=1/tier=3/type=3（21 条）、热成像事件报警 kind=1/level=1/tier=3/type=2（2 条）。主机配置链路（W5）与 AEP 链路（W1）的火警则统一落 Ⅳ（tier=4，278 条）。即 tier 值域当前只有 {3,4}，Ⅰ/Ⅱ 分支（manual/composite、人工建单）在生产无产出源。
- 【alarm_type 列约束补充】eiot_alert_record.alarm_type 为 tinyint NOT NULL DEFAULT 0。W5（AlertConfigServiceImpl.addAlertRecord）的 AlertRecord.builder 根本没有赋值 alarmType，MapStruct + MyBatis-Plus 默认 FieldStrategy.NOT_NULL 会省略该列，最终靠 DB default 落成 0；W1/W4 则是代码显式写 0。因此"主机链路 alarm_type=0"有两种成因（显式写死 / 未赋值被默认值兜住），排查时不可混为一谈。
- 【列注释失真】DB 中 level 列注释仍是旧编码口径"1=火警 2=故障 3=隐患 4=离线 5=其他"，与新语义（闭集{1,2,3}优先级）不符；alarm_type 注释仍写作"报警类型"，真实语义是事件来源。
