---
title: "安全配置中新增permitAll接口路径的规范操作"
usage_scenario:
    - "为新OSS相关接口（如/upload、/delete等）添加无认证放行时"
    - "修复因缩进错误导致的permitAll配置失效问题"
    - "同类Spring Security配置文件中追加路径时的格式校验"
keywords:
    - "permitAll"
    - "接口放行"
    - "缩进规范"
    - "MonitorAppAuthorizeConfig"
---

## 任务描述
为 /sys/oss/upload 接口在 MonitorAppAuthorizeConfig.java 中配置 permitAll 放行规则。

## 执行过程
```mermaid
graph TD
    A[需求:/sys/oss/upload开放访问] --> B[搜索匹配的安全配置文件]
    B --> C[定位到MonitorAppAuthorizeConfig.java]
    C --> D[读取文件确认已有类似路径如/compressUpload]
    D --> E[首次插入新路径，但缩进错误]
    E --> F[二次SearchReplace修正缩进为标准4空格对齐]
```

## 任务总结
成功为 /sys/oss/upload 添加 permitAll 放行配置，关键点在于：必须与同组路径（如 /sys/oss/compressUpload）保持一致缩进（4空格），避免因缩进错误导致编译失败或配置不生效。
