---
title: "作业票列表接口过滤口径与MySQL元数据查询可用写法"
usage_scenario:
    - "向测试库插入作业票后确认可见性"
    - "克隆base_work_special及其子表造数"
    - "编写涉及sys_user手机号的SQL"
keywords:
    - "getWorkSpecialList"
    - "ticketsType过滤"
    - "deleteMark过滤"
    - "varchar主键无自增"
    - "sys_user无phonenumber列"
    - "user_name即手机号"
    - "mobile_me列"
source: "auto"
---

APP 端特殊作业票列表接口 /api/special/getWorkSpecialList（ApiSpecialController → BaseWorkSpecialServiceImpl.selectBaseWorkSpecialList → BaseWorkSpecialMapper）的查询口径为：只按 ticketsType 等值过滤 + deleteMark = 0，按 createTime 倒序分页，不按登录用户、不按 curOperatorId 过滤。因此手工插入的 base_work_special 记录只要 ticketsType 正确且 deleteMark='0'，就会出现在对应类型列表的最前面，无需为"谁能看到"建立额外关联关系。

配套的主键形态（列结构实测确认）：base_work_special / base_work_sensor / base_work_approve / base_work_step 的主键均为 varchar(64) 且无自增列，克隆造数时需自行生成新主键值。

关于 sys_user 的手机号相关列（2026-09-23 于 220 正式库实测更正）：本项目 sys_user 既没有 phonenumber 也没有 phone_number 列（两者 SELECT 均报 Unknown column），按手机号定位账号要用 user_name（登录账号即手机号）；排查园区上报 IMEI 问题时用的列是 mobile_me。写这类查询前建议先用 information_schema.columns 确认列名，不要凭 RuoYi 默认 schema 猜列。
