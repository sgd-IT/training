---
title: "本机 JDK17 路径与命令行默认 JDK21 的差异"
usage_scenario:
    - "需要为本项目指定 JDK17 执行 mvn 时"
    - "排查命令行 java 版本与工程不一致时"
    - "判断某 JDK 目录能否作为 JAVA_HOME 时"
keywords:
    - "ms-17.0.19"
    - "JAVA_HOME临时指定"
    - "命令行JDK21"
    - "D:\\Tool\\jdk不完整"
    - "mvn -version"
source: "auto"
---

anwei-internet-java 在本机（Windows）的 Java 环境实测状态：命令行 PATH 上的 `java -version` 是 JDK 21，不是项目使用的版本；IDEA 工程 SDK 为 JDK 17，实际安装路径为 `C:\Users\Administrator\.jdks\ms-17.0.19`。临时指定方式：在 PowerShell 命令前设置 `$env:JAVA_HOME="C:\Users\Administrator\.jdks\ms-17.0.19"` 后再执行 mvn，`mvn -version` 会随之切到 17。另有 `D:\Tool\jdk` 目录存在但缺少 bin\java.exe（非完整 JDK，不可用作 JAVA_HOME）。注意：切到 JDK17 后 mvn compile 仍会因 lombok 版本过旧失败，JDK 路径正确不等于命令行可编译。根 pom.xml 的 maven.compiler.source/target 仍写 1.8，maven-compiler-plugin 版本为 2.3.2。
