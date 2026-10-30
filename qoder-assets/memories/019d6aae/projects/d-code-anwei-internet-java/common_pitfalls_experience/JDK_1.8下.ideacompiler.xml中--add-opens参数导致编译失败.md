---
title: "JDK 1.8下.idea/compiler.xml中--add-opens参数导致编译失败"
usage_scenario:
    - "IDEA中使用JDK 1.8编译时报'内部 java 编译器错误'"
    - "检查.compiler.xml发现模块options含-J--add-opens参数"
    - "多模块Maven项目在低版本JDK下无法编译"
keywords:
    - "JDK 1.8"
    - "--add-opens"
    - "compiler.xml"
    - "IDEA编译错误"
---

JDK 1.8 不支持 `--add-opens` JVM 参数，若 `.idea/compiler.xml` 中模块的 `<module name="xxx" options="...--add-opens=..."/>` 存在该参数，会导致 IDEA 编译报错 "内部 java 编译器错误"。修复方式：将对应模块的 `options` 属性值清空为 `options=""`。示例路径：`d:\code\anwei-internet-java\.idea\compiler.xml`，需清理 `monkey-common` 和 `monkey-monitor-api` 模块的 `options` 字段。（来源：SearchReplace）
