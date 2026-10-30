---
title: "APP账号user_type准入与curOperatorId决定按钮显示"
usage_scenario:
    - "为APP功能验收选取可登录测试账号"
    - "APP页面只读无按钮的排查"
    - "构造作业票数据避免详情tab为空"
keywords:
    - "curOperatorId本人不掩码"
    - "user_type 01准入APP"
    - "sys_user_role空角色"
    - "base_app_menu无过滤"
    - "克隆配套子表"
source: "auto"
---

APP 端作业票详情接口（/api/special/getWorkSpecial → BaseWorkSpecialServiceImpl.selectBaseWorkSpecialByOuterId）存在权限掩码：当查看者既非该票 curOperatorId 本人、又非管理员时，workStatus 被强制置为 "1"（已验收），APP 详情页将隐藏全部操作按钮（含气体分析录入/修改入口）。

APP 账号体系的实测定性规则：sys_user.user_type 是端侧准入开关——appPwdLogin/appLogin 仅放行 user_type='01'（APP账号），admin/system/system1 等内置后台账号为 '00'，在 APP 上直接报"账号不存在"，因此 PC 管理员永远无法用于 APP 测试；反之所有 '01' 业务账号（实测 sss/余威/高北秀等）在 sys_user_role 中未配任何角色。SysUserMapper.xml 的 selectUserVo 确实 left join sys_user_role + sys_role，即 roles 集合会被正常加载（不是 null），APP 会话不掩码只是因为角色为空而非未加载。故 isAdminUser()（超管判定 userId==1 或 role_key=='admin'）在 APP 会话恒 false。结论：APP 端不掩码的唯一可行路径是"测试票 curOperatorId = APP 登录账号的 user_id"；若需测"管理员视角"（非本人也可操作，对应需求54用例），须给指定 '01' 账号在 sys_user_role 配置 role_key='admin' 的角色。

另一项无约束事实：APP 首页菜单接口（ApiBaseServiceImpl.selectMenuList → ApiBaseMapper.selectMenuList）只按 menuType 查 base_app_menu（status=0），不带任何用户/角色过滤，所以任何 '01' 账号登录后都能见到"动火作业"等入口，选择测试账号时只需对齐 curOperatorId，不必担心菜单可见性。

另两个造数约束：(1) area_id 必须存在于 base_area，否则详情接口抛"作业区域不存在"；(2) 克隆 base_work_special 造数时应连带克隆 base_work_approve / base_work_step / base_work_sensor / base_work_video（主键均为 varchar，用 REPLACE(UUID(),'-','') 生成新主键），否则详情页各 tab 为空。
