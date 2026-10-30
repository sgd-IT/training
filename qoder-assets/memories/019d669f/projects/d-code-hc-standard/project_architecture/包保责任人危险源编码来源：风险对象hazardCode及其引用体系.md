---
title: "包保责任人危险源编码来源：风险对象hazardCode及其引用体系"
usage_scenario:
    - "填充或校验包保责任人危险源编码"
    - "开发危险源编码选择相关功能"
    - "排查风险对象编码重复同步失败"
keywords:
    - "hazardCode 风险对象"
    - "base_risk_obj"
    - "危险源编码来源"
    - "riskDevice 推送"
    - "包保责任人 sourceId"
source: "auto"
---

危险源编码（12 位纯数字装置编码，如 420510077002）的权威数据源是风险对象档案 base_risk_obj.hazardCode（实体 BaseRiskObj，PC 菜单"风险对象" menu_id 2605，与"风险单元"2472、"停机记录"2633 同父目录 2327），该档案由 PlatementRiskDeviceApi 推送到园区 riskDevice 接口；风险单元 base_risk_unit 的 hazard_code/risk_obj_id 也引用同一套编码体系。包保责任人 platement_implement_person.sourceId（对应园区 5.3.7.1 implementPerson 接口的危险源编码）应填该编码，测试库现有 4 条数据均为 420510077002（液氯储罐气化仓库）。

反直觉点：同样是"选风险对象"，两个模块存的字段不一样——风险单元页面表单的"对象编号"下拉 label 为装置名称、value 为风险对象 id（存 risk_obj_id），而包保责任人存的是 hazardCode 字符串本身；跨模块套用选择逻辑会导致落库字段错位。

前端包保责任人新增/修改对话框已将该字段改为下拉框：复用 listRiskobj({pageNum:1,pageSize:1000})（selectBaseRiskObjList 已过滤 deleted=0）加载，label 为"编码（装置名称）"、value 为 hazardCode，展开时（@visible-change）加载并做回显保护（编码不在列表中时补裸编码兜底项）。注意：加载接口需 base:riskobj:list 权限。

数据质量坑：hazardCode 在本地表可重复但园区侧要求唯一，测试库中 420510077703 被"对羟基本甲腈生产装置"与"污水预处理平台"重复使用，导致相关风险对象同步园区失败；排查此类同步失败时直接看 base_risk_obj.sync_result（会写明"hazardCode:危险源编码重复：xxx"），选择编码时应避开重复值。另：该库列名风格不统一，base_risk_obj 用驼峰列（hazardCode/deviceName）而 base_risk_unit 用下划线列（hazard_code/risk_obj_id），按实体字段名猜列名会报 Unknown column。
