---
title: "Java正则中双引号转义应使用\\\"而非\\\\\""
usage_scenario:
    - "在Java代码中编写含双引号的正则表达式时编译报错"
    - "使用SearchReplace修改Java文件正则逻辑时出现Syntax error"
keywords:
    - "Java"
    - "正则"
    - "转义"
    - "SearchReplace"
---

Java字符串字面量中匹配双引号应使用\"，而非\\"。在正则表达式字符串中写\\"会导致编译错误（Syntax error on tokens）。正确写法是\"?[\d.]+\"?。（来源：SearchReplace）
