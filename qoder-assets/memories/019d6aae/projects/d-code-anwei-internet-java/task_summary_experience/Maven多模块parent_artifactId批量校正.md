---
title: "Maven多模块parent artifactId批量校正"
usage_scenario:
    - "修复Maven多模块项目中多个子模块parent artifactId引用错误"
    - "排查因artifactId/name混淆导致的Maven模型警告"
    - "新团队接手遗留项目时快速校准模块依赖关系"
keywords:
    - "Maven"
    - "parent artifactId"
    - "多模块校正"
---

## 任务描述
修复多模块 Maven 项目中多个子模块 pom.xml 的 parent artifactId 错误引用问题（误将根模块 name 'anwei-internet-of-thing' 当作 artifactId，实际应为 'anwei-internet-java'）。

## 执行过程
```mermaid
graph TD
    A[报错: monkey-code-generator parent artifactId 不匹配] --> B[读取根pom.xml与子模块pom.xml定位差异]
    B --> C[确认根artifactId为anwei-internet-java而非name值]
    C --> D[修正monkey-code-generator/pom.xml]
    D --> E[主动Grep扫描全部子模块]
    E --> F[识别monkey-service/monkey-monitor/monkey-monitor-api/monkey-common共4处同类错误]
    F --> G[批量修正已确认的2个模块，提示其余待处理]
```

## 任务总结
成功定位并修复 Maven 多模块项目中因混淆 `<name>` 与 `<artifactId>` 导致的 parent 引用错误；建立主动扫描+批量修正模式，避免遗漏；强调 artifactId 必须严格匹配父 POM 的 `<artifactId>` 字段，而非 `<name>`。
