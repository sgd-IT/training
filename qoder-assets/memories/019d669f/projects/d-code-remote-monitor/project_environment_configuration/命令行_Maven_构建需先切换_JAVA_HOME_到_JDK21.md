---
title: "命令行 Maven 构建需先切换 JAVA_HOME 到 JDK21"
usage_scenario:
    - "命令行执行 mvn 编译或打包本项目"
    - "排查 invalid target release 21 构建错误"
    - "验证 Java 代码改动是否编译通过"
keywords:
    - "JDK21 build requirement"
    - "JAVA_HOME jdk17 conflict"
    - "maven invalid target release"
    - "incremental compile false success"
source: "auto"
---

本项目（anwei-fire-remote-monitor）构建必须使用 JDK 21：本机 JDK21 安装于 D:\Tool\jdk\jdk21，但命令行 Maven 默认 JAVA_HOME 指向 D:\Tool\jdk\jdk17，直接执行 mvn 会报 "Fatal error compiling: 无效的目标发行版: 21"（例如 mvn install 到 testCompile 时失败）或 class 读取异常。命令行构建前需先执行 $env:JAVA_HOME="D:\Tool\jdk\jdk21"。IDE 中项目 SDK 已配置为 jbr-21，IDE 构建不受影响。另注意：mvn compile（不 clean）会因增量编译跳过而"假成功"，验证编译需用 clean compile。
