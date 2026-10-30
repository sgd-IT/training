---
title: "双技术栈：Node.js PDF工具链 + Java Spring Boot若依框架"
usage_scenario:
    - "理解项目整体技术选型与分层架构"
    - "对接PDF生成功能时定位相关依赖"
    - "维护后端服务时识别核心框架与中间件"
keywords:
    - "pdfkit"
    - "fontkit"
    - "Spring Boot 2.5"
    - "若依 3.8.6"
    - "Druid"
    - "PDFBox"
---

项目采用双技术栈架构：
- 前端/工具层：Node.js生态，核心依赖`pdfkit@0.18.0`，深度依赖字体渲染（`fontkit@2.0.4`）、压缩（`brotli@1.3.3`, `pako@1.0.11`）、Unicode处理（`unicode-trie@2.0.0`, `linebreak@1.1.0`）等底层库；
- 后端服务层：Java Spring Boot 2.5.15，基于若依（RuoYi）v3.8.6框架，集成Druid连接池、PageHelper分页、Swagger3、POI/POI-TL文档处理、PDFBox、Hutool等企业级组件。
