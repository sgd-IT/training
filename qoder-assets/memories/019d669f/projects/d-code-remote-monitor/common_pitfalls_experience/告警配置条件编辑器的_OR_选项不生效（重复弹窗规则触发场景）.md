---
title: "告警配置条件编辑器的 OR 选项不生效（重复弹窗/规则触发场景）"
usage_scenario:
    - "配置多条件告警规则时"
    - "排查告警规则未按预期触发时"
    - "改造条件编辑器时"
keywords:
    - "OR逻辑失效"
    - "条件评估"
    - "ConditionEvaluator"
    - "多条件规则"
    - "重复弹窗"
    - "规则触发"
source: "auto"
---

Bug class: 告警配置条件编辑器 OR 逻辑失效
Root cause: 前端保存时未携带 logic 字段（DeviceAlertConfigSaveReqVO/ConditionVO 无该字段，conditions JSON 也无该字段），后端 ConditionEvaluator 的评估逻辑为 hasTrue && !hasFalse（所有条件都满足才触发），仅 AND 语义。
Fix pattern: 要么在 VO/存储/评估器补齐 logic 字段支持 OR，要么先移除界面的 OR 选项避免误导。
Reusable lesson: 不要选择告警配置条件编辑器的 OR 选项，因为后端实际只支持 AND 语义；若需 OR 功能，必须后端补全 logic 字段。适用于 2026-09 前代码状态；当 ConditionEvaluator 支持 logic 字段后不再适用。

## Notes
- Verified during this task: the UI offers an OR toggle but saves no logic field; backend evaluation remains strictly AND-only.
