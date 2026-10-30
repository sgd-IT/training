---
title: "微信小程序服务器域名配置与request.js更新"
usage_scenario:
    - "Configuring WeChat Mini Program server domains for production deployment"
    - "Updating mobile app API base URL to match production domain configuration"
keywords:
    - "WeChat Mini Program"
    - "domain configuration"
    - "request.js"
    - "HTTPS"
    - "port 18080"
source: "auto"
---

## Task description
- Core requirement: Configure WeChat Mini Program server domains and update the mobile app's request.js to use the correct HTTPS domain with port
- Task background: The project uses nginx to proxy requests to the backend; the WeChat Mini Program requires HTTPS domains for network requests; the current request.js uses a local IP address that won't work in production

## Execution process
1. Analyzed the nginx configuration to identify the SSL domain anxunwang.cn and port 8888
2. Researched WeChat Mini Program domain requirements and discovered they support ports for HTTPS domains
3. Identified the user's actual domain fire-remote-admin.anxunwang.cn:18080 from their input
4. Corrected the pre-connect domain configuration to remove the port number to satisfy the subset requirement
5. Updated request.js BASE_URL to use https://fire-remote-admin.anxunwang.cn:18080/admin-api

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\utils\request.js

## Notes
- WeChat Mini Program domain configuration supports ports for HTTPS domains, contrary to initial assumption
- Pre-connect domain must be a subset of both request domain and DNS pre-resolve domain, requiring removal of the port number
- The nginx configuration shows port 18080 is mapped to the frontend service, making it the correct external port to use
- 【2026-10-17 更正】第 5 步写成的 `/admin-api` 与当前代码不符：实测 utils/request.js 的生产 BASE_URL 是 `https://fire-remote-admin.anxunwang.cn:18080/app-api`（development 分支为 `http://192.168.250.130:48080/app-api`）。域名与端口部分仍准确，仅 API 前缀应以 `/app-api` 为准——移动端全部请求走 controller.app 包自动挂载的 /app-api 前缀。

## Task overview
Successfully configured WeChat Mini Program server domains and updated request.js to use the correct production domain format with port 18080.
