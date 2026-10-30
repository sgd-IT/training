---
title: "user_type 为登录平台位掩码及三处登录门禁的结构约束"
usage_scenario:
    - "修改或排查多端登录准入逻辑"
    - "用户管理维护账号平台归属"
    - "执行 user_type 存量数据迁移前核对口径"
keywords:
    - "user_type位掩码"
    - "登录平台门禁"
    - "checkLoginPlatform"
    - "双端账号"
    - "UserTypeEnum撞名"
source: "auto"
---

system_users.user_type 是账号固定的**登录平台位掩码**（tinyint）：1=PC 端、2=移动端、3=双端（1|2 按位或），NULL 一律按 PC 端兜底；双端目前仅超级管理员（admin, id=1）配置，属数据口径而非代码硬门禁。

结构性约束：
- 判定落点在 AdminAuthServiceImpl 的私有方法 checkLoginPlatform(userType, bit, errorCode)，配合常量 PLATFORM_BIT_PC/MOBILE 做位与运算；login、smsLogin、socialLogin 查 PC 位，mobileLogin 在 TenantUtils.execute 内查移动位。
- 时序不变量：平台校验必须放在 authenticate()（密码校验）**之后**，否则等于向猜密码者泄露账号的平台归属。
- 该字段不得再被登录流程翻写：历史上 login/smsLogin/mobileLogin 成功后各有一次 updateUserType(id, 1/2/3) 把它当"最后登录渠道"覆盖，已全部删除，AdminUserService/Impl 的 updateUserType 也已移除；登录渠道追溯由登录日志承担，不落在用户表。
- 概念撞名：框架 UserTypeEnum（ADMIN=1/MEMBER=2，用于 WebFrameworkUtils 前缀判定与数据权限规则）与本字段取值区间重叠但语义完全无关；前端列表也不要用 DICT_TYPE.USER_TYPE 字典渲染本字段。
- 透出链路：UserSaveReqVO 有 userType 且 createUser/updateUser 走 BeanUtils.toBean 自动写入（表单可改平台）；UserRespVO 早期无该字段导致分页接口拿不到值，现已补齐，前端 UserForm.vue 用双勾选（回显按位与、提交按位或）、user/index.vue 用平台列。
- 迁移禁忌：老数据里的 3 含义是"Wx 渠道标记"，新语义下 3=双端；发版必须与存量订正同批，且旧后端仍在运行时登录会把值翻写回去，须先重启新后端再跑迁移。

## Notes
- 后续会话中用户提出"给 userType 类型为 2 和 3 的用户开发"的新需求，指向的是本字段位掩码语义下的"移动端(2)"与"双端(3)"人群。依赖本条口径前应确认该需求是否仍沿用当前 1/2/3 定义，若产品重新划分取值含义，则以最新需求为准并更新本条。
