---
title: "作业票删除同步平台与删除时updateTime变更改造"
usage_scenario:
    - "改动作业票删除或平台删除同步链路"
    - "排查删除后平台仍显示活跃票"
    - "评估历史脏数据对推送校验的影响"
keywords:
    - "作业票删除同步"
    - "deleteMark=1推送"
    - "updateTime变更"
    - "startTicket重载"
    - "补推过滤已删票"
source: "auto"
---

## Task description
- Core requirement: 确认作业票（base_work_special）的删除标记是否会变更并同步推送给省厅平台，并按用户追加要求让删除时 updateTime 一并变更
- Task background: 平台 OpenAPI `/openapi/work/saveWorkSpecial` 文档规定 deleteMark 必填且"同步的数据不可物理删除，如需删除，标志记为1"，专门的删除接口 `/openapi/work/deleteWorkSpecial/` 已在 OpenApiConstant 中被注释弃用；此前删除流程只做本地逻辑删除、完全不推送平台，平台侧永远保留活跃状态

## Execution process
1. 代码取证确认现状：BaseWorkSpecialController.remove → deleteBaseWorkSpecialByOuterIds 仅置 deleteMark=1 并物理清理视频/操作证子表，全程无 api.startTicket 调用；批量补推 SQL 过滤 deleteMark=0，Android 端无删除入口
2. 数据佐证（tgr-gz-vue）：deleteMark=0 共 20 条、deleteMark=1 共 61 条，全部 sync_status='0'/'2'，无任何"删除后被推送"记录
3. BaseWorkSpecialMapper.xml：两个删除语句统一改为 `set deleteMark = 1, updateTime = now()`（单条与批量都覆盖）
4. PlatementTicketNewApi.java：startTicket 拆为 `(entity)` 与 `(entity, boolean validate)` 重载，原方法委托 validate=true，save/start/end/补推四条既有路径行为不变；删除推送传 false 跳过 BaseWorkSpecialValidator 的 93 项预校验
5. BaseWorkSpecialServiceImpl.deleteBaseWorkSpecialByOuterIds 重写并补上原先缺失的 @Transactional：先逻辑删除 → 逐条查出实体（含操作证、operatorId 转定位卡号）→ startTicket(entity,false) 推送 deleteMark=1 → 按返回码写 sync_status(1/2) → 最后清理视频与操作证子表；日志标签 `[推送作业票-删除]`
6. 验证：mvn -q -pl ruoyi-system -am compile -DskipTests 通过（EXIT_CODE=0，class 重新生成）；IDE GetProblems 因缺 lint 插件不可用

## Related files
- /ruoyi-system/src/main/resources/mapper/base/BaseWorkSpecialMapper.xml
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementTicketNewApi.java
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java

## Notes
- SearchReplace 首次匹配失败：Mapper XML 中两条 update 语句缩进不一致（一条有 4 空格缩进、一条顶格），改用不含行首空白的短 SQL 片段匹配后成功
- 关键实测发现：活跃票 20 条中 13 条 workStatus='1'（已验收）却缺 checkTime、另有 2 条缺 updateTime——若删除推送走完整 93 项校验，这些历史票将永远删不掉，故必须跳过本地预校验交由平台判定
- sendPost 对网络异常会抛 Hutool HttpException（execute() 未包 try/catch），非 JSON 响应返回 code=-1；因此删除推送遇网络故障会随事务整体回滚，与 save/start/end 口径一致
- MCP anwei_fire_remote_monitor_test 会话中途报 'not found MCPHost severName' 两次，改走 mysql.exe 兜底完成只读核对
- 遗留未做：61 条历史已删票不回推；删除推送失败的票无自动重试通道（补推仍过滤 deleteMark=0）；无调用方的单条删除方法保持原样

## Task overview
已完成：删除动作现在会同时把 deleteMark 置 1、updateTime 更新为删除时刻，并把带 deleteMark=1 的完整实体推送给平台，sync_status 留痕；编译验证通过，尚未在环境里实删联调（建议 Web 端用 base:workSpecial:remove 权限删一条看日志与平台侧变化）。

## 修正记录（2026-10-09）
- 上述执行过程第 5 步中"operatorId 转定位卡号"的描述已失效：2026-10-09 经部署包字节码与平台实推结果核实，平台按人员定位系统的人员ID（sync_id 原值 UUID）校验作业人员编码，转成 position_card_no 会被拒"作业人员编码错误"。该转换调用已从 deleteBaseWorkSpecialByOuterIds（及 saveWorkSpecial、rePushDeletedBatchToPlatform）移除，convertOperatorIdToCardNo 标记 @Deprecated 停用。本条其余各步结论仍然有效。
- "遗留未做"中的两项已被后续任务补齐：新增 POST /push/deletedWorkSpecial 可按删除时间范围补推已删票（默认 onlyUnsynced=true）；删除推送失败不再无重试通道。
