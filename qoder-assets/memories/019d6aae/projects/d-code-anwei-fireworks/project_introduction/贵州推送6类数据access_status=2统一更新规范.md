---
title: "贵州推送6类数据access_status=2统一更新规范"
usage_scenario:
    - "新增或重构贵州推送类型时同步补充access_status=2更新逻辑"
    - "排查推送状态不一致问题时核对各类型是否执行状态更新"
    - "代码审查时验证增量推送状态标记完整性"
    - "审查贵州推送相关配置时验证各类型是否启用access_status=2状态标记逻辑"
keywords:
    - "贵州推送"
    - "access_status=2"
    - "增量推送"
    - "状态标记"
    - "SXTFX"
    - "ZBSS"
    - "ZBBJ"
    - "BJCZ"
    - "RYCRJL"
    - "CLCRJL"
    - "saveInfoTargetRealtime"
    - "saveInfoTargetDisposal"
source: "auto"
---

贵州推送系统中，SXTFX（视频报警）、ZBSS（监测指标实时数据）、ZBBJ（监测指标报警）、BJCZ（报警处置）、RYCRJL（人员出入）、CLCRJL（车辆出入）共6类数据，在上传成功后必须统一将对应记录的`access_status`字段更新为2，以标识已成功推送。
