---
title: "移动端选作业人查证件的syncId传参链路与null丢参陷阱"
usage_scenario:
    - "改动作业票选人或证件查询接口"
    - "排查选作业人无证书弹窗弹出或加不了证件"
    - "核对移动端与后端字段契约一致性"
keywords:
    - "getWorkPeopleCertAll"
    - "syncId必填契约"
    - "Retrofit null丢参"
    - "selectZs弹窗时序"
    - "getWorkPeopleList"
source: "auto"
---

移动端特殊作业票"选择作业人→查看/添加证件"链路契约：各 Fragment(FragmentHotWork/FragmentElectricity 等8类)的 searchPeople 调用 /api/contractor/getWorkPeopleList，把返回 ContractorPeopleBean.syncId 填入 MultiSelectBean.id；用户点选某人后 selectZs 以该 id 作为 @Query("syncId") 调 /api/contractor/getWorkPeopleCertAll(后端 @RequestParam String syncId 必填)。关键不变量：Retrofit 对值为 null 的 @Query 参数会静默丢弃不发往服务端，导致后端抛 MissingServletRequestParameterException 返回500；且 selectZs 仅在接口 success 后才 show() 证书弹窗，故 syncId 缺失时表现为"选不上作业人、加不了证件"。因此 getWorkPeopleList 必须保证 syncId 非空——其 SQL 列标签须与 resultMap 显式映射列名一致(见 MyBatis 别名失配陷阱)。
