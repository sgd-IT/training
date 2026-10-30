---
title: "remote-monitor工作区仓库布局与远程配置"
usage_scenario:
    - "对anwei三仓库执行git操作前定位仓库与远程"
    - "向GitHub快照仓库增量更新或再次推送时"
    - "commit报身份缺失时知悉本机未配全局user"
keywords:
    - "多仓库工作区"
    - "Gitea远程"
    - "anwei-fire-remote-monitor"
    - "git remote配置"
    - "提交身份缺失"
source: "auto"
---

工作区 d:\code ol\remote-monitor 根目录本身不是 git 仓库，其下含 4 个独立 git 仓库：anwei-fire-remote-monitor（Java 后端，远程名 origin）、anwei-fire-remote-monitor-vue（PC 前端，远程名 origin）、anwei-fire-remote-monitor-mobile（uni-app APP，远程名为 anwei，无 upstream 跟踪）、ponytail（从 GitHub 克隆的第三方工具，非项目代码）。三个项目仓库主分支均为 master 且与 Gitea 远程同步；Gitea 服务器为内网地址（192.168.250.x 段、端口 10102），仓库路径形如 /r/gongmao/<项目名>.git，URL 中携带个人账号名——具体值以在各仓库执行 git remote -v 的实时输出为准，不在本记忆中转录。另有对外推送目标 https://github.com/sgd-IT/remote.git：已收录取自 push-remote/{backend,frontend,mobile} 三目录合一的脱敏快照（root-commit 35f2b3f，4348 文件，含 3D 模型/地图等全部大资源共约 191MiB），本地推送副本目录 push-remote/ 与 logs/ 日志保留用于后续增量更新。本机全局 git 未配置 user.name/user.email，commit 需显式指定身份。
