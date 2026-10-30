---
title: "作业票operatorId采用逐人兼容而非统一单一ID口径"
usage_scenario:
    - "决定作业票人员标识取值口径时"
    - "评估改后端映射还是改APP发版"
    - "跨系统历史数据分代无法归一时选型"
keywords:
    - "operatorId逐人兼容"
    - "两代ID并存"
    - "免APP发版"
    - "outid统一代价"
    - "sync_id无来源"
    - "推送mock开关"
source: "auto"
---

Conclusion: 作业票 operatorId 采用"逐人兼容"口径——人员在本地有 sync_id 且与园区侧一致时沿用 sync_id，新录入无 sync_id 来源的人员改用 outid，由后端在推送前统一映射，不改 APP。
Rationale (trade-off): 园区人员定位基础信息表中已有两代ID（历史内部人员为19位雪花、2026-03-13 后进园区者为 UUID），任何单一口径都会牺牲一代人；改后端映射只需一处改动即可当天验证，而改 APP 选人链路涉及 8 个 Fragment 且必须重新发版才能生效，交付成本与回归面显著更高。
Rejected alternatives: ① 全部统一 outid——口径最干净，但老人员在园区存的是雪花ID，会批量匹配失败，需园区配合改历史数据；② 全部统一 sync_id——新录入外施队人员没有 sync_id 产生渠道（整条定位同步链路不写该字段，唯一写点的从业人员定时任务处于停用状态），需园区提供查询或回写接口才可能实现。两者都是在等待外部条件时才成立，不是当下可执行项。
Applicable / expiry conditions: 在"园区侧人员ID按首次上报标识分代存储、且园区不提供历史数据改造"期间有效；一旦园区确认可批量把历史人员ID归一为 outid，或提供了人员ID查询/回写接口，应切换为单一口径并删除这层映射。注意本决策依赖推送总开关 PLATEMENT_PUSH_ENABLED 为真实生效值——该开关为 false 时所有 HTTP 推送返回 mock 成功，联调结论不可信。
