---
title: "Spring Security 5.1.x多路径匹配规范"
usage_scenario:
    - "编写HttpSecurity配置时选择正确的路径匹配器"
    - "修复Security编译错误时替换不兼容API"
    - "代码审查时检查路径放行逻辑是否符合版本规范"
keywords:
    - "OrRequestMatcher"
    - "AntPathRequestMatcher"
    - "路径匹配"
---

在Spring Security 5.1.x中，需使用OrRequestMatcher与AntPathRequestMatcher组合实现多路径匹配，替代不支持的lambda形式requestMatchers。
