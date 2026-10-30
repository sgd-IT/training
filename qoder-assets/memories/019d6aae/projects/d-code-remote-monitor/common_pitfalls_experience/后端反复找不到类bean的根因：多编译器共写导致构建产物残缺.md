---
title: "后端反复\"找不到类/bean\"的根因：多编译器共写导致构建产物残缺"
usage_scenario:
    - "启动报 NoSuchBeanDefinitionException 但源码有 @Service"
    - "改动后端代码后重建或命令行跑 Maven"
    - "判断 BUILD SUCCESS 是否真的重新编译了改动"
keywords:
    - "产物残缺"
    - "多编译器共写"
    - "NoResourceFoundException"
    - "旧进程未加载新类"
    - "devtools缺失"
    - "进程启动时间比对编译时间"
source: "auto"
---

Bug 类型：启动报 Unable to read meta-data for class ...（AutoConfiguration.imports 声明的类读不到，实测 modbus 的 TaskConfiguration）、No qualifying bean、或 Could not find class [裸类名]（无包名，实测 EnjoyRedisAutoConfiguration，源码完全正常）。

根因（2026-10-14 深度实测）：工作区至少 3 个编译器共写同一棵 target/classes——IntelliJ IDEA 的 jps builder 进程、命令行 Maven、VS Code/Qoder 的 redhat.java 语言服务（jdt.ls），各自持有私有"已编译"缓存且互不知情。任何一方 clean/rebuild 或构建中断后，其余方仍按旧缓存"增量保守"跳过编译，留下残缺产物（实测某模块 13 个源文件只剩 3 个 class、另一模块被清空为 0）；imports 资源与 class 的 staleness 独立演化，"资源还在、类没了"即启动必崩。jdt.ls 的 autobuild 还会用 ECJ 把解析失败写成"毒 class"覆盖正常产物（注解类引用丢包名成裸名，详见"Qoder Java语言服务自动构建用ECJ毒class污染target产物"记忆）。崩溃表象随机（找不到 bean / meta 读不到 / NoSuchMethodError / 裸名类找不到），每次像新问题。

修复模式：停应用 → 用 JDK21 全量重建 mvn -o clean install -DskipTests（69 模块约 2 分钟）→ 日志出现"Recompiling the module / Compiling N source files"才算有效。另注意 settings.xml 的 jdk-17 activeByDefault profile 会把部分模块压到 target 17（已删除该行，详见编译目标记忆），全量重建后须核对 [target xx]。

可复用教训：本项目"找不到类/bean"先查 target/classes 完整性（源文件数 vs class 数、时间戳批次、major 版本一致性），不要先怀疑注解；四兄弟症状（NoSuchBeanDefinition / Unable to read meta-data / NoSuchMethodError / Could not find class 裸名）均先跑产物体检；报错类名形态快速判别——全名=类缺失（产物残缺），裸名=毒 class（findstr /m /s /c:"Unresolved compilation" *.class 秒级定位）。适用边界：Spring Boot 3.4 + 组件扫描 + IDE 与命令行构建共用 target/classes 的场景。

差异化分支（2026-10-14 补充，实测 FireWatchAlarmAssistController）：若对应 class 在 target/classes 中存在且完好，但请求新接口仍报 NoResourceFoundException（Spring MVC 落到 ResourceHttpRequestHandler，错误文案形如 "No static resource admin-api/..."，被全局异常处理器包成 500），则不是产物残缺，而是运行中的旧 JVM 根本没加载这个新增类——本仓库 server 模块未引 spring-boot-devtools，IDEA 默认 HotSwap 又不支持新增类，新写的 Controller 必须重启 ServerApplication 才会注册路由映射，源码与路径拼接本身无需改动。判据：拿 PID 的 StartTime 与该 class 的 LastWriteTime 比对（Get-Process -Id <pid> | Select StartTime；Get-ChildItem <class> | Select LastWriteTime），进程启动早于编译即命中此分支。排查次序建议：先比时间戳（区分新旧进程），再做产物完整性体检（区分残缺/毒 class），最后才怀疑注解与包扫描。
