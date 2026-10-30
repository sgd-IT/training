---
title: "application.yaml混合配置文件必须采用surgical edit模式"
usage_scenario:
    - "When modifying config files that contain mixed business and environment settings, such as application.yaml or application-prod.yaml"
    - "When adding URL whitelists or security configurations without leaking profiles.active"
keywords:
    - "application.yaml"
    - "git apply --cached"
    - "零上下文补丁"
    - "surgical edit"
    - "environment leakage"
source: "auto"
---

Bug class: application.yaml配置文件编辑导致环境配置误提交
Root cause: 直接修改application.yaml文件时，未隔离profiles.active等环境专用配置行，导致在git add/commit时一并提交了用户明确禁止的环境变更。
Fix pattern: 先用git checkout HEAD -- application.yaml恢复原始版本，再通过git apply --cached --unidiff-zero <patch>在索引中插入业务相关的URL白名单行（补丁内容必须为ASCII纯文本），最后仅暂存并提交这些新增行。
Reusable lesson: Don't directly edit config files containing mixed business/environment settings because it risks submitting environment-specific changes; instead, restore to HEAD first, then use git apply --cached to surgically insert only the business-relevant lines. Applies when editing config files like application.yaml that contain both business rules and environment declarations; does not apply to pure business config files (e.g., logback.xml, routes.json).
