---
title: "Maven编译目标被压到17的全局settings陷阱（已修复并记录还原点）"
usage_scenario:
    - "命令行编译后端仓库验证改动"
    - "mvn 报目标发行版或类文件版本错误"
    - "mvn 提示 Nothing to compile 但源码已改"
keywords:
    - "settings.xml profile"
    - "target 17"
    - "编译目标版本混装"
    - "jdk-17 activeByDefault"
    - "已修复"
    - "major 52/61/65"
source: "auto"
---

后端仓库 anwei-fire-remote-monitor 要求 Java 21（根 pom java.version=21，revision=1.0.0-jdk21）。本机机器级 JAVA_HOME 已是 D:\Tool\jdk\jdk21，mvn -v 为 21.0.12，直接 mvn 即可编译。

历史陷阱（2026-10-14 发现并已修复）：D:\Tool\apache-maven-3.9.15\conf\settings.xml 的 jdk-17 profile 曾带 <activeByDefault>true</activeByDefault>，无条件把未显式声明编译版本的模块压到 target 17（实测 common=[target 21]，而 infra-api/web/mybatis/redis 等=[target 17]，造成 major 61/65 混存）。已删除该行并保留 <jdk>17</jdk> 条件（真用 JDK17 跑 Maven 时仍会激活），备份在同目录 settings.xml.bak-20261014；实测 mvn -o help:active-profiles 在 JDK21 下活动 profile 列表已为空。若未来构建日志再现 [target 17]，先查该行是否被加回。显式声明版本不受影响：framework/common 显式 21，iot-component-core 显式 8（产物 major 52）。

判定构建正确性：日志须出现「Recompiling/Compiling N source files」且 [target xx] 为 21。maven-compiler-plugin 增量检测不可靠，Nothing to compile 的 BUILD SUCCESS 不可信（先 clean 再 compile）；不要用 -Dmaven.compiler.useIncrementalCompilation=false（PowerShell 会拆坏参数）；多命令用分号连接。
