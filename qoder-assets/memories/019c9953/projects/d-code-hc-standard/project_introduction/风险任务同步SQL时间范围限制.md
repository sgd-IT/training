---
title: "风险任务同步SQL时间范围限制"
usage_scenario:
    - "排查类似OOM问题时参考时间过滤修复模式"
    - "新增批量查询接口时主动添加时间范围约束"
    - "评审SQL性能时检查是否存在隐式全表扫描"
keywords:
    - "OOM修复"
    - "SQL时间限制"
    - "风险任务同步"
---

项目风险任务同步功能存在性能瓶颈：`getSyncRecordList` SQL查询无分页和时间限制，导致`/api/risk/updTaskRecordStatus`接口在处理大量未同步记录（14,077条）时触发OOM；已修复为增加`AND r.check_time >= DATE_SUB(NOW(), INTERVAL 2 MONTH)`条件，将待同步数据量控制在3,329条以内。
