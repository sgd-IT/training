---
title: "Maven子模块parent artifactId必须严格匹配根模块artifactId"
usage_scenario:
    - "Maven构建报'Maven model problem: parent.relativePath points at ... instead of ...'"
    - "多模块项目中子模块无法正确继承父POM配置"
    - "IDE能运行但Maven命令行构建报parent artifactId不匹配错误"
keywords:
    - "Maven"
    - "parent artifactId"
    - "pom.xml"
    - "多模块项目"
---

Maven多模块项目中，子模块pom.xml的<parent><artifactId>必须严格等于根pom.xml中声明的<artifactId>值，而非<name>值。本项目根pom.xml的<artifactId>为'anwei-internet-java'，但所有子模块（monkey-code-generator、monkey-service、monkey-monitor、monkey-monitor-api、monkey-common）错误地写成了'anwei-internet-of-thing'，导致Maven模型验证报错。修复方式：将各子模块pom.xml第6行<artifactId>标签内容统一替换为'anwei-internet-java'。（来源：Bash）
