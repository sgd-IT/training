---
title: "本机 git 代理注入：GitHub 需走 7897、内网请求须绕开代理"
usage_scenario:
    - "向 GitHub 仓库推送或拉取内容时"
    - "内网 GitLab 或内网 MCP 服务请求超时时"
    - "沙箱终端执行网络命令异常排查时"
keywords:
    - "github 推送需代理"
    - "本地代理 7897"
    - "sandbox 注入 HTTP_PROXY"
    - "内网 GitLab 超时"
    - "MCP 连接失败排查"
source: "auto"
---

本机（Windows，remote-monitor 工作区）git 访问 github.com 必须走本地代理：

- 已验证可用方式：本地代理工具（Clash 类）混合端口 127.0.0.1:7897 处于 LISTENING 时，用一次性参数 `git -c http.proxy=http://127.0.0.1:7897 -c https.proxy=http://127.0.0.1:7897 push` 可成功推送至 GitHub（多次实测 sgd-IT/remote 推送成功）。不写入 git 配置文件。若 7897 未监听（Test-NetConnection -Port 7897 返回 False），重试无意义，应请用户启动代理工具。
- 重要修正一：终端由沙箱（sandbox.exe --workspace-root ... --cwd ... powershell -NoProfile -EncodedCommand）包装执行，会向子进程注入 HTTP_PROXY / HTTPS_PROXY = 127.0.0.1:7897。因此"git 未配置任何代理、环境变量为空"的旧结论不成立；反过来，公司内网 GitLab（192.168.250.252:10102）在该环境下走代理会连接超时而失败，并非"直连正常无需代理"。
- 处理模式：凡对内网地址（GitLab、MCP 服务的 192.168.x.x 端点等）发起请求超时或异常，先怀疑是代理注入所致，而不是网络故障或凭据问题。首选方案是把请求改到不经该沙箱的执行环境（例如 IDE 侧的 MCP 通道、用户自己的交互终端）完成；必须在沙箱内执行时，才对该次命令临时清空代理变量（$env:HTTP_PROXY=''; $env:HTTPS_PROXY='' 后再执行），但注意此类清除可能仍被上层拦截而无效。
- 推 GitHub 与访问内网服务是两类相反的操作：前者需要显式带代理参数，后者需要绕开代理，不要混用同一条命令模板。
