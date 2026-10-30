---
title: "多端用户类型（userType）全链路实现"
usage_scenario:
    - "排查登录被拒或账号平台归属问题"
    - "维护多端账号与用户管理平台字段"
    - "新增用户维度属性并打通VO/DO/Service链路"
keywords:
    - "userType"
    - "VO/DO同步"
    - "多端登录"
source: "auto"
---

## 更正（最新已验证事实）
上述"最后登录渠道翻写"语义已废弃，user_type 改为账号固定平台位掩码：1=PC 端、2=移动端、3=双端（按位或），双端目前仅 admin 配置。已落地：login/smsLogin/mobileLogin 三处登录后翻写全删；login/smsLogin/socialLogin 校验 PC 位、mobileLogin 校验移动位（null 按 PC 兜底，错误码 1_002_000_016/017）；updateUserType 接口与实现删除（全仓零残留）；UserRespVO 与前端 UserVO 透出 userType，PC 用户表单增"登录平台"双勾选、列表增平台列。编译 BUILD SUCCESS、eslint 通过。存量迁移：test/150/152/155 的 3→2；148/149/151/153/154 的 null 可显式设 1；admin 保持 3。

## 补充说明（后续会话：改造已由 agent 按最小单元实际执行完毕）
- 上一轮该改造只以手敲教程交付；后续会话用户改口"你来执行吧，按最小单元我逐个执行"，5 个单元全部由 agent 落盘并验证：①ErrorCodeConstants 新增 016/017 两条平台不匹配错误码（号段经全文件 grep 确认空闲）；②UserSaveReqVO 注释与 @DiffLogField 改名"登录平台"、UserRespVO 新增 userType（此前分页接口不透出该字段，列表拿不到值）；③AdminAuthServiceImpl 删三处翻写 + 新增 checkLoginPlatform(userType, bit, errorCode) 私有方法与 PLATFORM_BIT_PC/MOBILE 常量，login/smsLogin/socialLogin 查 PC 位、mobileLogin 在 TenantUtils.execute 内查移动位，一律放在密码校验之后；④AdminUserService/Impl 的 updateUserType 删除，全仓引用清零；⑤PC 前端 api/system/user/index.ts 的 UserVO 补字段、UserForm.vue 平台双勾选（回显按位与、提交按位或折算、一个都不勾则拦下提示）、index.vue 增"登录平台"列（formatter：3 双端/2 移动端/其余 PC 端）。
- 关键前提复核：MCP 查库确认测试库 admin(id=1) 的 user_type 已是 3，故教程里"admin 1→3"那条迁移作废；待执行仅剩 test/150/152/155 的 3→2 与 null 账号显式设 1（可选，代码已按 PC 兜底）。旧后端仍在运行时登录会把值翻写回去，因此必须"先重启新后端 → 再跑迁移 → 再测"。
- 验证结论：mvn -pl module-system/module-system-biz -am compile（JAVA_HOME=D:\Tool\jdk\jdk21）BUILD SUCCESS（api 66 + biz 361 文件真实重编）；node 直调 node_modules/eslint/bin/eslint.js 三个前端文件 EXIT=0；旧语义文案"1.Web 2.App""Wx 小程序端"全仓 0 命中；移动端登录页 catch 已有 e?.msg 兜底，平台拒绝提示可直接透出，无需改移动端。
- 概念撞名提醒：框架 UserTypeEnum（ADMIN=1/MEMBER=2）与业务字段 system_users.user_type（平台位掩码）取值区间重叠但语义无关，WebFrameworkUtils、数据权限规则用的是前者；本字段不要用 DICT_TYPE.USER_TYPE 字典渲染。
- 仍未做：sendSmsCode 发码前平台预校验（P2）；"双端仅超管可配"仅为数据口径、无硬门禁；本轮改动尚未 git 提交。
