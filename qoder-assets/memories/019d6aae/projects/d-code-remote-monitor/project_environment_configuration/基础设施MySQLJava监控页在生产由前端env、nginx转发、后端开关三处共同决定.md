---
title: "基础设施MySQL/Java监控页在生产由前端env、nginx转发、后端开关三处共同决定"
usage_scenario:
    - "排查PC端MySQL或Java监控页空白或500"
    - "生产开放监控页前核对nginx转发与鉴权"
    - "修改监控口令后确认镜像与挂载文件生效方式"
keywords:
    - "infra_config url.druid"
    - "nginx location清单"
    - "sba.htpasswd"
    - "application-prod.yaml挂载"
    - "actuator heapdump风险"
    - "前端镜像重建生效"
source: "auto"
---

PC 前端「基础设施 → MySQL 监控 / Java 监控」两页的生效链路与生产侧配置现状（2026-10-28 更新）。

三处共同决定：
1. 前端页面形态：src/views/infra/druid/index.vue（MySQL 监控）是 iframe 页，onMounted 读 infra_config 的 url.druid 覆盖默认值（判定 if (data && data.length > 0)，生产库该项 value 为空串故不覆盖，仍走 VITE_BASE_URL 拼接）；src/views/infra/server/index.vue（Java 监控）已改为 el-empty 提示页「Java 监控暂未开放」，不再渲染 iframe，也不再有 ConfigApi/onMounted。两页都被 KeepAlive 缓存，改回 iframe 时注意 onMounted 不重跑的坑。
2. nginx 转发（deploy/config/frontend/nginx.conf，在镜像内、非挂载）：
   - location /druid/ { auth_basic off; proxy_pass http://server:48080/druiddruid/; } 实际为 proxy_pass http://server:48080/druid/，鉴权交给 Druid 自身登录页。
   - location /admin/ { allow <用户公网出口IP>; deny all; auth_basic "Spring Boot Admin"; auth_basic_user_file /etc/nginx/conf.d/sba.htpasswd; proxy_pass http://server:48080/admin/; }。**这段不能整块删除**——删掉后请求落回 try_files SPA 兜底，iframe/页面会显示成前端首页而非报错。
   - sba.htpasswd 由 build-push.ps1 的 2c 段拷进镜像 /etc/nginx/conf.d/，账号 admin，口令用 {SHA} 哈希（PowerShell 用 SHA1+Base64 生成，UTF8 无 BOM 写入）。
3. 后端开关与鉴权（deploy/config/server/application-prod.yaml，是 docker-compose 挂载文件，镜像不带，改动必须单独同步到服务器）：spring.datasource.druid.stat-view-servlet 的 enabled/login-username/login-password 在此配置；SBA 三项 autoconfigure.exclude 已从 dev/prod/deploy 三份 yaml 删除，容器内部 /admin/ 始终可用（内网/curl 可达）。

版本与依赖修复（本地 192.168.250.130:48080 已实测打通）：druid 换 druid-spring-boot-3-starter 1.2.27 + EnjoyDataSourceAutoConfiguration 里 @Import 两个 stat 配置类；SBA 升 3.4.4（4.x 对应 Boot4，混用报 NoClassDefFoundError），server/pom.xml 补 spring-boot-starter-thymeleaf；SBA UI 入口是 /admin/（渲染后 302 到 /admin/applications），直接开 /admin/index.html 拿到未渲染模板会白屏。

SBA 风险口径（决策依据）：management.endpoints.web.exposure.include: '*' 全开时，heapdump 可下载并用 MAT 搜出明文库密码/Redis 密码/api-key，logfile 含带参数的完整 SQL，env/configprops 露基础设施拓扑（password 类字段 Spring 自动脱敏），loggers 可改级别致日志暴涨打满磁盘（可当 DoS）；shutdown 默认 enabled=false。因此 SBA 加 Basic Auth 只算"补上门"，更稳的是 exposure 收窄白名单或该路径不对外。Druid 风险低一个量级：能看到连接池/慢 SQL/SQL 语句、datasource.json 露库地址与用户名，但看不到密码、不给 heapdump。

生效方式：nginx.conf 与 sba.htpasswd 都在前端镜像内 → 必须跑 build-push.ps1 选 Frontend only 重建镜像并改 .env 的 FRONTEND_VERSION 后 up -d nginx-frontend（docker restart 无效）；application-prod.yaml 是挂载文件 → 同步到服务器后重启 server 容器。应急可用 docker cp + nginx -s reload，但容器重建即失效。
