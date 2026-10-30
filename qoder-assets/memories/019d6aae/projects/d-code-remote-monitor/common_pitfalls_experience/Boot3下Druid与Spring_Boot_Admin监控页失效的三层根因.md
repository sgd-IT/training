---
title: "Boot3下Druid与Spring Boot Admin监控页失效的三层根因"
usage_scenario:
    - "监控页返回500或空白时定位是哪一层失效"
    - "升级druid或SBA依赖后确认与Boot版本配套"
    - "监控页恢复后判断是否还需补nginx与鉴权"
keywords:
    - "Druid控制台"
    - "Spring Boot Admin"
    - "Boot3自动配置"
    - "NoClassDefFoundError"
    - "Thymeleaf模板"
    - "监控页白屏"
source: "auto"
---

Bug class：Spring Boot 3.4 项目里 Druid 控制台与 Spring Boot Admin 监控页 500 / 白屏，三层根因各在不同层，逐层排才定位得到。

Root cause（2026-10-27 在 anwei-fire-remote-monitor 实测）：① Druid——`druid-spring-boot-starter` 1.2.24 的 jar 里只有 META-INF/spring.factories（且只注册 DruidDataSourceAutoConfigure），没有 Spring Boot 3 只读的 AutoConfiguration.imports，所以 DruidStatViewServletConfiguration / DruidWebStatFilterConfiguration 从不装配，/druid/* 落到 DispatcherServlet；而且 1.2.24 的 StatViewServlet 是 javax 版，Boot3(jakarta) 根本加载不了，1.2.27 才提供 com.alibaba.druid.support.jakarta.StatViewServlet。② SBA——`spring-boot-admin-starter-server` 2.7.15 是 Boot2 时代版本，AdminServerAutoConfiguration 带 @ConditionalOnBean(Marker) 且 javax 依赖，在 Boot3 下整个 Admin Server 不装配（actuator/beans 里查不到 adminServerProperties）；升到 4.x 更糟，4.x 对应 Spring Boot 4，会拖进 spring-boot-reactor/jackson/restclient 4.x 模块，与 Boot 3.4.1 混用直接 NoClassDefFoundError: org/springframework/boot/EnvironmentPostProcessor，Boot 3.4 必须配 SBA 3.4.x。③ UI——SBA 3.x 的 index.html 是 Thymeleaf 模板，少了 spring-boot-starter-thymeleaf 就不会被渲染，且 UI 入口是 /admin/（渲染后 302 到 /admin/applications），直接开 /admin/index.html 拿到的是未渲染模板，base href 停在 / 导致 assets 全部 404 白屏。

Fix pattern：依赖换成 druid-spring-boot-3-starter 1.2.27（属性类包名同步换成 com.alibaba.druid.spring.boot3.autoconfigure.*，测试基类里的旧包名 import 也要一起换），在 EnjoyDataSourceAutoConfiguration 用 @Import 手工引入两个 stat 配置类；SBA 升 3.4.4 并删掉 spring.autoconfigure.exclude 里 server/ui/client 三项自动配置排除（同时开 client 才能让应用注册自己、UI 才有实例），server/pom.xml 补 spring-boot-starter-thymeleaf；前端 iframe 地址改成 '/admin/'。

Reusable lesson：判断「监控页打不开」不要只看 URL 和 500，要用运行时证据分层：actuator/beans 看 Servlet/Bean 有没有注册（能分辨"依赖没装配"与"路由没映射"），actuator/conditions 看自动配置是否被评估，jar 内 META-INF/spring/*AutoConfiguration.imports 看该 starter 有没有 Boot3 的自动配置入口，class 常量池里 grep jakarta/servlet 判断 servlet API 代际。版本对应关系：Spring Boot 3.4 ↔ SBA 3.4.x ↔ druid-spring-boot-3-starter 1.2.2x；SBA 4.x 只配 Boot 4。

适用边界：Spring Boot 3.x + yudao/enjoyiot 系多模块后端里，PC 端「基础设施 → 监控中心」下的 MySQL 监控与 Java 监控页；不适用于 Spring Boot 2 项目（那边 druid starter 与 SBA 2.x 本来就能自动装配）。
