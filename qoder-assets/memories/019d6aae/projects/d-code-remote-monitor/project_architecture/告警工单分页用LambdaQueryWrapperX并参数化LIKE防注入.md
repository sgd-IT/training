---
title: "告警工单分页用LambdaQueryWrapperX并参数化LIKE防注入"
usage_scenario:
    - "构造告警工单分页查询选择wrapper类型"
    - "编写可选模糊搜索条件时避免字符串拼接"
    - "按角色决定是否追加tenant_id过滤"
keywords:
    - "LambdaQueryWrapperX"
    - "doPageFromAlertRecord"
    - "likeIfPresent参数化"
    - "SQL注入防护"
    - "租户权限分支"
source: "auto"
---

## Goal
在 FireAlarmOrderServiceImpl.doPageFromAlertRecord 中构造动态查询时，选用 MyBatis-Plus 的 LambdaQueryWrapperX（支持 xxxIfPresent 空值自动跳过）而非 XML mapper 或原生 QueryWrapper 列名字符串，并为模糊筛选做参数化绑定。

## Steps
Step 1: 条件构造 — 用 LambdaQueryWrapperX 以 getter 引用字段拼等值/区间条件，input: 查询条件 VO，output: 未含租户条件的 wrapper；rationale: 列名字符串在字段改名后静默失配
Step 2: 模糊筛选 — 单位名/设备名等文本条件用 likeIfPresent(FireAlarmOrderDO::getUnitName, val)，由框架完成 %val% 包裹与 {0} 占位绑定，input: 可空文本参数，output: 参数化 LIKE 片段；rationale: 手工 format 拼 "%"+val+"%" 直插 SQL 存在注入风险
Step 3: 租户范围 — 需跨租户查看时走 TenantUtils.executeIgnoreWithResult 关闭拦截器并按入参 eqIfPresent(tenant_id)，普通租户交由拦截器自动过滤，input: 角色判定结果，output: 带租户范围的 wrapper
Step 4: 排序分页 — orderByDesc(创建时间) + page(pageNo, pageSize)，input: 完成条件的 wrapper，output: 分页结果
Step 5: 编译验证 — mvn -pl module-eiot/module-eiot-biz compile -DskipTests，input: 改动文件，output: BUILD SUCCESS

## Notes
- 手工拼接 %val% 字符串 causes SQL 注入风险，must 用框架的 like/likeIfPresent 参数化绑定
- 角色判定取 TenantContextHolder.getTenantId() causes 系统租户退化全量而越权，must 改用登录用户上下文（见 TenantContextHolder 禁用坑点记忆）
- 显式 eq(tenant_id) 与拦截器自动过滤叠加 causes 跨租户切换静默失效，must 先确认该查询走的拦截器口径再决定是否手写
- fire_property 仅有 PRIMARY KEY(id) 与 UNIQUE KEY uk_tenant_id(tenant_id)，物业与租户是 1:1，故按单位名过滤在数据模型上等价于按租户过滤
