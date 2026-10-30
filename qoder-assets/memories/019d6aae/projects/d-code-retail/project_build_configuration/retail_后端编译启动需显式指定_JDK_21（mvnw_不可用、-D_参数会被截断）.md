---
title: "retail 后端编译启动需显式指定 JDK 21（mvnw 不可用、-D 参数会被截断）"
usage_scenario:
    - "编译或打包 anwei-fireworks-retail 后端遇到发行版本 21 报错"
    - "以 java -jar 启动 RuoYi-Plus 后端服务"
    - "前端 vue-tsc 检查需区分存量与新引入错误"
keywords:
    - "JDK 21"
    - "Maven 编译"
    - "mvnw 乱码"
    - "vue-tsc 存量错误"
    - "java -jar 启动"
    - "PowerShell 参数截断"
    - "jar 被占用 repackage"
    - "单模块增量构建"
source: "auto"
---

## Usage Scenario / 使用场景
在 retail 工作区编译 anwei-fireworks-retail 后端（RuoYi-Vue-Plus，要求 JDK 21）时，默认 PowerShell 环境下 mvnw.cmd 因编码问题无法执行，且系统默认 JDK 为 17 导致"不支持发行版本 21"报错。

## Usage Method / 使用方法
在 PowerShell 中用全局 Maven 指定 JDK 21 编译（模块级编译带 -am 拉依赖；需生成可执行 jar 时用 install/package 而非 compile）：
$env:JAVA_HOME = "D:\Tool\jdk\jdk21"; Set-Location d:\code\retail\anwei-fireworks-retail; mvn -pl ruoyi-admin -am -DskipTests install
（全量 38 模块构建约 1 分 50 秒；产物为 ruoyi-admin/target/ruoyi-admin.jar）

启动后端：Set-Location d:\code\retail\anwei-fireworks-retail; & 'D:\Tool\jdk\jdk21\bin\java.exe' -jar 'ruoyi-admin\target\ruoyi-admin.jar'
注意：命令行里的 `-Dfile.encoding=UTF-8` 会被终端解析截断，导致 java 报"找不到或无法加载主类 .encoding=UTF-8"，需省略该参数。启动后 profile 为 dev，约 24 秒就绪，Jetty 监听 8080。

前端类型检查：pnpm.ps1 被执行策略阻止，需在项目目录下用 pnpm.cmd exec vue-tsc --noEmit。工作区当前存量 tsc 错误为 3 个：device/index.vue 的 DefaultRow、monitor/logininfo 的两个大小写路径错误，属既有问题非新引入（更早记录曾为 5 个，含 system/user/view.vue gender，现已减少）。

## Notes
- mvnw.cmd 在此机器的 PowerShell（GBK 控制台）下解析乱码，勿用
- cmd /c 形式被终端拒绝（Shell Type is PowerShell）
- JDK 21 路径 D:\Tool\jdk\jdk21（另有 jdk1.8/jdk17）；若 PATH 无 mvn，用绝对路径 `D:\Tool\maven\apache-maven-3.9.11\bin\mvn.cmd`
- pnpm 需先 Set-Location 到项目目录再执行，-C 参数指向的路径可能被误解析

## 增量重建补充
- 只改动单个业务模块（如 ruoyi-fireworks 的 Controller）时，两步构建比 `-pl ruoyi-admin -am` 全量快得多：先 `mvn -pl ruoyi-modules/ruoyi-fireworks -DskipTests install -q`，再 `mvn -pl ruoyi-admin -DskipTests package -q`，用 $LASTEXITCODE 与 jar 的 LastWriteTime 判断是否真正产出新包。
- spring-boot repackage 前必须先终止仍在运行的后端进程，否则报 "Unable to rename ruoyi-admin.jar to ruoyi-admin.jar.original"（文件被占用）。按命令行特征批量杀进程可能静默不生效，应先列出 java.exe 的 ProcessId 与 CommandLine 确认目标 PID，再用 Stop-Process -Id <PID> -Force 精确终止。
- 在沙箱内跑 maven 可能失败于 ruoyi-system 编译阶段，报 javac 初始化异常写 C:\Users\<user>\.msp 被拒绝；此时需以沙箱外权限执行同一命令。
