---
title: "BaseRiskTaskServiceImpl.java 新增任务时 work_start_time 必须取自用户输入而非硬编码"
usage_scenario:
    - "当实现任何可调度任务实体的创建逻辑时，避免硬编码派送时间"
    - "当排查用户无法配置任务派送时间的问题时，检查服务层是否硬编码了时间字段"
keywords:
    - "BaseRiskTaskServiceImpl"
    - "work_start_time"
    - "hardcoded"
    - "dispatch time"
source: "auto"
---

Bug class: 排查任务新增时派送时间不可配置
Root cause: BaseRiskTaskServiceImpl.java 第 89-90 行在新增任务时，将 work_start_time 和 work_end_time 硬编码为 "00:00:00"，且前端表单未提供对应输入项，导致全库 141 条任务的 work_start_time 均为 "00:00:00"。
Fix pattern: 修改 BaseRiskTaskServiceImpl.java 新增逻辑，从请求参数中读取 workStartTime，仅在参数为空时回退到默认值 "00:00:00"。
Reusable lesson: Don't hardcode work_start_time to "00:00:00" in the service layer because it makes the dispatch time completely unconfigurable for end users; instead always derive it from the request parameter with a safe default. Applies when implementing the creation logic for any scheduled task entity; does not apply to internal system-generated timestamps without user control.
