---
title: "生产 nginx 缺 /infra/ws 反代导致 WebSocket 握手返回 200"
usage_scenario:
    - "线上某路径返回200却渲染出另一个前端页面"
    - "新增需反代到后端的location或长连接通道"
    - "排查Druid/Spring Boot Admin等监控页打不开"
keywords:
    - "try_files兜底index.html"
    - "druid监控页异常"
    - "admin/applications缺失"
    - "nginx反代遗漏"
    - "WebSocket 200"
    - "infra/ws"
    - "VITE_BASE_URL为空"
    - "同源iframe加载SPA外壳"
source: "auto"
---

Bug 特征：前端控制台持续刷 "WebSocket connection to 'wss://域名:18080/infra/ws?token=...' failed: Error during WebSocket handshake: Unexpected response code: 200"，约 5 秒重连一次。
根因：云端部署的前端 nginx 容器（docker-compose 18080:8888）配置文件 deploy/config/frontend/nginx.conf 只配了 /admin-api、/app-api、/wsplayer-proxy 三个反代 location，缺 /infra/ws；请求落到 location / 的 try_files 被兜底为 index.html（HTTP 200）。生产 VITE_BASE_URL 为空，前端用 window.location.origin 拼 WS 地址（baseUrl.replace('http','ws') + '/infra/ws?token='），必然打到前端域名。
修复模式：nginx 增加 location /infra/ws { proxy_pass http://server:48080; proxy_http_version 1.1; Upgrade/Connection 头; 长超时 }，重建 frontend 镜像。后端 WS 路径由 platform.websocket.path=/infra/ws 定义（server/src/main/resources/application.yaml），后端 48080 未映射宿主机。
扩展结论（通用规律，已多次验证）：location / 的 try_files $uri $uri/ /index.html 对任何未被显式反代的后端路径都返回 SPA 的 index.html 且状态码是 200 而非 404，故障表现为"页面显示出另一个前端页面"、无任何报错，极易误判成前端跳转 bug。已命中实例：①/infra/ws 握手返回 200；②基础设施菜单 MySQL 监控（iframe src = /druid/index.html）与 Java 监控（/admin/applications）在生产同样被兜底成 index.html，SPA 为 hash 路由、URL 无 # 时解析出 path='/'，经 HomeRedirect 按角色渲染出 /duty-screen，即"监控页里出现值守大屏"。凡新增需要打到后端的路径（WS/SSE/Druid/Spring Boot Admin 等），必须同步在 nginx 加显式 location 反代（长连接类还要关 proxy_buffering），否则一律被 SPA 兜底。
边界：告警实时推送断供时有轮询兜底（大屏侧为定时轮询统计/未处置接口），功能降级不易察觉；本地 dev（VITE_BASE_URL 直指后端）不触发本坑。
