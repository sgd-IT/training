---
title: "作业票推送operatorId必须传人员定位UUID原值"
usage_scenario:
    - "新增或修改作业票平台推送路径时"
    - "排查平台返回作业人员编码错误时"
    - "核对推送字段取值口径与影响面"
keywords:
    - "operatorId推送取值"
    - "sync_id原值禁转卡号"
    - "作业人员编码错误"
    - "outid与sync_id两代混合"
    - "startTicket上游赋值链"
    - "convertOperatorIdToCardNo废弃"
source: "auto"
---

作业票（base_work_special）向省厅平台的同步是状态触发式结构，其 operatorId 字段必须传 base_contractor_people.sync_id 原值，禁止转换为 position_card_no 发卡号。平台校验"作业人员编码"依据的是园区人员定位基础信息表中的人员ID；发卡号（手机号形态）匹配不到人员，返回"作业人员编码错误"。该结论经2026-10-09删除补推实测（57条中16条被拒）、部署包字节码验证（getOperatorId→convertOperatorIdToCardNo→setOperatorId→startTicket调用链）、以及LD-/DH-系列新票持续成功三重证据链证实。

## Notes
- 本条原文把 sync_id 描述为"UUID 形态"不准确，2026-10-23 实查 tgr-gz-vue 库更正：sync_id 存在两代混合值——历史内部人员（contractor_id=1001 名下 149 人）为 19 位雪花 ID，另有部分人员 sync_id 等于 outid（如测试外施队人员 id=960）；而 outid 列恒为 UUID。LocationTask 上报园区的 personId 取的是 outid，不是 sync_id。
- 因此本条的正确口径是"传 sync_id 原值、推送路径不做任何转换"，而非"sync_id 就是 UUID"。为新录入人员设计 operatorId 时，必须先确认其在园区人员定位基础信息表中的实际人员ID，不能假定 sync_id 与 outid 同值，否则会出现同一人两个通道不同源的匹配失败。
