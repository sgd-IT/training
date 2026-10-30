---
title: "删除小程序免密直登端点，登录面收敛为 mobile-login 单一路径"
usage_scenario:
    - "修改小程序登录链路或微信取号逻辑"
    - "排查手机号无法自助注册问题"
    - "评审 @PermitAll 登录端点安全性"
keywords:
    - "mobile-login"
    - "wx-ma-phone"
    - "账号登录分流"
    - "演示租户自动注册"
    - "登录面收敛"
source: "auto"
---

结论：删除 /admin-api/system/auth/wx-ma-login 端点及 AdminAuthService.wxMaPhoneLogin、AdminUserService.createWxMaUser、platform.login.demo-tenant-id/demo-role-id 配置与 AUTH_LOGIN_DEMO_CONFIG_INVALID 错误码。移动端登录只保留一条路径：POST /system/auth/mobile-login，入参为 account（账号，多数是手机号但不限）+password+loginNonce；后端 getUserListByAccountIgnoreTenant 用 ValidationUtils.isMobile 分流——手机号形态查 mobile，其它查 username；命中后在其所属租户内 authenticate。微信 getPhoneNumber 仅通过 /system/auth/wx-ma-phone 换手机号回填账号框，不再免密直登。

理由：wxMaPhoneLogin 在前端零调用方，是上一版一键免密登录的遗留，却是唯一还能触发「陌生手机号自动注册进演示租户+演示角色」的 @PermitAll 入口，也是三条登录路径中唯一无 nonce 防重放的；与 mobileLogin 重复了跨租户查号、多租户判定、TenantUtils.execute 发包整段逻辑。

影响：新用户无法自助注册，必须由平台租户（tenant_id=1）管理员在 Web 用户管理建号并指定归属租户。Web 端 /auth/login 仍只按 username 匹配，未纳入账号分流。

## Notes（2026-10 复核）
- 现状仍成立：mobileLogin 仍是移动端唯一登录路径；pages/login/index.vue 已是纯账号密码表单，catch 分支以 uni.showToast({ title: e?.msg || '登录失败，请重试' }) 透出后端错误码文案，因此新增的业务级拒绝（如账号平台不匹配）无需改移动端即可正确提示。
- 追加约束：mobileLogin 在 TenantUtils.execute 内的 authenticate 之后还增加了登录平台位校验（user_type 位掩码），后续任何新增移动端登录方式除接 authenticate 外，也必须补同一处平台校验，否则会绕过端隔离。
- 触发时机澄清：移动端登录请求（POST /system/auth/mobile-login）由用户在登录页主动点击「登录」按钮这一事件触发；「获取当前手机号」按钮仅调用 wx-ma-phone 换取手机号回填账号输入框，本身不发起登录请求。排查登录请求来源时应以登录按钮点击为入口。
