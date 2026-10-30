---
title: "报警表定时轮询未消警记录并Redis去重播报"
usage_scenario:
    - "为报警类表新增定时轮询任务以覆盖外部直写场景"
    - "实现定时任务+Redis去重防止重复处理的通用模式"
    - "排查报警漏播报时快速定位轮询逻辑与去重机制"
keywords:
    - "定时轮询"
    - "Redis去重"
    - "alarm_status=0"
source: "auto"
---

## 任务描述
为em_alram_info表实现非法入侵报警（alarm_type=4）的语音播报覆盖，解决车闸等外部系统同步写入数据库（跳过save方法播报逻辑）导致的漏播报问题。

## 执行过程
```mermaid
graph TD
    A[需求:车闸同步报警漏播报] --> B[分析save方法仅判断当前entity的syncId]
    B --> C[车闸syncId为空被跳过]
    C --> D[改为查询最近3分钟内报警类型4记录]
    D --> E[通过device_code长度区分车闸人闸:7/8位为车闸]
    E --> F[有syncId记录直接播报2次Redis去重]
    F --> G[人闸无syncId走本地save逻辑按voiceMaxAgeMinutes判断]
    G --> H[车闸无syncId不播报]
```

## 任务总结
在AlramInfoServiceImpl.save方法中，对于报警类型4，不再仅判断当前entity的syncId，而是查询最近3分钟内报警类型4的未消警记录（limit 50），对每条记录通过device_code长度区分车闸/人闸，有syncId的直接播报（Redis setNx去重，key=alarm:voice:broadcast:{syncId}，过期180秒），人闸无syncId走本地save逻辑，车闸无syncId不播报。注入RedisUtils，新增handleIllegalIntrusionAlarms、isCarPlateLength、broadcastWithDedup、handleLocalPersonGateAlarm四个方法。

## Notes
- 本条与「车闸报警语音播报逻辑重构」（id 5370cf7a-9b2a-4c92-9cff-2ed969443e53）记录的是同一次改动，内容完全重叠；该条为权威版本，后续查阅以它为准，本条仅作历史快照保留。
- 其中"扫描窗口与过期时间统一为3分钟"的描述已过时：现行轮询窗口为最近 2 分钟（见 project_introduction「非法入侵报警2分钟轮询窗口与device_code长度分车闸人闸规则」），排障时按 2 分钟核对。
