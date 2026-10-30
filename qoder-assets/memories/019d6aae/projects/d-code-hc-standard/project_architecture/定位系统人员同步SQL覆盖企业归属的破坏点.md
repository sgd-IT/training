---
title: "定位系统人员同步SQL覆盖企业归属的破坏点"
usage_scenario:
    - "修复外施队人员被挪到源洹名下的问题"
    - "改造人员定位同步回填逻辑时"
    - "排查承包商企业名下无人员的成因"
keywords:
    - "syncContractorPeople覆盖归属"
    - "updateBaseContractorPeopleByPhone"
    - "contractor_id硬编码1001"
    - "clearPositionCard清卡"
    - "卡号形态与身份无关"
source: "auto"
---

BaseContractorPeopleServiceImpl.syncContractorPeople() 从外包定位系统 getTagInfo 拉人员后按手机号回写本地库，其 SQL updateBaseContractorPeopleByPhone（BaseContractorPeopleMapper.xml）存在三个隐式破坏点，任何涉及外施队/承包商人员归属的改造都必须先处理：

1. SET 子句包含 contractor_id = #{contractorId}，而构造实体时硬编码 contractorId=1001L、status=3，并把定位系统的 worktype 写入 id_card_no；只要手机号命中已有记录，该人员就被改挂到湖北源洹实业（1001）名下并跳过审核态。这是库内人员集中于 1001 的直接成因。
2. WHERE 条件仅有 phone = #{phone}，缺 del_flag='0'，逻辑删除的人员也会被改写。
3. 未命中手机号时执行 insert，同样落到 contractor_id=1001，导致外部施工队人员首次同步即归属错误；配套 clearPositionCard() 会把范围内人员的 position_card_no 整体清空再回填，作用范围过宽。

定位系统侧的组织结构只有"湖北源洹"一棵树，承包商仅是树下的一个部门分组（实测有 7 张卡、当前 0 在线），不存在企业维度；因此企业与内外属性只能由本系统 base_contractor.company_type（1=内部、2=承包商）承载，同步逻辑不得反向覆盖该归属。

卡号形态与"内部/外施队"身份无关（2026-10-23 线上 base_contractor_people 136 人实测：position_card_no 为 11 位手机号 95 人、12 位 mac 37 人、空 4 人，且这 136 人全部挂在 contractor_id=1001 内部企业下；承包商三家企业名下 0 人）。LocationTask 两侧均按 position.mac 与 position_card_no 做纯字符串相等匹配，两种形态走同一条路，不需要做任何形态判断或转换；真正的断点只在定位系统侧记录的 phone 是否为空——为空则按 phone 回填永远匹配不到，只能靠 PC 端手工录卡号。
