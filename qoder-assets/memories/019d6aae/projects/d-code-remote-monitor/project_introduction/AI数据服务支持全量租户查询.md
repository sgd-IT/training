---
title: "AI数据服务支持全量租户查询"
usage_scenario:
    - "重构AI数据接口时确保绕过租户过滤"
    - "验收AI模块是否满足跨租户数据分析需求"
    - "排查AI接口返回空数据是否因租户隔离导致"
keywords:
    - "AI数据服务"
    - "全量租户"
    - "TenantUtils.executeIgnore"
    - "租户隔离绕过"
source: "auto"
---

AI数据服务模块（AiDataServiceImpl）需支持查询全部租户的数据，不再受租户隔离限制，通过统一使用TenantUtils.executeIgnore()包裹所有数据查询逻辑实现。
