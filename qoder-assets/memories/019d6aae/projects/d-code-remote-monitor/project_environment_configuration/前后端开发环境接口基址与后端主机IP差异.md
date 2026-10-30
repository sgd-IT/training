---
title: "前后端开发环境接口基址与后端主机IP差异"
usage_scenario:
    - "确认前端各 mode 下实际后端基址"
    - "判断生产相对路径是否指向前端自身域名"
    - "核对Druid/Spring Boot Admin等iframe地址取值"
keywords:
    - "VITE_BASE_URL为空"
    - ".env.prod 基址"
    - ".env.local 基址"
    - "infra_config监控地址"
    - "server.port 48080"
    - "admin-api 前缀"
    - "192.168.250.130"
source: "auto"
---

后端服务端口在 dev 与 prod 两个 profile 下均为 48080（application-dev.yaml / application-prod.yaml 的 server.port 相同），不存在按环境区分端口的约定；接口统一走 /admin-api 前缀（开放回调类为 /app-api 前缀）。

PC 前端 Vite 环境变量按 mode 文件加载：`pnpm dev` 读 `.env.local`（实测生效配置），其 VITE_BASE_URL 指向 http://192.168.250.130:48080/admin-api，且本机 IP 就是 192.168.250.130——即 pnpm dev 默认连本机后端。`.env.dev` 模式才指向 http://192.168.250.144:48080/admin-api（另一台开发机）。排查"前端连的是哪个后端"时必须先看 .env.local，不能只看 .env.dev，否则会误判实例。

移动端 utils/request.js 中 dev 指向 http://192.168.250.130:48080/admin-api。PC(.144 仅 dev 模式) 与移动端(.130) 跨端验证需分别确认各自基址可达。生产环境的 nginx 入口、反代路径与容器内后端地址见另一条线上部署配置记忆。

数据库/Redis/TDengine 等凭据仅存在于后端 application-*.yaml 内，任何文档与输出均不抄录。
