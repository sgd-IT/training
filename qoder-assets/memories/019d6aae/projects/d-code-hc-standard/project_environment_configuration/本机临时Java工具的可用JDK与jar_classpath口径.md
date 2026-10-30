---
title: "本机临时Java工具的可用JDK与jar classpath口径"
usage_scenario:
    - "终端运行临时Java脚本选择java命令"
    - "组装pdfbox或jdbc的classpath参数"
    - "排查java.exe无法执行的架构报错"
keywords:
    - "JAVA_HOME jdk21"
    - "jdks ms-17不可执行"
    - "java单文件源码模式"
    - "mysql-connector-j jar"
    - "pdfbox classpath依赖"
source: "auto"
---

hc-standard 开发机上运行临时 Java 单文件工具（java -cp <jars> X.java，JDK 11+ 源码模式）时，必须使用 PATH/JAVA_HOME 里的 java（JAVA_HOME=D:\Tool\jdk\jdk21）；C:\Users\Administrator\.jdks\ms-17.0.19\bin\java.exe 虽被 IDEA 后端进程正常使用，但在终端直接调用会报"不是有效应用程序"，jstack.exe 同样不可用。常用 jar 落点：mysql-connector-j 8.0.33（com/mysql 与 mysql/mysql-connector-java 两个 groupId 均有）、pdfbox 3.0.7 + pdfbox-io 3.0.7 + fontbox 3.0.7 + commons-logging 1.2（缺 commons-logging 时 pdfbox IOUtils 初始化 ClassNotFound）。
