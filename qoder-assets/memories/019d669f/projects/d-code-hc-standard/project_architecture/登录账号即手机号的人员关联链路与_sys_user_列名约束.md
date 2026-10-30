---
title: "登录账号即手机号的人员关联链路与 sys_user 列名约束"
usage_scenario:
    - "实现按本人判定权限的业务接口"
    - "排查本人可见或可编辑逻辑失效问题"
    - "按手机号定位账号或删除账号被拦截时排查"
keywords:
    - "user_name即手机号"
    - "账号与人员关联"
    - "create_by空值陷阱"
    - "SecurityUtils.getUsername"
    - "phonenumber列不存在"
    - "curOperatorId删账号拦截"
source: "auto"
---

hc-standard（RuoYi 版）系统中"当前登录用户 ↔ 业务人员实体"没有 user_id 外键，权威关联是手机号字符串等值匹配：sys_user.user_name 存放的就是手机号（admin/system 等少数内置账号除外），base_contractor_people.phone 存人员手机号，二者直接相等即为同一人。开发库实测：149 名承包商人员中 122 名的 phone 能对上 sys_user.user_name；base_contractor_people_cert 无人员账号字段、其 create_by 历史数据全为空串（27/27 为空），因此任何按 create_by 判定归属的逻辑对该表都会把所有人挡在外面，不可使用。子表要拿人员手机号必须在 mapper 的 VO 里 LEFT JOIN base_contractor_people 增取 phone 并在 resultMap 加映射（证书表仅存 people_id）。代码内比对账号的既有写法是 SecurityUtils.getUsername()（见 BaseTicketYbServiceImpl 的"非本人创建数据，不允许删除"），管理员判定用 PermissionService.hasRole("admin")。

## sys_user 列名与账号关联的实测约束
- 该表列名为 user_name / nick_name / dept_id / status / create_time，写成 username 会报错；phonenumber 列在本项目 schema 中不存在（实测 SELECT u.phonenumber 报 Unknown column 'u.phonenumber'），按手机号定位账号必须用 user_name = '<手机号>'，不可再猜 phonenumber。
- 同一手机号可能在多个环境库各自存在不同的 user_id（实测开发库 160 与测试库 220 各有同号但昵称/ID 不同的账号），跨库结论前必须先锁定实例再取值。
- SysUserServiceImpl.deleteUserByIds() 删账号前有两道业务拦截：baseWorkSpecialMapper.selectCheckUserIdCount(userId)（SQL: base_work_special 中 deleteMark=0 AND curOperatorId=#{userId} 计数）与 baseRiskTaskMapper 同类校验，命中即抛"该用户存在作业票，不允许删除"；curOperatorId 存的是 sys_user.user_id（数值），不是手机号字符串。

## Notes
- 【2026-09-28 更正】正文"phonenumber 列不存在"的结论需补充：在 160 测试库 SHOW COLUMNS FROM sys_user 实测该表存在定制列 phone_number（带下划线），并非 RuoYi 标准的 phonenumber——查询时写 phonenumber 报 Unknown column，写 phone_number 可正常取到手机号。按手机号定位账号仍首推 user_name='<手机号>'（user_name 即手机号是本项目的既定约定），但读取手机号展示字段时应使用 phone_number 列。
