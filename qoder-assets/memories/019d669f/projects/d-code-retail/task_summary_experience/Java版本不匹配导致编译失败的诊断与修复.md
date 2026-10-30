---
title: "Java版本不匹配导致编译失败的诊断与修复"
usage_scenario:
    - "When encountering \"不支持发行版本 X\" errors in Java Maven projects"
    - "Diagnosing JDK version mismatches between project requirements and system installation"
keywords:
    - "Java"
    - "JDK version"
    - "Maven"
    - "pom.xml"
    - "JAVA_HOME"
source: "user"
---

## Task description
- Core requirement: diagnose and fix the "不支持发行版本 5" (unsupported release version 5) compilation error in a Java Maven project
- Task background: the project pom.xml specifies `<java.version>21</java.version>` and uses `<release>${java.version}</release>`; the user's system has JDK 17 installed (`JAVA_HOME=D:\Tool\jdk\jdk17`) but no JDK 21; the error occurs because JDK 17 cannot compile with `--release 21`, and IDE/Maven fallbacks to unsupported Java 5

## Execution process
1. Read pom.xml to confirm the required Java version is 21
2. Check IDEA configuration files (.idea/misc.xml, .idea/compiler.xml) to verify IDE settings are correct (they are: languageLevel=JDK_21, SDK=jbr-21)
3. Run `java -version` and `where java` to discover the actual JDK version on the system (JDK 17)
4. List installed JDKs to confirm JDK 21 is missing (D:\Tool\jdk only has jdk1.8 and jdk17)
5. Diagnose root cause: project requires JDK 21 but only JDK 17 is installed; JDK 17 does not support `--release 21`
6. Provide actionable fix: install JDK 21, update JAVA_HOME, reload Maven project in IDE

## Corrections（2026-08-28 验证）
- 无需安装独立 JDK 21：用户明确要求记住，IDEA 自带 JBR 21 可直接用于命令行编译。路径：`D:\Tool\IntelliJ IDEA 2025.1.3\jbr`（版本 21.0.7，JBR-21.0.7+9-895.130-jcef）。
- 命令行编译标准做法（PowerShell）：`$env:JAVA_HOME="D:\Tool\IntelliJ IDEA 2025.1.3\jbr"; $env:Path="$env:JAVA_HOME\bin;$env:Path"; mvn compile -pl <modules> -am -q -DskipTests`
- 注意：本项目 `mvnw.cmd` wrapper 在该终端环境执行会因编码乱码失败（"Cannot start maven from wrapper"），必须用系统 Maven（D:\Tool\apache-maven-3.9.15，Maven 3.9.15）+ 上述 JAVA_HOME 覆盖。
- `mvn -q` 静默模式无输出即编译成功；可通过查看 target/classes 下 .class 文件的 LastWriteTime 确认。
- 该路径随 IDEA 升级而变，失效时按 `D:\Tool\IntelliJ IDEA*\jbr\bin\java.exe -version` 重新定位。

## Corrections（2026-09-17 追加验证）
- `D:\Tool\jdk\jdk21` 目录现已存在且实测可用：`$env:JAVA_HOME="D:\Tool\jdk\jdk21"; mvn -q -pl ruoyi-modules/ruoyi-fireworks -am compile -DskipTests` 编译通过；与 JBR 21 二者任选其一。
- 编译前若 target 中存在损坏的生成文件（如 MapStruct Impl 被截断），先 `clean` 再 `compile` 可消除该类连锁语法错误。
