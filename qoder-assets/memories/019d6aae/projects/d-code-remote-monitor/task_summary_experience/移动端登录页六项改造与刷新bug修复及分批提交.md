---
title: "移动端登录页六项改造与刷新bug修复及分批提交"
usage_scenario:
    - "改造移动端登录页UI或底部信息展示"
    - "排查小程序页面被反复重建打断输入"
    - "uni-app项目静态语法校验与提交拆分参考"
keywords:
    - "登录页改造"
    - "定时器泄漏修复"
    - "协议页"
    - "小程序版本号"
    - "分批提交"
source: "auto"
---

## Task description
- Core requirement: 移动端（anwei-fire-remote-monitor-mobile）登录页六项改造：修复"输入到一半就刷新"、按钮文案改"获取当前手机号"、删除忘记密码并核查记住密码、密码显示切换、底部业务负责人拨号+版权署名+版本号、协议勾选（第6项后被用户搁置保持现状）；随后按规范分批 git 提交。
- Task background: 登录页 pages/login/index.vue 为卡片式账号密码登录；首页有30秒告警轮询；共享请求层 utils/request.js 对401无脑 reLaunch 登录页。

## Execution process
1. 刷新bug定位：首页 onLoad 无 token 时 reLaunch 登录页，但 onShow 仍启动 setInterval 轮询；reLaunch 销毁只触发 onUnload 不触发 onHide，清理仅写在 onHide 导致定时器泄漏；泄漏定时器每30秒发无 token 请求必401，request.js 无条件 reLaunch 重建登录页清空输入。
2. 三层修复：pages/index/index.vue 的 onShow 加 token 守卫、新增 onUnload 兜底 clearInterval、fetchAlerts 无 token 直接 return；utils/request.js 的401分支先用 getCurrentPages 末位 route 判断已在 pages/login/index 则静默 reject 不再 reLaunch。
3. pages/login/index.vue：按钮文案改为"获取当前手机号"并加 disabled 防重复点击；删除忘记密码入口/方法/样式；密码框右侧加眼睛图标切换明文密文；底部改为"业务负责人：胡经理18576897600 / 郭经理13430148074"整行可点击 uni.makePhoneCall 拨号，下方 Copyright © 2025 广东安为科技有限公司 + Version（运行时 getAccountInfoSync 取版本，回退 2.0.1）。
4. 记住密码核查结论：链路有效——onLoad 回填、登录成功存 rememberLoginForm、退出登录只清凭证不清该键；此前"感觉没用"是被刷新bug干扰。
5. 新建 pages/common/agreement.vue（服务协议/隐私协议简版模板文本，主体写"广东安为科技有限公司"，待法务定稿替换），并在 pages.json 注册；新建 static/icon-eye.svg、icon-eye-off.svg；icon-shield-gray.svg 变为无引用（保留文件）。
6. manifest.json versionName 升为 2.0.1、versionCode 201。
7. 验证：pages.json JSON 校验通过；三个 Vue 文件 script 提取后用 node --check 做 ESM 语法校验通过；request.js 动态 import 通过；本机无 HBuilderX CLI，最终编译需用户在 HBuilderX 确认。
8. git 提交：先 fetch 发现 anwei/master 最新提交已是本地祖先（本地领先3个，无远端新内容），pull --rebase --autostash 确认 up to date 且改动完整恢复；按逻辑拆3个提交：fix(index) 定时器泄漏修复、feat(login) 登录页新功能、chore(release) 版本号；用 -F 文件方式写中文提交信息并以脚本比对验证 UTF-8 正确；停在本地未 push。

## Related files
- /pages/login/index.vue
- /pages/index/index.vue
- /utils/request.js
- /pages/common/agreement.vue
- /pages.json
- /manifest.json
- /static/icon-eye.svg
- /static/icon-eye-off.svg

## Notes
- index.vue 同文件混用空格与 tab 缩进，SearchReplace 前需用 node 输出 JSON.stringify 逐行确认精确缩进（Grep 输出会剥离前导空白不可靠；PowerShell 内联转义与 ps1 编码在沙箱易失败）。
- 协议文本行业通行做法是用模板按公司主体改写而非从零创作；微信公众平台上架还需单独配置《用户隐私保护指引》。

## Task overview
已完成：六项需求全部落地（第6项协议勾选已实现但按用户指示搁置不再调整），刷新bug根因修复并经静态验证，仓库以3个 Conventional Commits 提交收尾、工作区干净、本地领先远端6个提交未推送。遗留：HBuilderX 真机编译验证、协议文本定稿、refreshToken 令牌续期问题（独立课题未动）。

## Notes（2026-10-16 复核）
- 遗留项中的 refreshToken 令牌续期问题已于 2026-10-16 修复落地：后端 AppAuthController 补 /app-api/system/auth/refresh-token 薄转发（@PermitAll 自动进安全白名单，无需改 yaml）；utils/request.js 改为 401→refreshToken 换新 token→重放一次（retried 防循环、并发共享 refreshingPromise、网络异常不误踢），refreshToken 也失效才清凭证回登录页并提示「登录已过期，请重新登录」。
- 验证：后端 mvn compile 通过 + javap 确认 refreshToken 方法存在；request.js 用 node --check 语法校验通过。生产库 system_oauth2_client.access_token_validity_seconds 实测为 1800 秒（此前提到的 600 已过时）。
- 生效条件：后端须先部署上线，小程序需以 HBuilderX 发行版重新编译上传体验版后才生效。

## Notes（2026-10-16 线上部署诊断补充）
- 同日后续会话进一步诊断了用户体验版"点登录即401"现象：根因不是token过期续期问题，而是线上后端为旧版本部署（不含AppAuthController），generate-login-nonce等/app-api登录端点均被anyRequest().authenticated()兜底拦截返回401。测试环境(192.168.250.130:48080)同一URL正常返回200证明代码本身无误。
- 关键部署约束：build-push.ps1执行git checkout master + git pull后构建Docker镜像，未提交推送到master的代码不会进入线上包。
- 修复闭环：需先git commit+push → build-push选"只构建后端" → 服务器pull-images → 改.env SERVER_VERSION → docker-compose up -d server → 验证/app-api端点可达。
