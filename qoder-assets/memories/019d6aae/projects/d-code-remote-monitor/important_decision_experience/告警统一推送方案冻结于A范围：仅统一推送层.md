---
title: "告警统一推送方案冻结于A范围：仅统一推送层"
usage_scenario:
    - "当规划跨链路告警统一推送架构时"
    - "当评估是否需重构存储层以支持统一告警时"
    - "当部署多租户WebSocket服务时"
keywords:
    - "告警统一推送"
    - "firewatch-alert-new"
    - "duty-screen"
    - "scope A"
source: "auto"
---

Conclusion: Froze the unified alert push solution at scope “A: only unify push layer”, adopting the firewatch-alert-new message type for duty-screen zero-change adoption.
Rationale (trade-off): Avoids breaking changes to monitoring-center (legacy), reduces rollout risk, and leverages existing duty-screen WebSocket listener; full unification (scope B) would require major refactoring of storage schema and frontend listeners across multiple modules.
Rejected alternatives: Unify both storage and push layers (scope B) — dropped due to high complexity, cross-module impact, and risk of regressing existing real-time capability.
Applicable / expiry conditions: Holds while duty-screen is the primary real-time alert interface and monitoring-center remains unchanged; re-evaluate if monitoring-center adopts duty-screen’s WebSocket channel or if a unified alert storage model is mandated.
