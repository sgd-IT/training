---
title: "IDEA 增量编译致模块 target/classes 为空引发启动 bean 缺失"
usage_scenario:
    - "IDEA 启动单体报 Api bean 缺失"
    - "同代码 jar 能跑 IDEA 不能跑"
    - "IDEA 启动报找不到或无法加载主类"
keywords:
    - "IDEA 增量编译"
    - "target/classes 为空"
    - "Rebuild Project"
    - "No qualifying bean"
    - "启动方式分流诊断"
source: "auto"
---

Bug 类别：同一份代码 java -jar 启动成功、IDEA 启动 yudao-server 单体却报某模块 Api（如 BpmProcessInstanceApi）bean "could not be found"，或 IDEA 报找不到/无法加载主类。
根因：外部 mvn clean/package 后 IDEA 增量编译缓存与磁盘不一致，IDEA Make 只回填资源文件、漏对某模块执行 javac，该模块 target/classes 下 0 个 .class，运行类路径缺失实现类。
排查编排（按序，别一上来就怀疑依赖）：
1. grep 定位报错接口的定义、实现与各处注入点，核对聚合模块依赖清单是否已含该实现模块——同代码另一条启动路径能跑起来本身已证明依赖完整。
2. bash 读取启动命令特征与历史日志，判定失败那次究竟是进程直跑产物包还是 IDE classpath 启动；日志时间戳还能暴露并存的老进程。
3. 分流：两种方式都失败转打包产物问题（见嵌套 fat jar 记忆）；仅 IDE 方式失败进入下一步逐模块比对编译输出。
4. bash 逐模块统计 target/classes 下 .class 数量并与资源文件对比（正常模块上百个 vs 异常模块 0），git status 确认无源码改动。
5. bash mvn compile 单独编译异常模块验证源码是否有错，区分"代码编译不过"与"IDE 缓存与磁盘脱节"。
6. bash 校验目标类已生成，并检查 48080 端口占用与 IDE 工程配置状态。
修复：mvn compile -pl <缺失模块> -am -P boot -DskipTests 重建输出（本地仓库无本工程 SNAPSHOT jar，-pl 必须带 -am），或 IDEA Build → Rebuild Project；单模块重建后 IDE 仍失败说明缓存整体损坏，升级为全量重建而非继续逐模块修补。
复用教训：No qualifying bean 报错先按"jar 能否启动"分流——jar 能跑/IDEA 不能跑是 IDEA 编译输出问题（本记忆）；jar 也报错查打包 profile（嵌套 fat jar）。外部 mvn clean 后在 IDEA 运行前应先 Rebuild Project，并提醒停掉仍在运行的旧进程，否则下次启动撞端口占用会被误判为修复无效。
