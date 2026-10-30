---
title: "贵州推送SQL移除access_status过滤"
usage_scenario:
    - "执行SQL查询时审查是否包含access_status过滤条件"
    - "排查数据库表中access_status字段的业务语义和使用范围"
    - "对特定企业进行数据探查时验证access_status字段的过滤逻辑"
keywords:
    - "贵州推送"
    - "SQL过滤"
    - "access_status"
source: "auto"
---

贵州数据推送服务的所有SQL查询（包括企业基础、人员、仓库、库房、服务器、设备、指标、报警等15类）均需移除access_status = 1过滤条件，以支持正式环境的全量推送逻辑。
