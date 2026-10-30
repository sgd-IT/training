---
title: "API访问日志分页查询链路瓶颈诊断与无索引约束优化方案"
usage_scenario:
    - "排查访问日志分页慢查询或设计其优化方案"
    - "评估日志类大表在不加索引前提下的查询改造"
    - "判断清理任务与SQL优化孰先孰后"
keywords:
    - "api-access-log分页"
    - "idx_create_time错位"
    - "SELECT大字段瘦身"
    - "PAGE_SIZE_NONE导出OOM"
    - "多租户列过滤"
    - "详情接口拆分"
source: "auto"
---

## Task description
- Core requirement: 用户要求"优化访问日志的查询接口"，经确认选择"先只出方案不动代码"且"暂不加索引"。
- Task background: /infra/api-access-log/page 由 Controller→Service（无 @TenantIgnore）→Mapper.selectPage 走 BaseMapperX 的 SELECT *；表 infra_api_access_log 已堆积约 107 万行、跨 5 个多月；上一轮已确认 AccessLogCleanJob 处于暂停从未执行。

## Execution process
1. 链路测绘：确认实际 SQL 形态为 `SELECT * FROM infra_api_access_log WHERE deleted=0 AND tenant_id=? AND begin_time BETWEEN ? AND ? ORDER BY id DESC LIMIT 10`，另附一条 COUNT(*)；tenant_id 条件由 TenantDatabaseInterceptor 自动追加（DO 未映射该列但插件仍生效），deleted 来自 BaseDO 的 @TableLogic。
2. 瓶颈定位 7 项：①SELECT * 带回 request_params(text≤8000)/response_body(text)/user_agent(512) 三个列表页不展示的字段，最坏单页响应体约 80KB；②过滤用 begin_time 而表上唯一索引是 idx_create_time(create_time)，完全错位；③ORDER BY id DESC 无索引可用必 filesort；④tenant_id 不在任何索引；⑤requestUrl 用 likeIfPresent 生成前后模糊；⑥每页都算全量 COUNT；⑦导出 exportApiAccessLogExcel 把 pageSize 设成 PAGE_SIZE_NONE 全量 selectList，而 ExcelUtils.write 是全内存非流式，百万行有 OOM 风险。
3. 方案落地 5 项（全部不改表结构）：Service 层强制默认时间范围（空则近 7 天、跨度>90 天截断，不报错以兼容老前端）；Mapper 第 54 行过滤字段 begin_time→create_time 对齐索引；第 57 行排序改 orderByDesc(createTime).orderByDesc(id) 消除 filesort 并保证同秒翻页稳定；列表显式排除三大字段并新增 GET /infra/api-access-log/get?id=（权限复用 infra:api-access-log:query），前端 ApiAccessLogDetail.vue 由 open(data) 改 open(id) 异步取数；导出加 5 万行硬上限。
4. 明确放弃项：likeRightIfPresent 仅在需要时做；深翻页 pageNo 上限、count 估算近似值两条判定为不做（日志真实用例是"看最近的"）。
5. 关键前置判断：若 14 天清理真正生效，表规模稳在 6300 行/天×14≈8~9 万行，上述性能收益会被大幅稀释——开清理任务是"釜底抽薪"、接口优化是"止血"，只能先做一件时应先开清理任务。
6. 证据边界声明：当前工作区 mcps 目录仅有 hc-standard-test、tgr-gz-vue，无本项目 MySQL MCP，全部结论为代码级推断，未跑 EXPLAIN；方案给出验收断言 type=range、key=idx_create_time、Extra 不含 Using filesort。

## Related files
- module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/dal/mysql/logger/ApiAccessLogMapper.java
- module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/controller/admin/logger/ApiAccessLogController.java
- module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/service/logger/ApiAccessLogServiceImpl.java
- module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/dal/dataobject/logger/ApiAccessLogDO.java
- framework/spring-boot-starter-mybatis/src/main/java/com/enjoyiot/framework/mybatis/core/mapper/BaseMapperX.java
- framework/spring-boot-starter-biz-tenant/src/main/java/com/enjoyiot/framework/tenant/core/db/TenantDatabaseInterceptor.java
- _sql/mysql/system.sql
- anwei-fire-remote-monitor-vue/src/views/infra/apiAccessLog/index.vue
- anwei-fire-remote-monitor-vue/src/views/infra/apiAccessLog/ApiAccessLogDetail.vue

## Notes
- 本轮为纯调研+方案交付，未修改或创建任何文件；用户选择只出方案不动代码。
- begin_time→create_time 是本方案唯一的语义偏移，前提是应用与 MySQL 时钟/时区一致（项目已统一 Asia/Shanghai），上线前建议抽查 TIMESTAMPDIFF(SECOND, begin_time, create_time) < 5，偏差大则回退该项只保留默认时间范围与排序对齐。
- 列表瘦身与详情新接口是前后端强耦合，必须同一次发版，否则详情弹窗的请求参数/响应结果空白。
- 超管统计接口的 4 个 GROUP BY（实测 300~500ms）本次明确排除在范围外。

## Notes
- 【后续轮次补记】用户实测反馈"EXPLAIN 命中索引仍然慢"后复核，原【执行过程】第 1 步对 SQL 形态的描述不完整，需按以下最新结论修正：
  - 每次翻页实际发出两条 SQL（PaginationInnerInterceptor searchCount 默认 true）：列表 SQL 与 COUNT(*) SQL。无任何时间筛选时列表走 PK 反向扫描读 10 行即停、毫秒级；COUNT 因 deleted/tenant_id 不在 idx_create_time 内无法覆盖索引，需约 107 万次回表，才是主要耗时来源——"命中索引还慢"应优先怀疑 count 而非 list。
  - 租户条件按登录身份分三种形态：企业租户为 tenant_id=?；系统租户+非监管角色完全不拼 tenant_id 条件；系统租户+监管角色拼 tenant_id IN(...)（无区域时 =-1）。不同账号测的不是同一条 SQL。
  - infra_api_access_log 不在 ignore-tables（仅 system_tenant、system_tenant_package）。
  - 原【执行过程】第 4 步"count 不改"的判断已收回，替代方案：游标式翻页去掉 count，或仅第一页算 count。
  - 真实 SQL 获取方法：logging.level 设为 Mapper 接口 debug 打印 Preparing/Parameters，或查 performance_schema.events_statements_history_long 的 ROWS_EXAMINED。

## Task overview
已完成方案交付（按用户要求未落任何代码）：摸清访问日志分页链路真实 SQL 形态，定位 7 项瓶颈（大字段回传、过滤/排序与 idx_create_time 三重错位、租户列无索引、前后模糊、全量 COUNT、导出全内存 OOM 风险），产出在无索引约束下的 5 项后端改造 + 3 项可选 + 3 项不做清单，并给出 EXPLAIN 验收断言与"先开清理任务更划算"的前置判断。待用户确认后可进入落地阶段。
