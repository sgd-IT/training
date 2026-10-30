---
title: "CRM 客户调研行业与模板码映射及编号生成规则"
usage_scenario:
    - "新增某行业的客户调研记录需确定模板码"
    - "生成 research_no 需推算当前自增 id"
    - "核对调研模板版本与行业匹配关系"
keywords:
    - "crm_customer_research"
    - "template_code FIRE"
    - "industry_id 映射"
    - "research_no 生成规则"
    - "AUTO_INCREMENT 查询"
source: "auto"
---

CRM 客户调研模块的模板选择由行业决定：industry_id 决定 template_code。industry_id=4（'智慧消防（远程值守）'）对应 template_code='FIRE'，映射到 '智慧消防调研 V1' 版本；该映射经前端 researchTemplates.ts 与后端模板解析逻辑双向确认，新增调研记录时应按行业取对应模板码而不是手工指定版本号。

调研单编号 research_no 的规则为 DY + yyyyMMdd + 6 位补零的自增 id（例：id=6 → DY20261012000006），生成前必须先查 information_schema.TABLES 读取 crm_customer_research 当时的 AUTO_INCREMENT 值再拼编号——该值是运行时快照，任何记录中的具体数值都可能已过期，不可凭记忆填写。
