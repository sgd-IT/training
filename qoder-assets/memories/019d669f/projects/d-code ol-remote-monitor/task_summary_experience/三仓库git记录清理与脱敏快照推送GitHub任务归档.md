---
title: "三仓库git记录清理与脱敏快照推送GitHub任务归档"
usage_scenario:
    - "回顾anwei三仓库git清理与推送GitHub的执行结果"
    - "再次尝试向sgd-IT/remote推送脱敏快照"
    - "排查本机git操作被环境拦截问题"
keywords:
    - "git历史清理"
    - "代码脱敏"
    - "GitHub推送"
    - "透明加密环境"
    - "快照式首推"
    - "敏感信息扫描"
source: "auto"
---

## Task description
- 核心需求：分两阶段。①"清理当前的git记录"——用户确认对三个 anwei-fire-remote-monitor 仓库（后端/vue/mobile）抹掉全部历史重新开始；②改为把三个项目脱敏后推送到 https://github.com/sgd-IT/remote.git，敏感信息全部用 ** 替换。
- 背景：工作区 d:\code ol\remote-monitor 下有 4 个 git 仓库（含第三方 ponytail，默认不动）；三个项目仓库均与 Gitea 远程同步；办公环境部署了透明加密系统，最近被编辑器写过的文件磁盘上是密文。

## Execution process
1. 现状勘察：根目录非仓库，定位 3 个项目仓库 + ponytail；核对各仓库分支/stash/remote/本地领先情况（backend origin、vue origin、mobile 远程名为 anwei），制定"备份 .git → git init -b master → 恢复 remote"计划并向用户展示路径清单获确认。
2. 清理失败：Rename-Item/Move 对 .git 一律"访问被拒绝"，沙箱内外一致；只读体检证明仓库完好无副作用；Get-Content 读 .ps1/.git/config 为乱码 → 判定为环境级防护+透明加密，用户取消该路线。
3. GitHub 探测：ls-remote 确认目标仓库存在且为空、网络通、GCM 已配置。
4. 敏感扫描：正则全仓扫 password/secret/token/IP 等 + 逐文件 Read；甄别误报（IP 归属地测试用例数据、协议常量类编号、上游开源版权头中的作者邮箱/电话予以保留）；定稿约 60 项替换清单。
5. 副本构建：git ls-files -c -o --exclude-standard 复制三仓库到 push-remote/{backend,frontend,mobile}（共 4348 文件）；中文文件名需设控制台 UTF-8 编码 + git -z NUL 分隔修复；git hash-object 抽样对比源与副本全部一致。
6. 脱敏执行：PowerShell 字节级(cp1252)字符串替换处理明文文件（命中 7 个）；加密文件改用 IDE Write/SearchReplace 重写（application*.yaml、deploy 配置、.env*、nginx.conf、docker-compose、部署手册、docs、Java 硬编码等共 34 个文件）；扫描中新发现的数据库/时序库/对象存储口令等凭据及时补替。
7. 复核：git grep 五组残留扫描，密钥组/IP 组 0 命中；git add -A 后 init+commit（root-commit 35f2b3f，作者 sgd-IT <sgd-IT@users.noreply.github.com>，用 git -c 命令级配置避免改全局 config，经用户选择身份并确认摘要）。
8. 推送受阻：首次 push 因 GCM 存无效凭据 Authentication failed → git credential reject 清除；再 push 出现 curl 55 Connection reset（HTTP/2 大体积上传被断），改 HTTP/1.1+postBuffer 重试时被用户取消。

## Related files
- push-remote/backend/server/src/main/resources/application-dev.yaml
- push-remote/backend/server/src/main/resources/application-prod.yaml
- push-remote/backend/deploy/docker-compose.yml
- push-remote/backend/deploy/部署操作手册.md
- push-remote/frontend/.env.local
- push-remote/mobile/utils/request.js
- push-remote/mobile/manifest.json
- push-remote/backend/_sql/mysql/system.sql
- logs/redact.ps1（临时替换器，任务后应清理）

## Notes
- 该环境 Rename/移动 .git 目录被系统级拦截（沙箱外同样失败），抹历史方案在本机不可行；改用"新建 push-remote 副本 + 快照首推"绕开。
- 透明加密导致 PowerShell 读 IDE 写过的文件是密文：git.exe 与 IDE 在放行名单，故验证用 git grep/hash-object，编辑加密文件必须走 IDE Write/SearchReplace 而非脚本。
- commit 前必须检查 git 身份（本机全局 user.name/email 为空会直接 COMMIT-FAILED）。
- GitHub 提交邮箱公开可见，建议 noreply 隐私邮箱（用户已选 sgd-IT@users.noreply.github.com）。
- 归档正文不复述任何被清除的凭据字面值，仅记类别，防止二次泄露。

## Task overview
部分完成：本地副本脱敏与初始提交（35f2b3f，4348 文件，复核 0 残留）已成功；推送因公司网络到 GitHub 大体积上传反复 connection reset 而未完成（远端仍为空仓库），后续需重试 push（建议 HTTP/1.1 或换网络/分批）。原"抹 git 历史"任务经验证在本机环境下不可执行，用户已转向。

## Notes
- 上方"任务概览"中"推送未完成、远端仍为空仓库"已过时：后续会话中用户确认采用全量推送口径（含 3D 模型/地图等大资源），以 HTTP/1.1 + 大 postBuffer 后台推送并重定向输出到日志轮询，最终推送成功（Writing objects 191.08 MiB、Resolving deltas 100%、新分支 master -> master、PUSH-EXIT=0），git ls-remote 核实远端 refs/heads/master 哈希与本地 35f2b3f 完全一致。整条"脱敏→快照→推送 GitHub"链路已完成。
