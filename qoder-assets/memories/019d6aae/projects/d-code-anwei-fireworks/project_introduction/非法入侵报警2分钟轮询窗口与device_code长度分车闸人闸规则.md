---
title: "非法入侵报警2分钟轮询窗口与device_code长度分车闸人闸规则"
usage_scenario:
    - "排查非法入侵轮询未命中或误命中记录"
    - "编写按device_code长度区分车闸人闸的逻辑"
    - "核对2分钟轮询窗口与alarm_status=0未消警条件"
keywords:
    - "非法入侵报警轮询"
    - "2分钟轮询窗口"
    - "device_code长度分闸机"
    - "车闸syncId前提"
    - "人闸voiceMaxAgeMinutes"
source: "auto"
---

非法入侵报警（alarm_type=4）定时轮询路径的播报触发规则（区别于 save 路径的 syncId 判据，见「非法入侵报警save路径车闸人闸统一播报分支逻辑」）：

1. 轮询查询最近 2 分钟内报警类型 4 的未消警记录（alarm_status=0）。
2. 通过 device_code 长度区分闸机类型：7位（油车车牌）/8位（新能源车牌）→ 车闸；其他长度 → 人闸。
3. 车闸数据必须有 syncId 才播报；无 syncId 的车闸记录不播报。
4. 人闸数据有两种来源：同步方式（有 syncId）和本地 save 方法（无 syncId）；本地 save 的人闸按 voiceMaxAgeMinutes 判断时效后播报 1 次。

注：本条仅描述轮询查询与闸机类型区分逻辑；具体播报次数与是否跳过由 save 路径的 syncId 判据统一控制。
