---
title: "隐患推送outerId应使用原始ID而非新UUID"
usage_scenario:
    - "隐患状态流转后在平台出现多条重复记录"
    - "平台考核统计隐患数量异常偏高"
    - "排查隐患推送后状态未更新问题"
keywords:
    - "outerId"
    - "隐患状态流转"
    - "saveOrUpdateBatch"
    - "平台接口"
---

隐患推送时`outerId`被错误地设为新生成的UUID（`baseHiddenTroubleUpload.getId()`），导致平台接口`/openapi/check_info/saveOrUpdateBatch`始终当作新增处理，无法实现状态更新。正确做法是将`outerId`设为隐患原始ID（`String.valueOf(baseHiddenTrouble.getId())`），确保平台识别为同一条记录并执行更新。（来源：BaseHiddenTroubleServiceImpl.java）
