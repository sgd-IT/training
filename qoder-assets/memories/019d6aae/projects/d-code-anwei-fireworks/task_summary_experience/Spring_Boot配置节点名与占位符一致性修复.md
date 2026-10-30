---
title: "Spring Boot配置节点名与占位符一致性修复"
usage_scenario:
    - "Spring Boot项目启动报错 'Could not resolve placeholder' 时的根因定位与修复"
    - "YAML配置文件对齐过程中因节点命名差异引发的Bean注入失败问题"
    - "多环境配置同步后验证启动异常的标准化排查流程"
keywords:
    - "占位符解析"
    - "YAML节点名"
    - "mqtt.province.host"
    - "配置对齐"
source: "auto"
---

## 任务描述
对齐 application-dev.yml 与 application-prod.yml 配置，并解决因 YAML 节点名与代码占位符不一致导致的 Spring Boot 启动失败问题。

## 执行过程
```mermaid
graph TD
    A[需求:prod/dev配置对比] --> B[识别5类prod有而dev缺失的配置块]
    B --> C[批量向application-dev.yml注入monkey.security/fileupload/qr/wx、qing-tian.chargeType、jt808、gdupload]
    C --> D[启动失败：BeanCreationException提示无法解析${mqtt.province.host}]
    D --> E[定位dev中mqtt节点名为local而非province]
    E --> F[将mqtt.local改为mqtt.province，使占位符与YAML结构严格匹配]
```

## 任务总结
成功完成配置对齐与启动修复：补全5大类缺失配置；关键修复点在于统一 MQTT 配置节点命名——必须与代码中 @Value("${mqtt.province.host}") 的占位符路径完全一致，否则触发 UnsatisfiedDependencyException。
