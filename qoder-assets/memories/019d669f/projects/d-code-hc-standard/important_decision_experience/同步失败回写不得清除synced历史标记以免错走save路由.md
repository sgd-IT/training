---
title: "同步失败回写不得清除synced历史标记以免错走save路由"
usage_scenario:
    - "实现对外同步失败的异常回写逻辑"
    - "排查记录重试误走平台新增接口问题"
    - "设计带历史成功标记的同步状态列"
keywords:
    - "updateSyncResult"
    - "synced历史标记"
    - "save与update路由"
    - "失败状态回写"
    - "双列同步设计"
source: "auto"
---

Conclusion: BaseServiceRecordServiceImpl.sync 异常分支回写失败状态时，调用 updateSyncResult(id, "2", msg, null) 最后参数固定传 null——只更新 sync_status/sync_result，绝不清除 synced 历史成功标记。
Rationale (trade-off): synced 是区分平台 save（首次上报）与 update（修改上报）的路由依据；若失败时顺手把 synced 清零，一条曾经上报成功的记录在重试时会错误地走 save 接口，造成平台侧重复或主键冲突。保留标记的代价仅是"失败记录仍显示曾成功过"，远小于路由错误的修复成本。
Rejected alternatives: ①失败时同时置 synced='0'（看似状态一致，实际破坏 save/update 路由）；②异常只记日志不回写（原实现，导致界面显示已同步但平台无数据，状态双向失真）。
Applicable / expiry conditions: 适用于带"历史成功标记 + 瞬时结果状态"双列设计的对外同步场景；若平台侧改为幂等 upsert 接口、不再依赖 synced 做路由，可重新评估是否允许失败时清标记。
