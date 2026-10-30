---
title: "GetProblems 语言服务未就绪时的假阴性需转真实编译验证"
usage_scenario:
    - "改完 Java 源码后判断是否有编译错误"
    - "GetProblems 返回无法检测或滞后报错"
    - "决定是否需要跑 Maven 编译验证"
keywords:
    - "GetProblems 假阴性"
    - "语言服务未就绪"
    - "滞后诊断"
    - "真实编译验证"
    - "Recompiling 判据"
source: "auto"
---

Bug class: IDE 静态问题检查工具（GetProblems）返回假阴性，被误当作"代码无错误"

Root cause: GetProblems 依赖编辑器 Java 语言服务的索引状态。语言服务仍在初始化时它返回 "Unable to detect code problems. Language server is not ready or still initializing."，这不是"检查通过"而是"检查未发生"；此外刚重写文件后它可能返回编辑前的滞后诊断（例如对已被覆盖的旧内容报"The public type EiotSpace must be defined in its own file"），两种情形都不构成有效结论。

Fix pattern: 把编译器等价的真实构建作为判定来源——用项目要求的 JDK 版本先 clean 目标模块再定向 compile，并只在日志出现「Recompiling the module because of changed source code / Compiling N source files」时才承认 BUILD SUCCESS 有效（仅 BUILD SUCCESS 可能是增量检测漏编译）。GetProblems 结果只作辅助参考，不可替代构建。

Reusable lesson: Don't treat GetProblems' "no problems detected" as a passing check because it may mean the language server is not ready or is reporting stale pre-edit diagnostics, which lets uncompilable code ship; instead confirm with a real build and verify recompilation actually happened. Applies when validating Java/backend source changes right after rewriting files, or whenever GetProblems returns an "unable to detect"/"not ready" message; does not apply to pure comment/Javadoc-only edits (no functional impact, no rebuild needed) and to non-Java projects where the language service is reliably warm.
