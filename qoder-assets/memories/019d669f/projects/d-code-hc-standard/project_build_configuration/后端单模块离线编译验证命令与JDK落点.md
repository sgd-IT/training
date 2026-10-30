---
title: "后端单模块离线编译验证命令与JDK落点"
usage_scenario:
    - "改动Java代码后在命令行验证是否编译通过"
    - "IDE诊断不可用时确认改动已产出class"
keywords:
    - "mvn -pl -am compile"
    - "离线编译-o"
    - "JAVA_HOME jdk1.8"
    - "class产物时间戳验证"
source: "auto"
---

改动 ruoyi-system 后在命令行验证编译的可用方式：先设 `$env:JAVA_HOME="D:\Tool\jdk\jdk1.8"`，再执行 `mvn -q -o -pl ruoyi-system -am compile -DskipTests`。-am 会连带编译依赖的 ruoyi-common；-o 离线模式在依赖已缓存时可通过，无输出即表示 BUILD SUCCESS（失败会以 ExitCode 非 0 呈现）。项目编译目标为 Java 1.8，须用 jdk1.8 而非系统默认较新 JDK，否则可能出现与改动无关的环境性失败。IDE 语言服务器未就绪、GetProblems 返回"Language server is not ready"时，可改用 `ruoyi-system\target\classes\...\XxxServiceImpl.class` 的 LastWriteTime 与 Length 确认该源文件确实被重新编译。
