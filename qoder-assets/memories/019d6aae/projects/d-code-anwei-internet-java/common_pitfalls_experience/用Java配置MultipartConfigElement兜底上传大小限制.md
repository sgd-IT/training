---
title: "用Java配置MultipartConfigElement兜底上传大小限制"
usage_scenario:
    - "需要上传限制不依赖yml加载顺序时"
    - "YAML已配multipart大小但仍报超限"
    - "确认改动写进了激活profile的yml"
keywords:
    - "MultipartConfigElement"
    - "Bean Java配置"
    - "profile未激活不生效"
    - "MaxUploadSizeExceededException"
    - "1MB默认值"
source: "auto"
---

Spring Boot 2.x+ 默认 multipart 上传限制为 1MB（1048576 bytes），仅靠 application.yml 配置可能因 profile 激活失败、YAML 缩进错误或配置未被加载而静默失效。最可靠方案是在 Java 配置类定义 `@Bean MultipartConfigElement`，构造参数依次为 (location, fileSizeThreshold, maxFileSize, maxRequestSize)，单位 bytes，例如 `new MultipartConfigElement("", 100 * 1024 * 1024, 100 * 1024 * 1024, 0)` 表示 100MB 上限且不设临时文件阈值。该 Java 配置优先级高于 yml，用于保证上传限制 100% 生效、不依赖配置文件加载顺序。配套要求：`spring.servlet.multipart.*` 必须写在当前激活 profile 对应的 application-{profile}.yml 中（如 spring.profiles.active=dev 则改 application-dev.yml）；若写在未激活的 profile（如 prod）中则完全不生效。
