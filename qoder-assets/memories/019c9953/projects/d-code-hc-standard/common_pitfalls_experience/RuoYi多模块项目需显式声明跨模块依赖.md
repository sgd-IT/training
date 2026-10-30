---
title: "RuoYi多模块项目需显式声明跨模块依赖"
usage_scenario:
    - "RuoYi项目编译报'程序包xxx不存在'"
    - "某模块Java类引用了其他模块的类但未生效"
    - "Maven多模块项目中模块间调用失败"
keywords:
    - "RuoYi"
    - "多模块依赖"
    - "pom.xml"
    - "Maven"
---

RuoYi多模块项目中，若某模块（如ruoyi-quartz）引用了其他模块（如ruoyi-system）中的包（如com.ruoyi.base.mapper），必须在该模块的pom.xml中显式声明对应依赖，否则编译报'程序包不存在'或'找不到符号'。正确依赖配置为：<dependency><groupId>com.ruoyi</groupId><artifactId>ruoyi-system</artifactId></dependency>（来源：SearchReplace）
