---
title: "循环内重赋值变量被lambda捕获导致编译失败的修复模式"
usage_scenario:
    - "Java 改造中把循环内可变集合传入 lambda 或 Callable"
    - "编辑工具报语法告警但代码看似正确"
    - "验证后端改动是否真的编译通过"
keywords:
    - "effectively final"
    - "lambda捕获局部变量"
    - "TenantUtils.execute取数"
    - "分层父链查询"
    - "mvn真编译裁决"
source: "auto"
---

Bug class: Java lambda 捕获"循环内被重赋值的局部变量"导致编译失败（本仓库 TenantUtils.execute(...) 取数场景实测）
Root cause: FireWatchAlarmAssistServiceImpl 的反控父链解析用 `while (!levelIds.isEmpty() && depth < N)` 逐层把 levelIds 重新赋值，随后在 lambda / Callable 里引用 levelIds，编译器判定该变量不是 effectively final 而报「从lambda 表达式引用的本地变量必须是最终变量或实际上的最终变量」。同类触发点还包括 for 循环计数器、条件分支内二次赋值的集合，以及把这种变量传进 MyBatis-Plus wrapper 的 lambda。
Fix pattern: 在循环体内先把当轮值拷给一个新的 final 局部变量（如 `final List<Long> queryIds = levelIds;`），lambda 只引用这个拷贝；后续轮次的重赋值不影响已捕获的 final 引用。不要靠"把变量声明挪到循环外"或改成数组包装来绕过。
Reusable lesson: Don't 直接在同文件里对 IDE/SearchReplace 返回的语法告警做回滚或反复改写，因为语言服务与编辑工具会给出过期快照（引用已被修正的行号），改了也白改；instead 用 mvn 真编译作为唯一裁决，并把日志是否出现 Recompiling/Compiling N source files 当作 BUILD SUCCESS 是否有效的判据。适用于本仓库任何把循环内可变变量传入 lambda/Callable 的改造（租户上下文包裹取数、批量分层查询等）；不适用于变量本身只赋值一次的场景，那种写法本就合法无需拷贝。
