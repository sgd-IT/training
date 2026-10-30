---
title: "API访问日志使用统计页面（仅超管）全链路落地与双仓提交"
usage_scenario:
    - "基于访问日志做调用量统计或埋点类页面"
    - "新增仅超级管理员可见的菜单与接口"
    - "回顾使用统计页数据口径与已知局限"
keywords:
    - "API使用统计"
    - "埋点页面"
    - "super_admin菜单机制"
    - "access_log聚合"
    - "system_menu插菜单"
    - "冷门接口排行"
source: "auto"
---

## Task description
- Core requirement: 新建一个展示"埋点数据"（接口使用次数、哪个入口频繁）的页面，且只有超级管理员可查看；后续追加"调用最少的接口也要展示"。
- Task background: 平台无前端埋点 SDK，此前已盘点确认复用 infra_api_access_log（ApiAccessLogFilter 全局写入）；现有 API 日志页面只有明细列表没有聚合统计；yudao 菜单由 system_menu 数据库驱动。

## Execution process
1. 权限双层收敛判定：后端新接口用 @PreAuthorize("@ss.hasRole('super_admin')")；菜单层靠 PermissionServiceImpl 的结构事实——账号拥有 super_admin 角色时 getRoleMenuListByRoleId 直接返回全部菜单，新菜单插入 system_menu 后超管自动可见，其他角色无需逐租户配 system_role_menu。
2. 后端（module-infra-biz）新增 4 个文件 + 改 1 个：ApiAccessLogStatisticsRespVO（summary/trend/urlRank/urlRankBottom/userRank 内嵌结构）；ApiAccessLogMapper 增加注解式聚合 SQL（COUNT/DISTINCT/DATE(create_time) GROUP BY，排行 ORDER BY callCount DESC/ASC 两组）；ApiAccessLogStatisticsService+Impl（默认近 7 天、top 上限 100、AdminUserApi.getUserMap 批量补昵称、@TenantIgnore 保证超管看全租户数据、日均按自然天数最少算 1 天防除零）；ApiAccessLogStatisticsController（GET /admin-api/infra/api-access-log/statistics）。
3. 用户排行 SQL 补 AND user_id IS NOT NULL，避免免登录接口的 null 分组产生"用户#null"行。
4. 前端：src/api/infra/apiAccessLog/index.ts 加类型封装；新建 usageStats.vue（时间范围筛选补足当天首尾时间、4 个汇总卡、每日趋势折线复用 data-center/useChart.ts 的 renderChart、接口排行卡 el-radio-group 本地切换最多/最少两套数据、底部标注统计口径）。
5. 菜单落库：MCP 查 system_menu 结构（id AUTO_INCREMENT，parent 1083「API 日志」下兄弟 sort 1/2），INSERT 得 id=3232（path=usage-stats，component=infra/apiAccessLog/usageStats，icon ep:data-analysis）；最终位置为 基础设施(/infra) → API 日志(log) → 使用统计，URL /infra/log/usage-stats。
6. 验证：mvn -pl module-infra/module-infra-biz -am compile exit=0；GetProblems 语言服务未就绪时改用 node node_modules/eslint/bin/eslint.js 直调（pnpm exec 会触发依赖检查失败）；MCP 实测聚合 SQL 近 7 天 18046 次调用/95 接口/300~500ms。
7. 提交：先按 code-comment-style 输出注释检查报告，展示清单获用户确认后分仓提交——后端 51db7c2（5 Java + 幂等菜单 SQL V20261019__menu_usage_stats.sql，按 parent_id+path 判重）、前端 b93075d，提交信息经 Node Buffer UTF-8 字节校验；停在本地未 push。
8. 发现并上报异常：工作区 ApiAccessLogDO.java 存在疑似误触改动 @KeySequence("dinfra_api_access_log_seq")（多一个 d），MySQL 下不生效，未纳入提交并向用户确认是否还原。

## Related files
- anwei-fire-remote-monitor/module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/controller/admin/logger/ApiAccessLogStatisticsController.java
- anwei-fire-remote-monitor/module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/controller/admin/logger/vo/apiaccesslog/ApiAccessLogStatisticsRespVO.java
- anwei-fire-remote-monitor/module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/dal/mysql/logger/ApiAccessLogMapper.java
- anwei-fire-remote-monitor/module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/service/logger/ApiAccessLogStatisticsService.java
- anwei-fire-remote-monitor/module-infra/module-infra-biz/src/main/java/com/enjoyiot/module/infra/service/logger/ApiAccessLogStatisticsServiceImpl.java
- anwei-fire-remote-monitor/_sql/mysql/V20261019__menu_usage_stats.sql
- anwei-fire-remote-monitor-vue/src/api/infra/apiAccessLog/index.ts
- anwei-fire-remote-monitor-vue/src/views/infra/apiAccessLog/usageStats.vue

## Notes
- 数据口径边界：①"调用最少"最低只能到 1 次，0 调用接口在日志里没有记录，找"完全没人用"需拿接口清单做差集；②带路径参数的 URL（/eiot/device/113/function）按不同 ID 拆行，最少排行会碎，归一化（数字段→{id}）留作后续；③大屏轮询接口（recentUnprocessedAlerts 7751 次、dashboard/statistics 4893 次）会霸占最多排行，解读时需背景说明。
- AccessLogCleanJob 名义保留 14 天，但实测库里存有约 5 个月 107 万条日志，说明清理任务实际未在跑——统计窗口比预期长，同时也意味着该结论未来可能变化。
- 菜单直接插的是测试库，生产环境需另行执行仓库内的幂等 SQL 文件；超管要重新登录才能看到新菜单（菜单有缓存）。
- 统计口径按 create_time 走索引过滤，不要改用 begin_time。

## Task overview
已完成：仅超管可见可用的 API 使用统计页全链路落地（后端聚合接口 + 前端页面 + 数据库菜单 + 幂等 SQL 入库），后端编译、ESLint、SQL 实测均通过，双仓各一笔 Conventional Commits 提交（51db7c2 / b93075d）停在本地未推送；冷门接口（最少排行）切换功能为最新一轮改动尚未提交；ApiAccessLogDO 疑似误触改动待用户决定还原。

## Notes

- 【状态更新】上述"停在本地未推送""最少排行未提交""DO 误改待还原"三点均已作废。后续会话中：①"调用最少排行"增量改动已分仓提交并推送——后端 1306876 `feat(infra): 使用统计新增接口调用最少排行`（ApiAccessLogMapper.java、ApiAccessLogStatisticsRespVO.java、ApiAccessLogStatisticsServiceImpl.java，3 files +19），推送 ed8108b..1306876；前端 4e80b7d `feat(infra): 使用统计页支持切换调用最多/最少排行`（api/infra/apiAccessLog/index.ts、views/infra/apiAccessLog/usageStats.vue，2 files +18/-2），推送 9ffa7a2..4e80b7d；两仓库 fetch 后均为纯 ahead 无 behind，push 后 status -sb 与 origin/master 同步。②ApiAccessLogDO.java 的 @KeySequence 已恢复为正确值 infra_api_access_log_seq（Grep 复核确认），不再是待处理项。③工作区剩余仅两个 yaml 配置与 docs 两篇文档，按约定继续排除在提交外。
