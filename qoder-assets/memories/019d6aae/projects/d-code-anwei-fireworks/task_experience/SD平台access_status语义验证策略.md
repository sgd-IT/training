---
title: "SD平台access_status语义验证策略"
usage_scenario:
    - "When refactoring or implementing status-based filtering logic in SD platform"
    - "When debugging '无XX信息，跳过' logs caused by incorrect access_status filtering"
keywords:
    - "SD平台"
    - "access_status"
    - "语义验证"
    - "状态过滤"
    - "跨模块假设"
source: "auto"
---

## Goal
Refactor SD platform push logic using correct `access_status` semantics to ensure accurate eligibility filtering across all base-data entities.

## Steps
Step 1: Verify status semantics with the user before implementation - Confirm the exact meaning of `access_status` values (e.g., 0/1/2) for the SD platform specifically, key constraint: status semantics are not transferable across modules (e.g., GD ≠ SD); assuming equivalence causes systemic mis-filtering
Step 2: Apply the verified semantics uniformly across all related entities - Use the confirmed value (e.g., `access_status = 0`) in all SD push methods (`savePerson`, `saveStore`, `saveStoreroom`, `saveDevice`, `saveComputer`), key constraint: all base-data push methods must use the same value; inconsistency breaks eligibility logic

## Notes
- Assuming status semantics from another module (e.g., GD service) causes incorrect filtering logic across all push methods; must always verify with the user for the target module

## Notes
- This memory has been superseded by the more detailed and up-to-date task summary 'SD平台设备推送storeId为空问题修复', which captures the precise execution and outcome of applying these principles.

## Notes
- 该记忆已被更详细、更新的任务摘要 'SD平台设备推送storeId为空问题修复' 取代，需在Notes中明确标注以避免重复使用

## Notes
- 该语义验证策略已在贵州数据推送流程优化与自动化方案中得到实践验证；具体包括：贵州推送access_status环境配置化、贵州推送access_status查询参数化等核心改进。
