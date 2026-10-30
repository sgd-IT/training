---
title: "skill 用量盘点优先手工转写扫描而非 harness observed 报表"
usage_scenario:
    - "评估 AI 资产使用频次时选择证据来源"
    - "工具报表给出全量未观测结论时的复核"
    - "决定是否申请读取原始会话存档"
keywords:
    - "unobserved不等于unused"
    - "transcript手工扫描"
    - "最小会话门槛"
    - "记忆库轨迹交叉印证"
    - "原始会话源授权边界"
source: "auto"
---

Conclusion: 回答"哪些 skill 不常用"这类资产用量问题时，优先自建只读的 transcript 关键词扫描 + 记忆库任务轨迹双轨取证，而不是直接采信 better-harness 的 observed/unobserved 报表。

Rationale (trade-off): harness 报表是现成能力、口径统一，但它要求至少 5 个合格会话才做推断，而本机本地会话归档在 2026-09-11 之后不再增长，eligibleSessions=0 使它把所有 37 个 skill 一律标为 unobserved——这是"无数据"而非"未使用"，直接采信会把 9 月 11 日后新建的 skill 全部误判成闲置。自建扫描零依赖、可按项目分组统计命中数与去重会话数，代价是需要额外抽命中行上下文来排除"仅在清单里被罗列"的假阳性。

Rejected alternatives: ① 直接按 harness 的 unobserved 输出结论 —— 舍弃原因是会把数据缺失当结论，误导用户删除其实仍在用的 skill；② 申请授权读取原始会话存储（checkup 显示 cache-project-session 与 global-project-jsonl 两源 exists=true 但 enabled=false）以拿到完整覆盖 —— 舍弃原因是涉及隐私边界须用户明确授权，且本轮已有足够旁证，不宜擅自扩大读取范围。

Applicable / expiry conditions: 在本地会话归档存在时间断层、或合格会话数低于工具最小门槛时成立；一旦用户授权启用原始会话源、或 Qoder 恢复向本地写入完整对话记录，应改回以 harness 的 observed 统计为准，此时手工扫描退化为校验手段而非主证据。
