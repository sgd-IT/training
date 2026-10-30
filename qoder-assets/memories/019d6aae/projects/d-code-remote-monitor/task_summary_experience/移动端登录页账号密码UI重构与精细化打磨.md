---
title: "移动端登录页账号密码UI重构与精细化打磨"
usage_scenario:
    - "当需要复现或参考移动端登录页账号密码UI重构方案时"
    - "当需要为其他登录页应用frontend-design技能进行精细化打磨时"
keywords:
    - "移动端登录页"
    - "UI重构"
    - "SVG图标"
    - "frontend-design"
    - "账号密码登录"
source: "auto"
---

## Task description
- Core requirement: 将移动端登录页 pages/login/index.vue 重构为设计稿所示的「账号密码卡片式登录页」
- Task background: 当前页面仅支持微信小程序手机号快捷登录；用户要求替换为纯账号密码登录，并按设计稿进行视觉和交互的精细化打磨，包括间距、对齐、色彩、阴影、圆角、状态反馈等细节

## Execution process
1. 创建4个新SVG图标：icon-shield-white.svg（顶部Logo）、icon-shield-gray.svg（底部文案）、icon-user.svg（用户名输入框）、icon-lock.svg（密码输入框），全部为单根元素SVG
2. 完全重写 pages/login/index.vue：实现浅色渐变背景、蓝色圆角方块Logo、标题副标题、白色阴影卡片、带图标输入框、蓝色圆角登录按钮、记住密码复选框/忘记密码、底部盾牌文案
3. 登录链路复刻Web端：调用 generate-login-nonce → get-tenant-id-by-username → generate-login-nonce → POST /system/auth/login
4. 应用 frontend-design 技能：建立120/88/44/36/28rpx留白阶梯；统一字体颜色与对比度（#16213a主文字、#6b7484次要文字）；应用双色分层阴影；建立32(卡片)→20(输入框)→48(按钮)→10(复选框)圆角节奏；为输入框、按钮、忘记密码添加hover-class按压反馈；修复原生button[disabled]样式问题
5. 优化适配性：使用 box-sizing:border-box 防溢出；状态栏与底部安全区保留；rpx全量缩放无断点问题

## Related files
- /static/icon-shield-white.svg
- /static/icon-shield-gray.svg
- /static/icon-user.svg
- /static/icon-lock.svg
- /pages/login/index.vue

## Notes
- None

## Task overview
成功完成移动端登录页账号密码UI重构与精细化打磨：创建了4个新SVG图标；完全重写了登录页Vue组件；实现了与Web端一致的多步登录链路；应用frontend-design技能对间距、对齐、色彩、质感、适配性进行了全面优化；所有改动均通过编译验证。
