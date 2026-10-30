---
title: "Lombok 1.18.30+ 不需 -J--add-opens 参数"
usage_scenario: []
keywords:
    - "Lombok"
    - "JDK 17"
    - "--add-opens"
    - "IDEA编译错误"
source: "user"
---

Lombok 1.18.30 及以上版本已原生支持 JDK 17，无需在 maven-compiler-plugin 的 compilerArgs 中添加 -J--add-opens 参数；添加会导致 IDEA 内部编译器报 '内部 java 编译器错误'。（来源：read_file）
