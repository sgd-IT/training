---
title: "时间字段SQL映射规范"
usage_scenario:
    - "编写或修改MyBatis XML映射SQL时处理时间字段"
    - "排查类似'get column from result set'错误时参考修复方案"
keywords:
    - "DATE_FORMAT"
    - "SQL映射"
    - "时间字段"
---

SQL查询中涉及时间字段映射到String类型时，应使用DATE_FORMAT函数格式化为字符串，避免数据库连接不稳定时因类型转换失败导致异常
