---
title: "IDEA Maven Runner JRE必须显式设为JDK 1.8而非#JAVA_HOME"
usage_scenario:
    - "IDEA中Maven编译报内部java编译器错误"
    - "项目pom.xml指定java.version=1.8但Maven仍用JDK 17编译"
    - "Project SDK已设为JDK 1.8但Maven构建失败"
keywords:
    - "IDEA"
    - "Maven Runner"
    - "JRE配置"
    - "JDK版本冲突"
source: "auto"
---

IntelliJ IDEA 中 Maven 编译失败报 '内部 java 编译器错误'，根本原因是 Maven Runner 使用的 JRE 与项目 JDK 版本不一致：即使 Project SDK 设为 JDK 1.8，若 Maven → Runner → JRE 仍为 #JAVA_HOME（指向 JDK 17），则 Maven 仍用 JDK 17 编译，与 pom.xml 的 <java.version>1.8</java.version> 冲突。正确做法：Settings → Build, Execution, Deployment → Build Tools → Maven → Runner → JRE 下拉选择 '1.8'（路径如 D:\Tool\jdk\jdk1.8），禁止使用 '#JAVA_HOME' 选项（来源：IntelliJ IDEA 配置）
