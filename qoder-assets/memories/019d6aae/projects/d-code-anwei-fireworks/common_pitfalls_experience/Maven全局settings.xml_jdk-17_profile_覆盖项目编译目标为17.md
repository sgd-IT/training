---
title: "Maven全局settings.xml jdk-17 profile 覆盖项目编译目标为17"
usage_scenario:
    - "本机Maven打包Java8项目后class版本异常"
    - "现场JDK8运行jar报UnsupportedClassVersionError"
    - "排查产物字节码版本与pom编译目标不符"
keywords:
    - "settings.xml jdk-17 profile"
    - "maven.compiler.source 覆盖"
    - "UnsupportedClassVersionError"
    - "字节码版本验证"
    - "Java8 打包"
source: "auto"
---

在本机用 Maven 打包目标为 Java 8 的项目时，产物 class 版本会被强制为 61.0（Java 17），部署到现场 JDK 8 报 UnsupportedClassVersionError（61.0 > 52.0），而项目 pom 中 java.version=8 形同虚设。

根因：D:\Tool\apache-maven-3.9.15\conf\settings.xml 中定义 id=jdk-17 的 profile（activeByDefault=true），其 properties 将 maven.compiler.source/target 固定为 17；settings 的 user properties 优先级高于 pom properties，覆盖了 java.version=8（mvn help:evaluate -Dexpression=maven.compiler.source 实测输出 17）。

修复模式：在项目 pom 的 maven-compiler-plugin <configuration> 中显式写 <source>1.8</source><target>1.8</target>，显式 configuration 不会被环境 user property 覆盖，实测产出 52。验证方法：读 jar 内 class 文件第 7-8 字节 major（52=Java8、61=Java17、65=Java21）。

适用边界：任何在这台机器构建且运行环境低于 Java 17 的项目；不要修改全局 settings.xml（会影响用户其他 JDK 17 项目）。
