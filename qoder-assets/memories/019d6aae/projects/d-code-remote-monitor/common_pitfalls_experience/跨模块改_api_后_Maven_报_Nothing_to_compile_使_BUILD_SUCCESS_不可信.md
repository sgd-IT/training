---
title: "跨模块改 api 后 Maven 报 Nothing to compile 使 BUILD SUCCESS 不可信"
usage_scenario:
    - "跨模块新增 DTO 字段或常量后验证编译"
    - "BUILD SUCCESS 但下游找不到新符号时排查"
    - "推送前轻量确认已提交代码编译产物健康"
    - "重构或清理死代码后用clean compile验证编译真实生效"
keywords:
    - "增量编译误判"
    - "Nothing to compile"
    - "class时间戳校验"
    - "clean compile 验证"
    - "maven-compiler-plugin"
source: "auto"
---

Bug class: 多模块 Maven 增量编译对"上游模块源码已改"误判为无需重编，导致 BUILD SUCCESS 结论不可信

Root cause: 在本仓库（module-eiot-api → module-eiot-biz）用 `mvn -pl <biz> -am compile` 时，上游 api 模块输出 "Nothing to compile - all classes are up to date"，其 target/classes 实际是 IDE 后台编译刷新的产物；biz 能编译通过只说明 class 里有新符号，并不代表 Maven 自己重编了 api。同时 maven-compiler-plugin 的 staleMillis 判定在本机不可靠，"Compiling N source files" 可能来自 IDE 抢先写入的 class。

Fix pattern: 跨模块新增字段/常量后，必须单独执行 `-pl <api 模块> clean compile` 并确认日志出现 "Compiling N source files"（本轮实测 55 个文件），再跑下游 biz 的 clean + compile；判定 BUILD SUCCESS 是否有效的唯一依据是"目标模块是否真的发生了重编"，而不是仅看 SUCCESS。可用读取 .class 字节判断新符号是否存在来区分"IDE 已刷新"与"Maven 未重编"两种情形。

轻量校验法（无 rebase 冲突、只需确认产物健康即可推送时）：不必逐模块 clean 重建，改为比对本次改动关键类的 `target/classes/**.class` 与对应 `.java` 的 LastWriteTime，class 晚于源码即说明已被重编（PowerShell 用 Get-ChildItem 两个路径一次取回对比）。本轮实测 FireMonthlyReportServiceImpl.class 与 GbtDataReader.class 均新于源码，配合 reactor 全量 SUCCESS 判定通过。

Reusable lesson: Don't 把 `-am` 一次带出的 BUILD SUCCESS 当作上游改动已编译的证据，因为上游可能被判定为 Nothing to compile 而沿用 IDE 产物，交付前一旦清理 IDE 缓存就会出现"本地过、CI 不过"；instead 对每个被改动的模块单独 clean compile 并核对 Compiling 行数。适用于本仓库 module-eiot-api / module-eiot-biz 这类跨模块新增字段、枚举常量的场景；纯单模块内改动或只改注释署名时无需逐模块重建。
