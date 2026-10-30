---
title: "多端账号平台分离改造：user_type 位掩码、登录门禁与用户管理平台字段"
usage_scenario:
    - "排查登录被拒或账号平台归属问题"
    - "维护多端账号与用户管理平台字段"
    - "执行存量 user_type 数据迁移前复核口径"
keywords:
    - "user_type位掩码"
    - "登录平台门禁"
    - "双端账号"
    - "登录渠道翻写"
    - "updateUserType删除"
    - "checkLoginPlatform"
source: "auto"
---

## 任务描述
将 system_users.user_type 从"最后登录渠道标记"改为"账号固定平台位掩码"并落地双向登录门禁：1=PC 端、2=移动端、3=双端（1|2 按位或）；双端目前仅超级管理员（admin）配置。以手敲教程模式起头，后经用户授权由 agent 按最小单元执行。

## 执行过程
1. 错误码：ErrorCodeConstants 新增 AUTH_LOGIN_PLATFORM_NOT_MATCH_PC=1_002_000_016（该账号为移动端账号，请使用小程序登录）、AUTH_LOGIN_PLATFORM_NOT_MATCH_MOBILE=1_002_000_017（该账号为 PC 端账号，请使用电脑端登录）。
2. AdminAuthServiceImpl：删除 login/smsLogin/mobileLogin 三处登录后翻写（原 login→1、smsLogin→2、mobileLogin→3）；新增私有方法 checkLoginPlatform(userType, bit, errorCode)（null 按 PC 兜底）与常量 PLATFORM_BIT_PC/MOBILE；login、smsLogin、socialLogin 加 PC 位校验（放在密码校验之后，避免泄露账号平台），mobileLogin 在 TenantUtils.execute 内加移动位校验。
3. 死代码清理：AdminUserService/Impl 的 updateUserType 删除（全仓零残留）。
4. VO 透出：UserSaveReqVO 注释与 @DiffLogField 改名"登录平台"；UserRespVO 新增 userType 字段。
5. PC 前端：api/system/user UserVO 加 userType；UserForm.vue 增"登录平台"双勾选（回显按位与、提交按位或折算，至少勾一个否则拦下）；index.vue 增"登录平台"列（formatter：3 双端/2 移动端/其余 PC 端）。
6. 验证：eslint EXIT=0；mvn -pl module-system/module-system-biz -am compile BUILD SUCCESS；移动端登录页 catch 已透出后端 msg，无需改动。

## 关键结论与待办
- 门禁放在密码验证之后；null 按 PC 兜底（存量 null 账号不迁移也能登 PC）。
- 存量迁移（MCP anwei_fire_remote_monitor_test 复核）：admin(1)=3 保持；test(104)/150/152/155 的 3→2；148/149/151/153/154 的 null 可显式设 1；144/145/147=1 保持。迁移须与新后端同批，旧代码运行期间登录会翻写回旧值。
- 未做：sendSmsCode 发码前平台预校验（P2）；"双端仅超管可配"未做硬门禁（目前为数据口径）。
