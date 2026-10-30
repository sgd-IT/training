---
title: "Lombok 编译失败先查版本：jvm.config 注入 add-opens 方案未落地"
usage_scenario:
    - "命令行 mvn compile 报 Lombok IllegalAccessError 时"
    - "判断是否需要升级 pom 中 lombok 版本时"
    - "javac 错误被老版 compiler-plugin 吞掉时"
keywords:
    - "lombok 1.18.6"
    - "BOM锁定版本"
    - "mvn compile失败"
    - ".mvn/jvm.config缺失"
    - "could not parse the error"
    - "user.language=en"
source: "auto"
---

本项目（d:\code\anwei-internet-java）命令行 mvn compile 在 JDK17/JDK21 下失败的判定顺序：先看 Lombok 版本，再谈 --add-opens。

关键事实（2026-10-24 实测）：根 pom.xml 的 lombok 只有 groupId/artifactId、没有 <version>（约第 32-33 行 dependencyManagement 与第 50-53 行 dependencies 两处），因此继承 spring-boot-dependencies 2.1.5.RELEASE 锁定的 lombok 1.18.6；该版本不支持 JDK9+ 编译器内部 API，必然报 `IllegalAccessError: class lombok.javac.apt.LombokProcessor cannot access class com.sun.tools.javac.processing.JavacProcessingEnvironment`。此时加 --add-opens/--add-exports 无效——1.18.6 自身实现不兼容 JDK17，唯一修法是显式声明 lombok 1.18.30+。

曾尝试过的 `.mvn/jvm.config`（每行一个 --add-opens，避开 maven-compiler-plugin compilerArgs 中 -J 前缀无效的问题）当前在工作区已不存在，说明该方案未持续落地，不应默认它存在。

另一处干扰：项目 maven-compiler-plugin 为 2.3.2、source/target 仍是 1.8，它会吞掉 JDK17 的错误输出成 `Failure executing javac, but could not parse the error`，需追加 `-Duser.language=en -Duser.country=US` 才能看到真实堆栈。monkey-common、monkey-service 不含 Lombok 注解处理可正常编译，失败集中在 monkey-monitor 等模块。

验证替代：无法命令行编译时用全仓 grep 确认引用是否真清除，不要只信 LSP GetProblems（它读 target/classes 旧 class 会报假阳性）。
