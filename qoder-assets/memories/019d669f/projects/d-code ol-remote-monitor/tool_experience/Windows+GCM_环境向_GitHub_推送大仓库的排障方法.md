---
title: "Windows+GCM 环境向 GitHub 推送大仓库的排障方法"
usage_scenario:
    - "git push报认证失败或exit 128时"
    - "GitHub大体积推送出现连接重置时"
    - "需核实推送是否真正到达远端时"
keywords:
    - "git push排障"
    - "GCM凭据失效"
    - "credential reject"
    - "HTTP/1.1大文件上传"
    - "ls-remote核实"
source: "auto"
---

## Usage Scenario
Description: 在 Windows+GCM 环境经 HTTPS 向 GitHub 推送仓库时遭遇认证失败或大体积上传中断，以及需要核实推送是否真正到达远端。
Examples: push 报 Authentication failed / exit 128；190MB 级仓库推送出现 curl 55 connection reset；反复重试后需判断远端真实状态。

## Usage Method
git push (Windows + GCM + HTTPS)：①凭据失效（Invalid username or token / Password authentication is not supported）根因是 GCM 缓存了已失效凭据且每次 push 会静默复用——用 protocol=https、host=<域名> 两行管道输入 git credential reject 清除该主机缓存后重试以触发重新登录；②大体积上传在 HTTP/2 下易被中途重置，重推时加 -c http.version=HTTP/1.1（必要时配合增大 http.postBuffer）；③推送输出易被终端环境吞掉，改后台运行并把输出重定向到日志文件轮询——"Writing objects 100%"只是本地缓冲完成，网络上传耗时看后续 remote 响应；④成功判定以 git ls-remote 远端 ref 哈希与本地 commit 一致为准，不轻信 "Everything up-to-date"；⑤提速判断：先做体积统计定位大头（如前端 public 下 3D 模型/地图资源），剔除可选大资源可把上传量降到约 1/4，是否剔除由用户按完整性需求决策。

## Notes
- push 报 128 或 connection reset 时反复原样重试或直接怀疑网络会造成每次都静默失败且协商阶段输出误导判断，必须先清凭据缓存、切 HTTP/1.1 并用 ls-remote 核实远端真实状态
- 该方案在公司网络推送约 191MiB 全量仓库场景已验证成功（新分支 master 建立、远端哈希与本地一致）；不适用于 SSH 协议推送（应查 ssh-agent/key 授权），也不涉及本地文件读写通道问题
