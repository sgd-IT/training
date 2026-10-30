---
title: "反控码UI缺失的发版链路诊断与两端产物重建"
usage_scenario:
    - "小程序功能已提交但真机看不到对应UI时"
    - "PC前端需重新出生产包并核验产物时"
    - "排查上传包内容比源码旧的构建流程断裂"
keywords:
    - "构建产物陈旧"
    - "HBuilderX发行"
    - "dist-prod构建"
    - "terser中文转义"
    - "BASE_URL环境切换"
    - "反控码上线"
source: "auto"
---

## Task description
- Core requirement: 定位用户反馈"小程序更新了但反控码 UI 都没有"的原因，并恢复 PC 端与移动端反控码功能的线可见性。
- Task background: 反控码功能源码已提交（移动端 commit 59f846f、PC 前端 commit 6f75a93），后端接口未部署；用户通过微信开发者工具 CLI open unpackage/dist/build/mp-weixin 上传的包里没有反控码入口和页面。

## Execution process
1. 逐层排除后端因素：确认入口全部为前端本地写死——pages/remote-control-code/index.vue 页面模板与样式齐全（min-height:100vh 不会白屏）、pages/profile/index.vue 的 menuGroup2 第一项即 control-code、pages/index/index.vue 首页宫格 todoList 第 4 项 C01，均无权限条件渲染；结论是 UI 缺失与后端无关。
2. 核对 uni-app 编译产物时间戳：unpackage/dist/dev/mp-weixin mtime 10-23 17:38（含 remote-control-code 页、profile js 含 control-code）；unpackage/dist/build/mp-weixin 的 app.json mtime 10-23 06:43 UTC（北京 14:43），早于功能 commit 17:48，且仅 12 个页面、不含 pages/remote-control-code/index，profile/index.js 与 index/index.js 均不含 control-code / C01 —— 判定用户上传的是功能开发前的旧 build 包，根因是改完代码只跑过「运行」（更新 dev），从未跑过「发行」（刷新 build）。
3. 用 HBuilderX CLI 重新发行构建：D:\Tool\HBuilderX.5.14.2026070214\HBuilderX\cli.exe publish --platform mp-weixin --project <移动端目录>，构建成功并自动打开微信开发者工具。
4. 核验新 build 产物：app.json 13 页且含 pages/remote-control-code/index；profile/index.js 含 control-code、index/index.js 含 C01；vendor 中 BASE_URL 编译为生产地址 https://fire-remote-admin.anxunwang.cn:18080/app-api（证明 NODE_ENV=production 生效，非内网 dev 地址）；project.config.json appid 为正式 wx222eb07bdf774f1d。
5. PC 端同样核查：dist-prod 目录不存在（.env.prod 的 VITE_OUT_DIR=dist-prod），dist 目录 mtime 2026-07-16 且内容为 favicon/images/static 等手放静态资源而非 vite 产物 —— 本机从未成功构建过 PC 生产包，线上必然无反控码 UI。
6. pnpm.cmd build:prod 失败：pnpm 触发 supply-chain 检查自动 install，报 [ERR_PNPM_IGNORED_BUILDS] Ignored build scripts: esbuild@0.19.12、@swc/core 等后退出；改为直接调实质命令 node --max_old_space_size=4096 ./node_modules/vite/bin/vite.js build --mode prod，输出 Build successful（exit code 1 为 PowerShell 管道假象）。
7. 核验 dist-prod（531 个 js chunk）：首次 Grep 搜中文「反控码」零命中造成误判，因 terser 将中文转义为大写十六进制 \uXXXX；改用 ASCII 标识符核验通过 —— VerifyModal-C6k40Ogc.js 含 \u4F4D\u53CD\u63A7\u7801（位反控码）+ 二次确认逻辑、RemoteConfigDialog chunk 含 control-code 6 次、remoteConsole.api chunk 含 control-code 1 次。
8. 交付部署指引：dist-prod 覆盖到 18080 nginx 前台静态目录；提醒本次构建把工作区未提交的 alarm-order/index.vue（92 行）一并打进产物，若未定稿应 stash 后重建。

## Related files
- /anwei-fire-remote-monitor-mobile/unpackage/dist/build/mp-weixin（重新发行生成）
- /anwei-fire-remote-monitor-vue/dist-prod（vite --mode prod 新建构建产物）

## Notes
- 「运行」只刷新 dist/dev、「发行」才刷新 dist/build，微信小程序真机看到的是上传到后台的 build 包；排查"UI 都没有"应先比对 build 产物 mtime 与功能 commit 时间，而不是先怀疑后端或权限。
- manifest.json 的 git diff 只是 LF/CRLF 行尾差异，无实质改动，不影响 appid。
- 项目根无 package.json（HBuilderX 项目特征），无法用 npm scripts 构建；project.config.json 无 miniprogramRoot，开发者工具需直接打开产物目录。

## Task overview
已完成诊断并重建两端产物：根因确认为前端生产构建产物陈旧（uni-app build 目录停留在功能开发前、PC 端 dist-prod 从未生成），与后端是否更新无关；移动端经 HBuilderX CLI 发行、PC 端经 vite --mode prod 直调均构建成功并逐项核验含反控码 UI 与生产 BASE_URL。待用户执行：开发者工具点上传并设体验版、真机删小程序清缓存、dist-prod 部署到 nginx、后端 module-eiot + server 发布后码值才会出现。
