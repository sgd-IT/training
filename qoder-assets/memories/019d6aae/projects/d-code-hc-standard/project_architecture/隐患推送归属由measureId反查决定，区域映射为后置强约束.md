---
title: "隐患推送归属由measureId反查决定，区域映射为后置强约束"
usage_scenario:
    - "园区平台反馈隐患装置或危险源归属错误而本地看似正常"
    - "设计隐患录入的区域单元措施联动校验"
    - "需要重推历史隐患却找不到前端补推入口"
keywords:
    - "隐患归属"
    - "管控措施"
    - "measure反查"
    - "hazardCode"
    - "base_area_risk_unit"
    - "push/hiddenTrouble"
    - "sync_status不自动推"
source: "auto"
---

向园区平台推送隐患时，最终展示的"装置""单元""危险源编码（hazardCode）"由管控措施（base_risk_measure）的关联关系 measure → event → unit → obj 反查唯一确定，取的是**所选措施的归属**，而不是隐患录入时填写的物理"隐患区域（base_area）"。因此本地详情页只展示隐患区域、不展示措施与单元，会出现"本地看着正常、园区平台挂到别的装置（甚至重大危险源）"的现象——排查此类问题必须去查 measureId 指向哪个单元，而不是看区域。

补充约束（改造后新增）：项目已引入 base_area_risk_unit（区域→装置单元映射）表与 /api/hidden/getAreaRiskUnits 接口，移动端按"先选区域→自动带出并锁定装置单元→措施列表按单元过滤"的顺序约束选择；后端在**该区域已配置映射时**才对 area→unit→measure 做一致性强校验，未配置映射的区域回退全量单元、不做拦截。所以"区域与推送归属完全无关"只对存量数据与未配置映射的区域成立，新录入在配好映射后受区域约束。

重推入口事实：隐患没有任何定时同步任务（DataSyncTask 不含隐患），sync_status 只是标记字段、改库不会触发推送。推送仅在业务动作发生时发生（新增 businessState=0、申请关闭=1、关闭驳回=2、确认关闭=4），普通修改 updateBaseHiddenTrouble 不推送。历史数据修正只能手动调 POST /push/hiddenTrouble（只捡 status=31 且 sync_status != '1' 的已关闭隐患，固定推 businessState=4）。该接口在前端菜单里**没有对应页面**，只能通过 Swagger /doc.html（Knife4j，需 Authorize 填 Authorization 头）或 Postman 调用。

## Notes
- 补充（2026-10-22 读码核实）：主表推送走 buildUploadEntity 时存在大量**硬编码翻译**，是园区侧与本地字段值对不上的直接原因：dangerManageType 恒为 1（限期整改）、dangerSrc 恒为 1、handleSource 恒 "1"、hazardCategory 恒 "0"、经纬度固定 "1"；且 registTime=本地 findTime、registrant=本地 findUserName、dangerManageDeadline=本地 requirementTime、dangerName=本地 areaName。比对平台与本地字段差异时须先扣除这层投影。
- 补充：base_hidden_trouble_upload 与 base_hidden_trouble 两张表在 SQL 层零 JOIN、零同步（selectHiddenTroubleList 只 from base_hidden_trouble），任一表新增都不会在另一表生成记录，字段命名口径也完全不同（登记人/登记时间/治理期限 vs 发现人/发现时间/落实要求时间）。"两个菜单看到的隐患数据不一样"属结构事实，不是同步故障。
