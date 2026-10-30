---
title: "IDE语言服务对Lombok构造器注解报错须以mvn编译实证而非据此改码"
usage_scenario:
    - "IDE即时报错但需判定是否真编译失败"
    - "改动后选择权威编译验证手段"
    - "语言服务未就绪时的错误甄别"
keywords:
    - "语言服务误报"
    - "Flexible Constructor Bodies"
    - "GetProblems不可信"
    - "mvn编译实证"
    - "Lombok AllArgsConstructor"
source: "auto"
---

Bug class: 被 IDE 语言服务的虚假编译错误误导，进而去"修复"本无问题的代码。
Root cause: 在本工作区，Java 语言服务常处于未就绪/初始化状态（GetProblems 会返回 "Language server is not ready or still initializing"），且会对 Lombok @AllArgsConstructor 等注解给出与真实源码级别无关的假报错（实测例："The Java feature 'Flexible Constructor Bodies' is only available with source level 25 and above"），而项目实际用 JDK21 正常编译。这类诊断并非来自 javac，而是语言服务解析器抖动。
Fix pattern: 判断改动是否真的编译失败，唯一权威依据是 mvn compile 的退出码（如 mvn -pl <模块> -am compile -DskipTests; 看 EXITCODE=0），不能采信 GetProblems / 语言服务的即时报错；两者冲突时以 mvn 为准。
Reusable lesson: Don't 因为语言服务报错就去改写 Lombok 注解或构造器写法，因为那多半是误报、改了反而引入真问题；instead 一律用 mvn 编译退出码做最终裁决。适用于本仓库所有 Java 模块改动的编译判定；不适用于 mvn 本身明确报出的 javac 语法/依赖错误（那是真错须修）。
