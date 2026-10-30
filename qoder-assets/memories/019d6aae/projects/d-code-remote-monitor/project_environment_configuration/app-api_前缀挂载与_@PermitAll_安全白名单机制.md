---
title: "/app-api 前缀挂载与 @PermitAll 安全白名单机制"
usage_scenario:
    - "app-api路径下登录或回调接口返回401需定位白名单收集机制"
    - "新增@PermitAll端点后判断是否需要额外yaml配置"
    - "线上接口返回401而非404时区分版本缺失与权限配置问题"
keywords:
    - "/app-api"
    - "@PermitAll"
    - "getPermitAllUrlsFromAnnotations"
    - "anyRequest authenticated"
    - "401账号未登录"
    - "tenant ignore-urls"
    - "AppAuthController"
source: "auto"
---

项目 Web 层框架（EnjoyWebAutoConfiguration）为 controller.app 包下的控制器自动挂载 /app-api 前缀，因此所有位于该包下的控制器接口（如 AppAuthController、CameraAlertOpenController）的实际访问路径均为 /app-api/{path}。

免登录机制：框架 EnjoyWebSecurityConfigurerAdapter.getPermitAllUrlsFromAnnotations() 在启动时扫描所有标注 @PermitAll 的方法，从 RequestMappingHandlerMapping.getHandlerMethods() 中提取含前缀的完整 URL pattern 加入 Spring Security permitAll 列表——无需手动在 application.yaml 配置 permit-all_urls。application.yaml 中列出的 auth 相关路径是 tenant ignore-urls（租户上下文跳过），不是安全白名单。

诊断要点：不在 permitAll 白名单中的任何 URL（无论接口是否存在于代码中）都会被兜底规则 anyRequest().authenticated() 拦截，由 AuthenticationEntryPointImpl 统一返回 HTTP 200 + body {"code":401,"msg":"账号未登录"}。若线上出现某个 @PermitAll 端点返回 401，优先怀疑线上后端版本过旧（该 Controller 尚未部署），其次才排查收集机制问题。
