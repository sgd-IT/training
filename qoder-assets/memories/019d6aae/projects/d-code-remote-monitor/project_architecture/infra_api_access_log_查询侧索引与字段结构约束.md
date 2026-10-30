---
title: "infra_api_access_log 查询侧索引与字段结构约束"
usage_scenario:
    - "改造或调优访问日志分页与导出接口"
    - "为该表新增筛选条件前判断索引可用性"
    - "排查日志表慢查询与全表扫描成因"
keywords:
    - "idx_create_time单索引"
    - "begin_time与create_time双列"
    - "TenantDatabaseInterceptor隐式过滤"
    - "request_params截断8000"
    - "PAGE_SIZE_NONE全量导出"
    - "COUNT全表扫回表"
    - "filesort排序错位"
source: "auto"
---

infra_api_access_log（API 访问日志表）的查询侧结构约束，实测于 _sql/mysql/system.sql 建表段与 ApiAccessLogMapper / ApiAccessLogDO / BaseMapperX / TenantDatabaseInterceptor：

- 该表只有一个索引 idx_create_time(create_time)。但分页接口 ApiAccessLogMapper.selectPage 的时间过滤用的是 begin_time（betweenIfPresent(getBeginTime)），排序用的是 orderByDesc(id)，三者全部与索引错位——任何带/不带时间条件的分页都退化为全表扫 + filesort。
- create_time 由数据库默认值 CURRENT_TIMESTAMP 写入，begin_time 由应用侧写入，两者实际只差几毫秒。因此"把过滤与排序对齐到 create_time"是零表结构变更的唯一可用优化路径；统计类聚合 SQL 同样必须按 create_time 过滤。
- ApiAccessLogDO 继承 BaseDO，deleted 由 @TableLogic 自动追加为 deleted=0；DO 未映射 tenant_id 列，但多租户插件 TenantDatabaseInterceptor 仍会在 SELECT 上自动拼 tenant_id=?（Service 方法无 @TenantIgnore 时生效），所以 tenant_id 也是实际过滤条件却不在任何索引内。超管统计接口靠 @TenantIgnore 绕过该条件。
- request_params(text，写入时截断 8000)、response_body(text，仅 @ApiAccessLog(responseEnable=true) 才写)、user_agent(varchar 512) 三个大字段只在详情弹窗使用，列表页不展示；而 selectPage 走 BaseMapperX 默认 SELECT *，每页都会把它们拉回来。
- 导出接口 exportApiAccessLogExcel 将 pageSize 置为 PAGE_SIZE_NONE 走全量 selectList，配合 ExcelUtils.write 的全内存（非流式）实现，构成大表导出的 OOM 落点。
