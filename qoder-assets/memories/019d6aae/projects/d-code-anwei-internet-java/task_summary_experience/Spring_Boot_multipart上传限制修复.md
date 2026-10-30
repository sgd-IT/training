---
title: "Spring Boot multipart上传限制修复"
usage_scenario:
    - "排查和修复Spring Boot文件上传大小超限异常"
    - "配置Spring Boot multipart时同时设置max-file-size和max-request-size"
    - "多环境YAML配置文件中统一调整上传限制参数"
keywords:
    - "文件上传"
    - "MaxUploadSizeExceededException"
    - "multipart配置"
---

## 任务描述
解决 Spring Boot 应用中文件上传大小超限（1MB）导致的 MaxUploadSizeExceededException 异常。

## 执行过程
```mermaid
graph TD
    A[异常:MaxUploadSizeExceededException 1MB] --> B[全局搜索multipart配置]
    B --> C[定位application-{dev/prod/test}.yml]
    C --> D[读取配置确认max-file-size: 10MB已存在但未生效]
    D --> E[分析发现缺失max-request-size且需确保servlet.multipart层级]
    E --> F[统一将max-file-size提升至100MB]
    F --> G[新增max-request-size: 100MB]
    G --> H[三环境配置同步更新并提示需重启生效]
```

## 任务总结
成功修复上传限制问题：确认仅配置 max-file-size 不足以覆盖总请求大小限制，必须同时设置 max-request-size；将三个环境配置文件中的 multipart 限制统一升级为 100MB，并强调配置生效需重启服务。
