---
title: "Spring Boot 启动失败修复：海康URL缺失与多模块配置补齐"
usage_scenario:
    - "Spring Boot 启动报错 'URI is not absolute' 时的根因定位与修复"
    - "微服务项目中 dev 环境配置缺失导致 Bean 创建失败的系统性排查"
    - "多环境配置差异导致功能异常时的 prod/dev 对比与补全"
keywords:
    - "URI is not absolute"
    - "HikVisionUtil"
    - "application-dev.yml"
    - "配置补齐"
source: "auto"
---

## 任务描述
修复 Spring Boot 应用启动失败问题，根因为 HikVisionUtil 初始化时因 hikang.url 配置缺失导致 URI is not absolute 异常，并同步补齐 dev 环境缺失的 prod 关键配置。

## 执行过程
```mermaid
graph TD
    A[启动失败：URI is not absolute] --> B[定位堆栈至HikVisionUtil.init]
    B --> C[读取HikVisionUtil.java和HikVisionProperties.java]
    C --> D[发现hikang.url.group等配置为空]
    D --> E[向application-dev.yml补充hikang.url.token/group/peopleUrl/closeUrl]
    E --> F[对比application-prod.yml识别dev缺失配置]
    F --> G[识别5类缺失：monkey.security / monkey.fileupload+qr+wx / qing-tian.chargeType / jt808 / gdupload]
    G --> H[在application-dev.yml中分块补全全部配置]
```

## 任务总结
成功修复启动异常：先补全海康 URL 必填项避免 init 失败；再通过 prod/dev 配置对比，系统性补齐 OAuth2 安全、文件/二维码/微信、JT808 端口、广东数据上传、青天充电类型共 5 类配置，确保 dev 环境功能完整性与 prod 对齐。
