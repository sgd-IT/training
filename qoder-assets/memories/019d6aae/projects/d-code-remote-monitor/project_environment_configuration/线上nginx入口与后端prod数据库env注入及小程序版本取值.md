---
title: "线上nginx入口与后端prod数据库env注入及小程序版本取值"
usage_scenario:
    - "确认移动端线上请求链路指向哪个服务"
    - "排查线上后端实际连接的数据库实例"
    - "修改小程序版本号显示或发版基线"
keywords:
    - "18080 nginx"
    - "deploy .env 注入"
    - "nginx.conf 打进镜像"
    - "docker-compose 部署"
    - "getAccountInfoSync 版本号"
source: "auto"
---

移动端/PC端线上共用同一台 nginx（https://fire-remote-admin.anxunwang.cn:18080，docker-compose 端口映射 18080:8888）：location / 托管 PC 前端静态 SPA（浏览器打开 /#/duty-screen?scene=city 即此站点，scene 默认 city），location /admin-api/ 与 /app-api/ 反代到后端容器 server:48080；移动端与 PC 大屏共享同一后端、同一云 MySQL 库 anwei_fire_remote_monitor、同一 Redis 与 WebSocket。

## Notes
- 前端配置装配方式（2026-10-16 实测 build-push.ps1 / docker-compose.yml 补正）：deploy/config/frontend/nginx.conf 由构建脚本打成 tar 拷入镜像 /etc/nginx/conf.d/default.conf，再 docker commit 为 nginx-frontend:<version> 推送私有仓库；compose 的 nginx-frontend 服务**只挂载 certs**，不挂载 nginx.conf。因此改了仓库里的 nginx.conf 后，在服务器上单纯 restart 容器无效——必须先在新机器跑 build-push.ps1 选"只构建前端"推送新镜像，服务器改 .env 版本后 `docker-compose up -d nginx-frontend`（start-services.sh restart 等价于 docker-compose restart，不换镜像）。应急可 docker cp 新 conf 进容器后 `nginx -s reload`，容器重建即失效。
- 本条早前把线上库写成"腾讯云 gz-cdb-nmt6uclj:26671"需以 SELECT DATABASE() + @@hostname 实测为准；本轮经 MCP 探测生产库主机标识为 cdb-2cb321a8-*:20120，与记忆中的主机名/端口不一致，说明实例地址会漂移，不可凭本条直接下结论。
