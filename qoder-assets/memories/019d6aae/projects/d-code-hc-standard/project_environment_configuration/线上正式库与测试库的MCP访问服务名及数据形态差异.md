---
title: "线上正式库与测试库的MCP访问服务名及数据形态差异"
usage_scenario:
    - "需要查询线上正式库数据时选择MCP服务名"
    - "下结论前确认查询落点是线上还是测试库"
    - "评估人员标识形态跨环境是否可互推"
keywords:
    - "220.203.23.144"
    - "anwei1-0001"
    - "hc-standard-test"
    - "sync_id等于outid"
    - "@@hostname验落点"
    - "practitioners.sync启用"
source: "auto"
---

本项目两套 MySQL 环境的实测访问口径（2026-10-23 核验）：
- 线上正式库：地址 220.203.23.144:3306/tgr-gz-vue，@@hostname=anwei1-0001。可用 MCP serverName 为 tgr-gz-vue（工具 mysql_query），其定义位于用户级配置 %APPDATA%\Qoder\SharedClientCache\extension\local\mcp.json；该服务未出现在项目 mcps 缓存目录列表中，但已启用时仍可直接调用 —— 不能以缓存目录是否列出判断服务可用性。
- 开发/测试库：MCP serverName 为 hc-standard-test，连 192.168.250.160，@@hostname=WINDOWS-3D9R3V5，默认库名同样是 tgr-gz-vue。两个服务返回的 DATABASE() 同名，仅凭库名无法区分环境，必须 SELECT @@hostname 或比对业务表计数确认落点。
- 两库人员标识形态不同且不可互推：线上 base_contractor_people 中 1001（湖北源洹实业）名下 136 人的 sync_id 与 outid 完全相等（均为 UUID）；测试库同表人员 sync_id 为 19 位雪花且 sync_id≠outid。据此，任何关于 operatorId / personId 可用性的结论都必须先切到目标库重取，沿用另一库的取值形态会得出相反判断。
- 定时任务状态也不同：线上 sys_job 中 practitioners.sync 与 locationTask.getLocation 均为每 10 秒、status=0（启用），新录入人员约 10 秒即被真实推送到园区从业人员库；测试库对应任务为停用状态。
