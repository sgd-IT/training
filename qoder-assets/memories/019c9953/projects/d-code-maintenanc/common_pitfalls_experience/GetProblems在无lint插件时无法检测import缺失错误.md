---
title: "GetProblems在无lint插件时无法检测import缺失错误"
usage_scenario:
    - "GetProblems返回'Unable to detect code problems'但实际存在编译错误"
    - "Java项目报类找不到或注解不可用，但静态分析工具未提示"
    - "需要手动排查因import缺失导致的编译失败"
keywords:
    - "GetProblems"
    - "import缺失"
    - "lint插件"
    - "Java编译错误"
---

当IDE未安装lint插件时，GetProblems工具无法检测Java编译错误（如缺少import导致的类未解析），返回'Unable to detect code problems'。此时应结合Glob定位相关文件，用Read读取源码，人工检查报错类是否被正确import（来源：GetProblems）
