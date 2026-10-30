---
title: "报警记录action_status积压：消警依赖前端轮询监控页机制缺陷"
usage_scenario:
    - "排查漏铝报警记录长期未消警或积压"
    - "用户反馈监控页看不到报警时的根因定位"
    - "评估消警机制改造必要性前"
keywords:
    - "前端轮询消警"
    - "action_status积压"
    - "videoOffAlarm"
    - "热成像不消警"
    - "mon_aluminum_leakage_alarm_record"
source: "auto"
---

Bug 类型：漏铝报警"未消警积压"或"看起来没报警"被误判为链路故障。

根因：mon_aluminum_leakage_alarm_record 的消警唯一实际入口是前端监控页轮询 todayAlarm 接口触发 videoOffAlarm，且要求铸造机处于铸造中状态（cdReses==1）、报警创建超过 30 秒保护期、每次轮询只消 1 条；没有客户端开页面轮询时报警永远保持 action_status=0 持续积压（实测验证：白天有人开页面时段 168 条全部被消，晚间无人时段 16 条全部未消）。热成像（alarmType=0）分支的消警调用已被注释，该类报警必然一直挂起；批量消警 offAlarm 所有调用处均被注释属死代码。

正确处置：排查"报警记录未消警/列表看不到报警"时，先确认①是否有客户端在轮询监控页、②是否处于铸造中状态、③报警类型是否为热成像，再怀疑链路故障；不能以 action_status 未置 1 直接推断消警逻辑损坏。

适用边界：适用于漏铝记录表（mon_aluminum_leakage_alarm_record）的消警状态排查；不适用于感知数据表 alarm_information——后者由阈值恢复自动消、ZzqjMethod 水泵恢复消警、AlarmEliminationMethod.allClearAlarm 全清，是与前端轮询无关的另一套体系。
