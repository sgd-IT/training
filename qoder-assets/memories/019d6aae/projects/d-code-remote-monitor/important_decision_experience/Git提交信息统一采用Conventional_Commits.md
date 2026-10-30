---
title: "Git提交信息统一采用Conventional Commits"
usage_scenario:
    - "执行git commit或push时"
    - "新建项目或团队制定提交规范时"
    - "为新成员讲解项目贡献指南时"
keywords:
    - "Conventional Commits"
    - "type scope 前缀"
    - "中文描述"
    - "提交规范"
source: "user"
---

Conclusion: Git 提交信息统一采用 Conventional Commits 格式（type(scope): 中文描述，type 与 scope 用英文小写，描述用中文），废止此前「大白话纯中文、无前缀」的风格。
Rationale (trade-off): 标准化前缀可被工具解析（生成变更日志、按类型回溯筛选），描述保留中文兼顾团队可读性；代价是书写稍繁琐。
Rejected alternatives: 纯大白话无前缀风格（团队原有做法，因用户要求统一规范而放弃）；全英文描述（不利于中文团队快速阅读）。
Applicable / expiry conditions: 当前项目与团队约定下有效；若团队改用其它提交规范再评估。
