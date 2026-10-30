---
title: "系统租户视图缺失tenant_id使idx_tenant_alert_time首列失效"
usage_scenario:
    - "排查同一SQL时而走索引时而全表扫"
    - "为含租户拦截器的表设计复合索引列序"
    - "解释平台账号与账号统计结果差异"
keywords:
    - "TenantDatabaseInterceptor情况三"
    - "系统租户不下钻"
    - "tenant_id条件缺失"
    - "索引首列失效"
    - "type=ALL全表扫描"
source: "auto"
---

TenantDatabaseInterceptor#ignoreTable 规定：当登录用户属系统租户且未下钻到具体租户时，跳过为 SQL 注入 `tenant_id = ?`，改由数据权限规则接管。由此产生一条反直觉后果——凡是不在 platform.tenant.ignore-tables 名单里的表（如 eiot_alert_record），其 SQL 在平台账号全量视图下会**没有 tenant_id 条件**，而该表唯一二级索引 idx_tenant_alert_time 以 tenant_id 打头，首列等值缺失即整体不可用，EXPLAIN 表现为 type=ALL、possible_keys=NULL、全表扫描 + Using temporary + Using filesort；换成企业账号或已下钻物业时同一条 SQL 变为 type=ref 命中索引。

因此排查"同一条 SQL 有时走索引有时不走"必须先确认发起方是系统租户还是企业租户，而不是怀疑索引缺失；设计该表新索引时也不能默认 tenant_id 一定出现在 WHERE 里。另注：数据中心 FireDataCenterServiceImpl 的 inScope() 虽用 TenantUtils.execute 包裹，但对系统租户同样不产生 tenant_id 条件。
