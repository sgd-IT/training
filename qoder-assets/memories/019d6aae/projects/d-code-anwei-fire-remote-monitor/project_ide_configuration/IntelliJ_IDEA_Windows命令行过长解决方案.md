---
title: "IntelliJ IDEA Windows命令行过长解决方案"
usage_scenario: []
keywords:
    - "IntelliJ IDEA"
    - "命令行过长"
    - "JAR manifest"
    - "运行配置"
---

项目使用IntelliJ IDEA开发，在Windows环境下运行Java应用时可能出现'Command line is too long'错误。解决方法是在运行配置中启用'Shorten command line'并选择'JAR manifest'模式，可通过IDE界面操作（Run → Edit Configurations → Modify options → Shorten command line → JAR manifest）或在`.run/ServerApplication.run.xml`中添加`<shortenClasspath name="MANIFEST" />`配置。
