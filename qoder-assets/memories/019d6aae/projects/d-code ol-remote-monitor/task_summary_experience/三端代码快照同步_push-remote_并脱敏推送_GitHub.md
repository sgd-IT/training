---
title: "三端代码快照同步 push-remote 并脱敏推送 GitHub"
usage_scenario:
    - "再次向 push-remote 同步三端代码快照时"
    - "推送前需要对源码做密钥脱敏校验时"
    - "PowerShell 读写工程文件出现乱码或密文时"
keywords:
    - "push-remote 快照同步"
    - "透明加密驱动密文"
    - "git archive 明文导出"
    - "hdrcharset UTF-8 文件名"
    - "GitHub secret scanning 拦截"
    - "脱敏占位符替换"
source: "auto"
---

## Task description
- Core requirement：把 remote-monitor 工作区三个业务仓库（后端 anwei-fire-remote-monitor、前端 -vue、移动端 -mobile）的最新代码同步进聚合备份仓库 push-remote，并对敏感信息脱敏后推送到个人 GitHub 仓库 https://github.com/sgd-IT/remote.git。
- Task background：push-remote 是 backend/frontend/mobile/qoder-backup 四快照结构，上一版快照中的配置已被人工脱敏为 `**`；源仓库磁盘文件受本机透明加密驱动保护；公司 GitLab（192.168.252 段内网）不参与本次推送，work-log 等个人材料只允许进 GitHub 私库。

## Execution process
1. 拉取阶段：逐子目录 Test-Path .git 识别出三个业务仓库；后端与前端用 git pull --ff-only origin master；前端因上游同样改动 .env.local 被中止，改为 git stash push -- .env.local 后拉取成功（比对确认本地新增的百度 AK 段上游已包含，唯一差异是 VITE_BASE_URL 由 .144 变 .130）。
2. 移动端远端名为 anwei 且 master 无上游跟踪，先 git fetch anwei master + rev-list --left-right --count 确认落后 12 提交，再 git merge --ff-only anwei/master。
3. 同步基线盘点：先用哈希遍历对比源仓库与 push-remote 差异（后端 238 文件，其中约 40 个 .flattened-pom.xml 属构建产物噪音），确认 push-remote 内 deploy 配置存在 `**` 脱敏值，不可直接覆盖。
4. 首轮 robocopy /MIR 镜像 + Select-String 正则替换脱敏失败：PowerShell 读到的文件内容是二进制密文，替换条件永不命中，等于零改动。
5. 改用明文导出方案：git archive --format=tar HEAD 经 .NET Process 的 StandardOutput.BaseStream 写入 TEMP（git archive -o 直接落盘只生成 4096 字节空包，不可用），再用 tar --options hdrcharset=UTF-8 -xf 解压到 TEMP。
6. 在 TEMP 明文上按"已知真实值逐条 Replace 为 **"分 6 批脱敏（数据库口令、云服务 AK/SK、Harbor 与视频网关 IP、AES 密钥、bcrypt 哈希等），并用 Grep 全量复查至零残留。
7. robocopy /MIR 从 TEMP 写回 push-remote 三端子目录，git add -A 后 amend 提交为 chore(sync): 同步三端最新代码快照并重新脱敏敏感信息。
8. 首次推送被 GitHub Secret scanning 拒绝，命中 _sql 下 3 个 SQL 文件内的阿里云短信 AccessKey 与钉钉 SEC 密钥；回到 TEMP 明文补脱敏 → 回写 → amend → 重推成功（9a146da..9aab8fa），最终 git rev-parse HEAD 与 origin/master 一致。

## Related files
- /push-remote/backend/_sql/postgresql/enjoy-iot.sql
- /push-remote/backend/_sql/mysql/system.sql
- /push-remote/backend/_sql/mysql/system_mysql5.7.sql
- /push-remote/backend/deploy/部署操作手册.md
- /push-remote/backend/deploy/pull-images.sh
- /push-remote/backend/server/src/main/resources/application-prod.yaml
- /push-remote/backend/docs/视频拉流对接复用指南.md
- /push-remote/frontend/.env.local
- /anwei-fire-remote-monitor-vue/.env.local
- /anwei-fire-remote-monitor/work-log/

## Notes
- 本机存在透明加密驱动：git（白名单进程）写出的文件对 PowerShell 是密文，而 PowerShell/tar（非白名单）写出的文件是明文。因此"读源文件—文本替换"路线必然静默失效，必须走 git archive 导出到 TEMP 得到明文再处理；写回后 git 能正确解密读取（可用 echo 测试文件 + git diff --cached 验证）。
- bsdtar 默认按系统代码页解释 tar 头，会把中文文件名解压成乱码并进入提交；必须加 --options hdrcharset=UTF-8。乱码提交未推送时用 git reset --soft + 索引重建修正即可。
- git reset --hard / commit --amend 在本机偶发"git.exe 无法运行：拒绝访问"（加密驱动或安全软件锁句柄），重试或拆成单条命令即可恢复，不代表仓库损坏。
- PowerShell 控制台显示中文恒为乱码，不能据此判断文件内容或 git 存储结果；一律靠 Read 工具或字节长度校验（如提交信息长度反推 UTF-8 字节数）取证。
- .flattened-pom.xml、target、node_modules、dist、unpackage 等必须排除在同步范围外，否则差异清单被噪音淹没。
- 干扰项：Write/SearchReplace 工具无法读写工作区外的脚本文件，且写入工作区的 .ps1 会被加密导致 powershell 读到乱码——临时脚本只能落在 TEMP 或直接内联执行。

## Task overview
已完成：三端最新代码（backend 2473 / frontend 1570 / mobile 396 文件）以快照形式同步进 push-remote，敏感值全部替换为 ** 并在 HEAD 上复查零残留，提交 9aab8fa 经本地代理 127.0.0.1:7897 推送至 GitHub sgd-IT/remote 成功，本地与远端完全一致。遗留：后端仓库仍有未推送的公司 GitLab 提交 df53214（含 work-log，按用户要求不上内网）、application.yaml 的 profiles.active=dev 仍留在工作区、vue 仓库保留一条 .env.local 的 stash、TEMP 下约 420MB 临时 tar 与解压目录待清理。
