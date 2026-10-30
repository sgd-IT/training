---
title: "Maven依赖集中管理配置"
usage_scenario:
    - "了解项目如何统一管理多模块依赖版本及关键第三方库选型"
keywords:
    - "dependencyManagement"
    - "Maven"
    - "版本统一"
    - "jackson-databind"
    - "动态数据源"
---

Maven依赖管理机制：
- 通过`<dependencyManagement>`统一声明所有子模块依赖版本
- 关键依赖覆盖示例：`jackson-databind 2.9.10.8`（安全加固）、`spring-boot-dependencies 2.1.5.RELEASE`、`spring-cloud-dependencies Greenwich.SR6`
- 多数据源：`dynamic-datasource-spring-boot-starter 3.3.1`
- 常用工具库：EasyExcel、ZXing、Hutool、PDFBox、iTextPDF、Pinyin4j等
