---
title: "风险单元园区接口能力与删除链路（含批量DELETE传Body约定）"
usage_scenario:
    - "判断风险单元删除是否需上报园区平台"
    - "实现批量DELETE带JSON请求体的外呼"
    - "核对园区对接删除接口的封装与调用方"
keywords:
    - "base_risk_unit"
    - "risk_unit/batch删除接口"
    - "PlatementRiskUnitApi"
    - "sendDelete签名"
    - "array string请求体"
    - "先推平台后本地软删"
source: "auto"
---

hc-standard 中风险单元（base_risk_unit）对园区平台的三个接口均已封装且可用（OpenApiConstant.ShuangChongYuFang.Unit）：

- 批量新增或修改：POST /openapi/risk_unit/saveOrUpdateBatch → PlatementRiskUnitApi.save()
- 批量删除：DELETE /openapi/risk_unit/batch → PlatementRiskUnitApi.delete(String[] ids)，内部 Arrays.asList(ids) 后 JSONUtil.toJsonStr(list)，即 Body 为 application/json 的 array[string]（形如 ["id1","id2"]），走 PlatementBaseApi.sendDelete(uri, body)，带 KS-APPKEY / KS-TIMESTAMP / KS-SIGN 签名头，签名串为 uri#DELETE#body#timestamp#appkey。与平台文档 5.4.3.2 的参数定义一致（文档标注 body 可选，代码始终传 ids）。

调用链（PC 端删除风险单元即走此接口）：BaseRiskUnitController.remove（权限 base:riskUnit:remove，DELETE /base/riskUnit/{ids}）→ BaseRiskUnitServiceImpl.deleteBaseRiskUnitByIds() → ① 先 api.delete(ids) 推平台；② 平台返回 code==500 则抛 ServiceException，本地不删；③ 成功后本地软删 update base_risk_unit set sync_result=?, sync_status=1, deleted=1。

顺序特征：与"服务记录先本地软删再同步平台、失败恢复"相反，风险单元是**先推平台、成功才落本地**，平台故障会阻塞本地删除。二者不可互相套用推断。

触发频率提示：220 正式库 base_risk_unit 仅 14 条且最新创建停在 2026-07-18 初始化批次，故该删除接口日常几乎不被触发。
