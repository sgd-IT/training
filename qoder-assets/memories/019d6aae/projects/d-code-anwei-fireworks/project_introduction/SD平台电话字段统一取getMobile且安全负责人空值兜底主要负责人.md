---
title: "SD平台电话字段统一取getMobile且安全负责人空值兜底主要负责人"
usage_scenario:
    - "对接SD平台时保证电话字段取值一致"
    - "安全负责人数据库值为空时的兜底处理"
    - "新增同类人员字段时复用统一取值方法"
keywords:
    - "getMobile"
    - "principalPersonPhone"
    - "safetyPersonPhone"
    - "getPersonInCharge兜底"
    - "SD平台字段校验"
source: "auto"
---

项目中所有电话字段（包括 principalPersonPhone 和 safetyPersonPhone）均统一调用 getMobile() 方法获取；安全负责人（safetyPerson）字段在数据库值为空时，自动兜底使用 getPersonInCharge() 方法返回的主要负责人信息。
