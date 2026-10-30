---
title: "小程序权限放开业务诉求与user_type位掩码技术实现的权衡决策"
usage_scenario:
    - "当业务方提出'小程序权限全部放开'的需求时，理解其与现有技术实现的冲突点"
    - "当需要为新账号默认开通小程序权限时，确认是否应修改 user_type 默认值"
keywords:
    - "小程序权限放开"
    - "user_type位掩码"
    - "登录门禁"
    - "权限校验"
source: "auto"
---

Conclusion: 小程序权限放开的业务诉求，当前技术实现仍基于 user_type 位掩码进行登录门禁和接口权限校验。
Rationale (trade-off): 业务上要求所有账号都能使用小程序，技术上 user_type 位掩码提供了清晰的平台归属管理能力；完全放开会失去对 PC/移动端账号的区分能力，影响未来精细化运营和策略制定。
Rejected alternatives: 彻底移除所有权限校验（如删除 checkLoginPlatform 和 hasMobilePlatformAccess），使所有登录账号均可访问所有小程序接口 —— 被拒绝，因为这会破坏平台账号的隔离性，导致后续无法为不同平台账号提供差异化服务。
Applicable / expiry conditions: 当前决策适用于项目初期快速上线和验证阶段；当业务明确要求必须支持 PC 账号直接使用小程序时，需重新评估并可能引入新的权限模型（如租户级开关或更细粒度的角色控制）。
