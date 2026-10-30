---
title: "remote-monitor 多仓库工作区的 Git 远端与分支配置"
usage_scenario:
    - "在 remote-monitor 工作区任意子仓库执行 git 操作时"
    - "确认各仓库远端名称与上游跟踪分支时"
    - "引用工作区绝对路径构造命令时"
keywords:
    - "code ol 含空格路径"
    - "内网GitLab地址"
    - "master跟踪origin"
    - "anwei远端无上游"
    - "push-remote备份仓库"
source: "auto"
---

后端仓库 anwei-fire-remote-monitor 的 Git 配置（本机实测确认）：
- 工作区根目录：`d:\code ol\remote-monitor`（注意 code 与 ol 之间有空格，PowerShell 中必须整体加引号，否则 cd 失败）；根目录本身不是 git 仓库，各业务仓库是其下的独立子仓库。
- 后端 远端 origin → http://shenguodong@192.168.250.252:10102/r/gongmao/anwei-fire-remote-monitor.git（内网 GitLab，直连正常，无需代理）；当前分支 master 跟踪 origin/master，推送用 git push origin master。
- 前端 anwei-fire-remote-monitor-vue：origin 同名规则，master 跟踪 origin/master。
- 移动端 anwei-fire-remote-monitor-mobile：远端名是 `anwei` 而非 origin，且 master 未设上游跟踪，拉取/推送需显式 git pull anwei master / git push --set-upstream anwei master，直接 git pull 会报 no upstream branch。
- 个人 GitHub 备份仓库为工作区内独立的 push-remote 目录（聚合 backend/frontend/mobile/qoder-backup 四份快照），推 GitHub 需走本地代理。
- application.yaml、.env* 等本地配置改动按用户约定不提交，保留在工作区。
以上由 git remote -v、git status -sb、pull/push dry-run 输出直接证实。
