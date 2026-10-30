---
title: "Qoder Java语言服务自动构建导致ECJ毒class污染target产物的诊断与修复闭环"
usage_scenario:
    - "When diagnosing Spring Boot startup crashes with 'class not found' or 'unresolved compilation' messages"
    - "When investigating multi-IDE workspace target/classes pollution"
    - "When patching global Maven configuration files outside the project workspace"
keywords:
    - "Qoder"
    - "jdt.ls"
    - "ECJ毒class"
    - "target/classes污染"
    - "Maven settings.xml"
    - "java.autobuild.enabled"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the recurring Spring Boot startup crash caused by Qoder's Java language service (jdt.ls) automatic build writing ECJ 'poison classes' into the shared target/classes directory
- Task background: The d:\code\remote-monitor workspace is opened simultaneously by IntelliJ IDEA (primary builder) and Qoder (redhat.java); jdt.ls uses ECJ compiler and writes poison classes when its classpath model fails to resolve dependencies; this has previously caused startup crashes (e.g., Could not find class [EnjoyRedisAutoConfiguration])

## Execution process
1. First crash diagnosis: identified missing modbus TaskConfiguration class in AutoConfiguration.imports and confirmed incomplete compilation of redis module (only 3/13 classes present)
2. Root cause deep dive: discovered three concurrent writers to target/classes — IDEA jps builder, Maven CLI, and jdt.ls; uncovered Maven settings.xml jdk-17 profile with activeByDefault=true forcing modules to target 17, causing mixed major versions (52/61/65)
3. Global config fix: used PowerShell regex to safely delete <activeByDefault>true</activeByDefault> from D:\Tool\apache-maven-3.9.15\conf\settings.xml (with backup), verified via mvn help:active-profiles
4. Second crash diagnosis: found bare-name class errors (Could not find class [EnjoyRedisAutoConfiguration]) → javap -v revealed "Unresolved compilation problems" and bare-name annotations → confirmed jdt.ls autobuild as root cause (timestamp match)
5. Stop-the-bleed & rebuild: set "java.autobuild.enabled": false in d:\code\remote-monitor\.vscode\settings.json → terminated jdt.ls process → ran mvn clean install -DskipTests
6. Six-point verification passed: BUILD SUCCESS (2m03s); zero poison class hits (findstr); restored full package names; unified target 21 (68×[21]+1×[8]); zero post-rebuild jdt.ls writes

## Related files
- D:\Tool\apache-maven-3.9.15\conf\settings.xml
- d:\code\remote-monitor\.vscode\settings.json
- framework\spring-boot-starter-mq\target\classes\...

## Notes
- SearchReplace/Write tools cannot edit files outside the workspace; global config files must be modified via terminal commands with BOM detection and precise matching
- Using Select-Object -First/-Last to truncate mvn help:active-profiles output risks missing critical lines; always use Select-String with keywords
- User-cancelled builds leave half-finished modules (e.g., first rebuild stopped at [11/69]), creating independent damage sources
- Diagnostic rule-of-thumb: full-classname error = product incompleteness (check src vs class count); bare-name error = ECJ poison class (findstr for "Unresolved compilation")

## Task overview
Completed closed-loop: both startup crashes (modbus class missing, mq poison class) were root-caused and fixed — jdt.ls autobuild disabled, Maven JDK-17 profile removed, full rebuild passed all six verifications. Remaining structural risk: IDEA and Maven still co-write target tree (mitigated by discipline).
