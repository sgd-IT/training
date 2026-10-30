---
title: "生产监控页收口：Java监控IP白名单化与双监控口令配置"
usage_scenario:
    - "生产上线或收回监控台对外暴露范围"
    - "配置nginx IP白名单与Basic Auth口令"
    - "解释前端镜像重建与仅打包的区别"
keywords:
    - "Druid监控上线"
    - "SBA白名单鉴权"
    - "htpasswd生成"
    - "监控页提示改造"
    - "前端镜像构建链路"
source: "auto"
---

## Task description
- Core requirement: 为生产环境上线 MySQL（Druid）监控、暂不对外开放 Java（Spring Boot Admin）监控，并为两个监控台配置统一登录口令
- Task background: 前一轮已在本地 dev 打通 druid-spring-boot-3-starter 1.2.27 与 SBA 3.4.4；生产 nginx.conf 原本只有 /、/admin-api/、/app-api/、/wsplayer-proxy、/infra/ws 五个 location，缺 /druid/ 与 /admin/ 反代；用户先要求"先不弄 java 监控"，随后又给出公网出口 IP 要求只放行自己，并两次调整口令

## Execution process
1. deploy/config/frontend/nginx.conf：location /admin/ 先改为 return 403;（不能整段删除，否则请求落回 try_files 的 SPA 兜底，页面会显示成值守大屏而非报错），保留注释形式的反代指令作为将来开放入口
2. deploy/build-push.ps1 的 2c 段：sba.htpasswd 缺失时由 exit 1 降级为 Write-Host WARNING，因为 /admin/ 已不再引用该口令文件，缺文件不应中断构建
3. anwei-fire-remote-monitor-vue/src/views/infra/server/index.vue：移除 IFrame + ConfigApi/onMounted 逻辑，改为 ContentWrap 内 el-empty 提示「Java 监控暂未开放」；选型前先 Grep 确认项目大量使用 el-empty、el-result 零使用，故弃 el-result；defineOptions name 'InfraAdminServer' 保留以维持 KeepAlive key
4. 用 ESLint 定向校验改动文件通过（node node_modules\eslint\bin\eslint.js src/views/infra/server/index.vue，无输出且退出码 0）
5. 浏览器验证受阻：页面 body 只剩"安为智慧消防"，控制台报 [Vue Router warn] No match found for /infra/monitors/admin-server（动态路由未生成＝登录态失效），另有 ERR_CACHE_READ_FAILURE 与 Failed to fetch zh-CN.ts；本机后端 48080 端口 open 但 HTTP 请求超时，jps 显示进程仍在跑 modbus 任务
6. 用户给出公网出口 IP 219.131.130.21（电信 AS4134，网段 219.131.128.0/20）后，location /admin/ 改为 allow 该单 IP + deny all + auth_basic "Spring Boot Admin" + auth_basic_user_file /etc/nginx/conf.d/sba.htpasswd + proxy_pass http://server:48080/admin/
7. 口令两次变更：先用 PowerShell SHA1+Base64 生成 {SHA} 哈希写入 sba.htpasswd 并同步 application-prod.yaml 的 login-password 为 admin123，再按用户要求整体换成 Awkj@20260821（最终哈希 admin:{SHA}nIaKGL67FnpHkrbe7MhnvsOmcWw=）
8. 讲清"重建前端镜像 ≠ 重新打包"：build-push.ps1 Frontend only 走 git pull → npm run build:prod → 起临时 nginx 容器 → docker cp 塞 dist/nginx.conf/sba.htpasswd → docker commit + push，服务器侧改 .env 的 FRONTEND_VERSION 后 pull + up -d

## Related files
- /anwei-fire-remote-monitor/deploy/config/frontend/nginx.conf
- /anwei-fire-remote-monitor/deploy/config/frontend/sba.htpasswd
- /anwei-fire-remote-monitor/deploy/config/server/application-prod.yaml
- /anwei-fire-remote-monitor/deploy/build-push.ps1
- /anwei-fire-remote-monitor-vue/src/views/infra/server/index.vue

## Notes
- SearchReplace 对 application-prod.yaml 返回"failed to match"但 Read 回读证实新值已写入，写后校验存在假失败，须以读回磁盘内容为准
- YAML 标量中间含 @ 无需加引号（项目既有 t@m7Iici2i 裸写可正常运行），仅首字符为 @ 才是保留字符
- 交付物停留在工作区未提交、未推送、未构建镜像；线上生效仍需推 master + 重建前后端镜像 + 单独同步挂载的 application-prod.yaml

## Task overview
部分完成（配置改动全部落地并通过 ESLint，页面渲染验证因登录态失效与后端无响应未完成）：Java 监控对外收敛为"IP 白名单 + Basic Auth + 前端提示页"三层，MySQL 监控具备 /druid/ 反代与 Druid 自身登录；两处口令统一为同一账号体系，后续开放只需改 nginx 一行。

## 备注（后续会话核实结果）
- 上述"未提交"状态在下一轮"提交 git"盘点时仍未改变：本批监控相关改动（dependencies/pom.xml、三个 starter pom、EnjoyDataSourceAutoConfiguration、GlobalExceptionHandler、application-dev/prod.yaml、deploy 下 nginx.conf/build-push.ps1/sba.htpasswd、前端 infra/server/index.vue）全部还停留在工作区，尚未 add/commit，也未推送和重建镜像；线上是否生效仍以实际部署记录为准。
- 盘点同时核实：deploy/config/server/application-prod.yaml 里除本任务的 SBA 启用改动外，还混装了另一主题的连接池改动（max-wait 由 600000 调整为 300000、慢 SQL filters: stat 挂到 dynamic.druid 下），属不同逻辑主题，提交时须按 hunk 拆成两笔或将其留在工作区。
- server/src/main/resources/application.yaml 的 profiles.active 已由 prod 改为 dev，是本地调试态，非本任务改动，按约定不进任何提交。
