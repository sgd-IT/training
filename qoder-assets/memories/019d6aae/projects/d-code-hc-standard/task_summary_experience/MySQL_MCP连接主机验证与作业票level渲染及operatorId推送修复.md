---
title: "MySQL MCP连接主机验证与作业票level渲染及operatorId推送修复"
usage_scenario:
    - "调试临时用电票被平台渲染为特级动火"
    - "改动PlatementTicketNewApi推送前字段剔除逻辑"
    - "核对MCP连接目标与配置指向是否一致"
keywords:
    - "MySQL MCP"
    - "anwei1-0001"
    - "ticketsType=6"
    - "level渲染"
    - "position_card_no"
    - "BaseWorkSpecialValidator"
source: "auto"
---

## Task description
- Core requirement: 核实 MySQL MCP 服务实际连接的数据库实例，并修复临时用电票（ticketsType=6）在园区平台被渲染为"特级动火"的问题
- Task background: application-druid.yml 配置指向 192.168.250.160，但实测 MCP 解析到 anwei1-0001，该差异导致票数统计不一致（79 vs 136）；同时发现 BaseWorkSpecialValidator.java 中子对象校验器全部被注释停用；ticketsType=6 的 level 为 null/"" 导致平台按默认值渲染成特级动火

## Execution process
1. 经 CallMcpTool 确认 tgr-gz-vue 返回 hostname=anwei1-0001，而非配置里的 192.168.250.160
2. 校准 `MySQL MCP 当前仅 hc-standard-test 可用且连测试库的连接现状`（id 8156910a-a933-4ef5-905f-7cff6f937e97）的环境映射口径
3. 校验 base_work_special.sync_status 实际取值为 null/1/2（不是 0/1/2）
4. 定位 ticketsType=6 的 level=null 或 "" 是平台渲染成特级动火的直接原因
5. 定位 BaseWorkSpecialValidator.java 第 331–349 行：validateSensors / validateApproves / validateVideos / validateCertificates 四个子对象校验器均被注释停用
6. 确认 transformOperatorInfo 仅在上传/开工前做字段转换，不校验完整性
7. BaseContractorPeopleMapper 新增 selectBaseContractorPeopleBySyncId，支撑 operatorId 转换
8. PlatementTicketNewApi.java 推送前对 ticketsType=2/6/7/8 剔除 level/levelShow
9. BaseWorkSpecialServiceImpl.java 推送前把 operatorId 由 syncId 转 position_card_no
10. workUnit 改为取 workUnitName，不再硬编码 "0"

## Related files
- d:\code\hc-standard\ruoyi-system\src\main\java\com\ruoyi\base\service\impl\BaseWorkSpecialServiceImpl.java
- d:\code\hc-standard\ruoyi-common\src\main\java\com\ruoyi\common\platement\api\PlatementTicketNewApi.java
- d:\code\hc-standard\ruoyi-system\src\main\java\com\ruoyi\base\mapper\BaseContractorPeopleMapper.java
- d:\code\hc-standard\ruoyi-system\src\main\resources\mapper\base\BaseContractorPeopleMapper.xml
- d:\code\hc-standard\ruoyi-common\src\main\java\com\ruoyi\common\platement\validator\BaseWorkSpecialValidator.java
- C:\Users\Administrator\AppData\Roaming\Qoder\SharedClientCache\projects\d--code-hc-standard\mcps\tgr-gz-vue\SERVER_METADATA.json

## Notes
- @benborla29/mcp-server-mysql 不会自动为 tgr-gz-vue 这类含连字符的库名加反引号，裸 SQL 必须显式用单反引号包裹，不能依赖驱动透明处理
- 四个校验器方法存在但被注释停用，启用后可在保存阶段拦截传感器/证书/视频/点位缺失
- 结论：MCP 实际落点为 anwei1-0001，解释了 79 vs 136 的票数差异；校验器仍处停用状态，数据完整性检查未生效

## 修正记录（2026-10-09）
- 本任务第 9 步"推送前把 operatorId 由 syncId 转 position_card_no"经实测证伪：平台校验的"作业人员编码"为人员定位系统的人员ID（sync_id 原值），转卡号导致平台返回"作业人员编码错误"，任何推送路径都不得再做该转换。
- 证据：删除补推实推 57 条中 16 条被拒"作业人员编码错误"；部署包字节码证实补推循环为 getOperatorId→convertOperatorIdToCardNo→setOperatorId→startTicket；不改值的开工/完工推送路径 2026-07 至今持续成功。
- 处理：2026-10-09 已删除 saveWorkSpecial、deleteBaseWorkSpecialByOuterIds、rePushDeletedBatchToPlatform 三处转换调用，convertOperatorIdToCardNo 标记 @Deprecated 停用（编译通过，待部署验证）。
