---
title: "创建coding-ability-training Skill"
usage_scenario:
    - "为新训练方法、学习计划或工作流创建正式Skill时的标准化流程"
    - "排查Skill未生效问题时回溯目录结构与元数据完整性"
keywords:
    - "Skill创建"
    - "coding-ability-training"
    - "SKILL.md"
---

## 任务描述
在 hc-standard 项目中创建名为 coding-ability-training 的新 Skill，用于支持编程能力自主提升训练计划。

## 执行过程
```mermaid
graph TD
    A[需求:创建训练Skill] --> B[搜索现有Skill目录结构]
    B --> C[读取.template/下SKILL.md和.openskills.json模板]
    C --> D[确认无重名Skill存在]
    D --> E[创建d:\code\hc-standard\.agent\skills\coding-ability-training目录]
    E --> F[写入SKILL.md：完整Markdown格式训练计划]
    F --> G[写入.openskills.json：含source/local和安装时间戳]
```

## 任务总结
成功创建 coding-ability-training Skill，包含标准化 SKILL.md（含三阶段训练流程、AI使用边界总则、配套习惯）和 .openskills.json 元数据文件，触发关键词已明确定义，可被系统识别并激活。
