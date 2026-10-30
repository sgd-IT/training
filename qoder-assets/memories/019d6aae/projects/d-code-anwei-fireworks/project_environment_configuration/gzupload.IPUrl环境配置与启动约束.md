---
title: "gzupload.IPUrl环境配置与启动约束"
usage_scenario: []
keywords:
    - "gzupload.IPUrl"
    - "Spring Profile"
    - "prod启动"
    - "环境隔离"
source: "user"
---

项目中获取token的IP和端口地址通过配置项`gzupload.IPUrl`从配置文件加载，但环境生效强依赖Spring Boot启动参数：**生产环境必须显式指定`--spring.profiles.active=prod`**，否则默认加载`dev` profile，将导致`gzupload.IPUrl`错误使用测试地址（http://39.104.15.82:16825）而非生产地址（http://172.16.48.131:16826），引发严重线上问题。
