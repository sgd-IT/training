---
title: "Java+MyBatis数据访问分层规范"
usage_scenario:
    - "新增推送逻辑时选择合适的数据加载方式"
    - "排查推送缺失子表数据问题时定位到Mapper/Service差异"
    - "代码审查时检查是否按规范加载关联数据"
keywords:
    - "Java"
    - "MyBatis"
    - "Mapper"
    - "Service分层"
    - "子表加载"
source: "auto"
---

项目采用Java技术栈，基于MyBatis实现数据访问层，区分Mapper（仅查主表）与Service（查主表+关联子表）两层数据加载逻辑
