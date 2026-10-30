---
title: "前后端双技术栈：PDF/文档生成与若依后台系统"
usage_scenario: []
keywords:
    - "pdfkit"
    - "fontkit"
    - "pdfbox"
    - "poi-tl"
    - "若依"
    - "Spring Boot 2.5"
---

**前端技术栈**: Node.js项目，核心依赖`pdfkit@0.18.0`，深度依赖字体处理（`fontkit@2.0.4`）、压缩（`brotli@1.3.3`, `pako@1.0.11`）、Unicode支持（`unicode-trie@2.0.0`, `linebreak@1.1.0`）及PNG解析（`png-js@1.1.0`）。

**后端技术栈**: Java 1.8 + Spring Boot 2.5.15，采用若依（RuoYi）框架（v3.8.6）；数据库连接池：`Druid 1.2.16`；PDF处理：`pdfbox 2.0.12`；文档生成：`poi-tl 1.12.1`；JSON：`fastjson2 2.0.39`；验证码：`kaptcha 2.3.3`；JWT：`jjwt 0.9.1`；MQTT：`paho 1.2.5`；工具包：`hutool 5.8.26`。
