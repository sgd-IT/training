---
title: "外部安全框架下Jiago接口路径放行配置实现"
usage_scenario:
    - "为漏铝监控新增免认证回调接口时复用安全配置模式"
    - "外部安全框架拦截下覆盖放行特定API路径"
    - "扩展/lyxl/**等多路径白名单时参考优先级设计"
keywords:
    - "JiagoSecurityConfig"
    - "@Order(1)"
    - "WebSecurityConfigurerAdapter"
    - "permitAll"
    - "csrf禁用"
source: "auto"
---

## 任务描述
为 Jiago 设备回调接口 `/api/lyxl/file` 构建绕过外部安全框架默认拦截的放行配置，解决 401 Unauthorized。

## 执行过程
```mermaid
graph TD
    A[401报错:/api/lyxl/file] --> B[识别拦截来源:外部jar monkey-security-core + monkey-admin-core 的OAuth2白名单]
    B --> C[新建JiagoSecurityConfig.java]
    C --> D[加@Order(1)使其优先于框架默认安全配置生效]
    D --> E[继承WebSecurityConfigurerAdapter，antMatcher限定目标路径]
    E --> F[对/api/lyxl/**执行permitAll并csrf.disable()]
    F --> G[后续扩展：同法追加放行/lyxl/**（设备状态上报路径）]
    G --> H[验证无编译错误且设备可无Token直调]
```

## 相关文件
- /monkey-monitor-api/src/main/java/com/monkey/general/config/JiagoSecurityConfig.java

## 注意事项
- 必须用 @Order(1) 提高优先级，否则不生效仍被外部框架拦截返回 401。
- Spring Security 5.1.x 需用 antMatcher/requestMatcher + AntPathRequestMatcher 组合，不支持 requestMatchers lambda 写法。
- 诊断真实传参方式的做法：在 Controller 注入 HttpServletRequest，打印 Content-Type 与全部参数名。
