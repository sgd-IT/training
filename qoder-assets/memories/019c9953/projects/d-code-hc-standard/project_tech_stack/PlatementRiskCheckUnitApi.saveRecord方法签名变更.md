---
title: "PlatementRiskCheckUnitApi.saveRecord方法签名变更"
usage_scenario:
    - "调用该API时需传入单个实体而非列表"
    - "排查编译错误'无法应用于List'时定位方法签名不匹配"
    - "新增调用方代码时遵循最新单实体入参规范"
keywords:
    - "PlatementRiskCheckUnitApi"
    - "saveRecord"
    - "单实体参数"
    - "方法签名"
---

项目中`com.ruoyi.common.platement.api.PlatementRiskCheckUnitApi`接口的`saveRecord`方法，已从接收`List<PlatementRiskRecordEntity>`重构为接收单个`PlatementRiskRecordEntity`参数
