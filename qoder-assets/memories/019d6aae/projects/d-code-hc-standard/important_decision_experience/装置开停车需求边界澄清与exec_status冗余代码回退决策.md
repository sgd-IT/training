---
title: "装置开停车需求边界澄清与exec_status冗余代码回退决策"
usage_scenario:
    - "当评估装置开停车类需求是否需新增开发时"
    - "当发现因需求误解而引入冗余代码时"
keywords:
    - "装置开停车"
    - "exec_status"
    - "风险单元启停"
    - "需求边界澄清"
source: "auto"
---

Conclusion: 装置开停车需求本质是风险单元启停逻辑（base_risk_unit_log + autoSyncUnitStatus），无需在device_ctrl上新增exec_status字段及配套调度。
Rationale (trade-off): 风险单元启停已覆盖需求9全部语义，且其状态同步链路（saveEnable）已与园区平台对齐；新增exec_status会引入重复维护、状态不一致和调度冲突风险，权衡后选择复用现有机制而非新建。
Rejected alternatives: 在device_ctrl上新增exec_status字段及每分钟调度以实现装置开停车自动执行——被拒绝，因其与风险单元启停功能重叠，且无真实控制物理装置的业务场景支撑。
Applicable / expiry conditions: 适用于当前系统架构下所有'装置按设定时间自动开停车'类需求；当未来系统需对接DCS/生产控制系统并具备真实下发指令能力时，此决策需重新评估。
