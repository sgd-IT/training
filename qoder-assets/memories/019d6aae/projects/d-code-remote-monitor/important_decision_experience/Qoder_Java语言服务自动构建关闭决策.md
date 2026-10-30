---
title: "Qoder Java语言服务自动构建关闭决策"
usage_scenario:
    - "When deciding whether to disable Qoder's Java language service autobuild"
    - "When weighing trade-offs between build safety and language service features in multi-IDE environments"
keywords:
    - "Qoder"
    - "java.autobuild.enabled"
    - "ECJ毒class"
    - "build safety"
    - "language service trade-off"
source: "auto"
---

Conclusion: Disable Qoder's java.autobuild.enabled in .vscode/settings.json to prevent ECJ poison classes from polluting target/classes.
Rationale (trade-off): Disabling autobuild eliminates the root cause of startup crashes caused by jdt.ls writing poison classes, at the cost of losing automatic compilation feedback (e.g., cross-file 'cannot resolve' errors); code completion, navigation, and syntax error highlighting remain fully functional.
Rejected alternatives: Replacing jdt.ls with another language server (e.g., Eclipse JDT) — dropped because it requires heavier setup and does not address the core issue of co-writing the same output directory; keeping autobuild enabled with periodic manual cleanup — dropped because it is reactive and cannot prevent crashes during active development.
Applicable / expiry conditions: Holds while the workspace uses redhat.java with ECJ compiler and shares target/classes with other builders (IDEA/Maven); re-evaluate if jdt.ls switches to javac or if build isolation (e.g., separate output directories) is implemented.
