---
title: "base_ticket_safe_config 票证安全措施配置表结构"
usage_scenario:
    - "新增或修改票证类型的安全措施配置条目"
    - "排查某类作业票安全措施显示异常或缺失"
    - "需要 ticketsType 与 source_type 对应数值时"
keywords:
    - "base_ticket_safe_config"
    - "source_type映射"
    - "ticketsType编码"
    - "其他安全措施editable"
    - "票证配置表"
source: "auto"
---

项目安全措施配置基于 base_ticket_safe_config 表，通过 source_type 字段关联不同票证类型；source_type 与 ticketsType 的映射关系为：1→11（动火）、2→14（受限空间）、3→18（盲板抽堵）、4→12（高处）、5→15（吊装）、6→13（临时用电）、7→16（动土）、8→17（断路）。该映射即 TicketsType 枚举中各票证的 codeNumber 前缀取值来源（如 DH-、GC-、LD-），作业编号生成逻辑复用同一枚举。所有票证类型的最后一条安全措施均为 '其他安全措施：'，且 editable=1，表示该条目可编辑。该结构和映射关系已在数据库中查询并验证。
