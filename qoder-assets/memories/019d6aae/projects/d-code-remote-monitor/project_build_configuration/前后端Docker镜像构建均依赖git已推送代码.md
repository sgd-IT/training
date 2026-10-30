---
title: "前后端Docker镜像构建均依赖git已推送代码"
usage_scenario:
    - "代码修改后线上行为不变需排查是否遗漏push步骤"
    - "执行或规划后端生产部署操作流程"
    - "改动nginx.conf或口令文件后确认如何生效"
keywords:
    - "build-push.ps1"
    - "docker镜像构建"
    - "FRONTEND_VERSION"
    - "nginx.conf入镜像"
    - "sba.htpasswd入库"
    - "deploy README入口"
source: "auto"
---

后端生产镜像通过 deploy/build-push.ps1 构建：脚本执行 git checkout master + git pull origin master 后从源码 mvn package 生成 jar，再 docker build/push。因此本地未提交且未推送到远端 master 的代码修改不会进入构建产物——任何"代码改了但线上没生效"的首要排查点即为确认改动是否已 commit 并 push 到 master。

前端镜像同理由 build-push.ps1 的 Frontend only 分支构建：git pull → npm run build:prod → 起临时 nginx 容器 → docker cp 把 dist、deploy/config/frontend/nginx.conf、sba.htpasswd 一并塞进容器 → docker commit + push；服务器侧改 .env 的 FRONTEND_VERSION 后 pull + up -d。故 nginx 反代配置与口令文件的改动同样必须先进 git 才可能进镜像，"重建前端镜像"不等于"重新打包"，docker restart 无效。

注意 deploy/config/server/application-prod.yaml 是以 volume 方式挂载到后端容器的，它不进镜像，改完需在服务器上单独同步该文件并重启 server 容器。

凭据口径（2026-10-28 用户拍板，私有库）：deploy/config/frontend/sba.htpasswd 直接入 git（不再走 .gitignore 忽略），application-prod.yaml 里 Druid 控制台账号口令保持明文，与同文件 MySQL/Redis/TDengine 口令同一口径；docker-compose.yml 中不存在 DRUID_USERNAME/DRUID_PASSWORD 透传，服务器 .env 无需补这两行。轮换口令时须记住旧值会永久留在 git 历史。

部署文档入口：deploy/README.md 是唯一上手入口（本地跑起来 → 发版两步 → 验收 → 高频故障对照）。deploy/部署操作手册.md 仍存在，是完整流程与服务架构参考，不再是第一入口。
