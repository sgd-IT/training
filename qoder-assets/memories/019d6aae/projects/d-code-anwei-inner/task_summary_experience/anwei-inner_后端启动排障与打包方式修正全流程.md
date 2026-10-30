---
title: "anwei-inner 后端启动排障与打包方式修正全流程"
usage_scenario:
    - "anwei-inner 后端启动失败或缺 bean 排查"
    - "确定 yudao-server 打包时机与 profile 选择"
    - "选择 local/dev/prod 配置启动"
keywords:
    - "yudao-server 启动排障"
    - "嵌套 fat jar"
    - "mvn clean -P boot"
    - "tenant-id 登录头"
    - "profile local dev prod"
    - "打包时机"
    - "jar 交付部署"
source: "auto"
---

## Task description
- Core requirement: 用户先问"程序启动顺序"，随后要求"帮我启动看看怎么缺那么多，然后让我自己解决"，即实际把 anwei-inner-office 后端跑起来并定位报错根因。
- Task background: IDEA 中 yudao-server 启动类大面积报"无法解析符号 SpringApplication/SpringBootApplication"；项目为 yudao 框架多模块工程（75 个 Maven 模块），根 pom 用 profile 控制单体（boot）/微服务（cloud）打包，默认激活 cloud。

## Execution process
1. 依赖体检：本地 .m2 已有 spring-boot 3.5.9 jar；离线 `mvn -o` 报 4 个 BOM "present, but unavailable/absent"，但检查文件均完整存在 → 判定为 `_remote.repositories` 来源标记误判，非真实缺失；联网 `mvn validate` EXIT=0 佐证。
2. 首次全量编译失败在 yudao-spring-boot-starter-mybatis（找不到 cn.iocoder.yudao.framework.common.* 类），根因是 yudao-common 从未编译过（无 target/classes）；单独构建 yudao-common 后再跑 `-pl yudao-server -am compile` → BUILD SUCCESS，75 模块 0 错误（4m16s）。
3. 默认 cloud profile 执行 package 失败于 yudao-module-intelligence-server：`spring-boot-maven-plugin repackage: Unable to find main class`；确认该模块设计上无启动类，其 pom 用 `<skip>${skip.repackage}</skip>`，只有 boot profile（skip.repackage=true）才跳过。
4. 改用 `-P boot` 打包成功，产出 yudao-server.jar 但体积异常 2614MB；`java -jar` 启动失败：Parameter 0 of method securityFrameworkService（@Bean("ss")，YudaoSecurityAutoConfiguration）required a bean of type PermissionCommonApi that could not be found。
5. 逐层排查：配置 yudao.info.base-package=cn.iocoder.yudao 正常；PermissionApiImpl.class 在模块 target/classes 中存在；fat jar 内 BOOT-INF/lib 含 system-server jar；展开嵌套 jar 后发现其内部结构为 `BOOT-INF/classes/cn/iocoder/...` → 证明子模块 jar 曾被 cloud profile repackage 成可执行包并被嵌入，运行时按 `jar!/cn/iocoder/...` 找类导致模块类集体加载不到。
6. 修复：`mvn clean package -P boot -pl yudao-server -am "-Dmaven.test.skip=true"`（必须 clean，否则 maven-jar-plugin 沿用旧 fat jar）→ 2m51s 成功，jar 降至 325.9MB，嵌套 jar 恢复为普通 `cn/iocoder/...` 结构。
7. 启动验证：Tomcat started on port 48080，Started YudaoServerApplication in 29.1 seconds；/actuator/health 返回 {"status":"UP"}，/v3/api-docs 200；登录接口需带请求头 tenant-id: 1（否则 code=400「请求的租户标识未传递」），admin/admin123 返回 code=0 且拿到 accessToken。
8. 追加答疑：对比 application-local.yaml 与 application-dev.yaml —— local 端口 48090、Redis 远端 db3、关闭验证码、开启 mock 登录、Mapper debug 打 SQL、连接池 1/1；dev 端口 48080、Redis localhost db1、验证码与 mock 走主配置默认、连接池 5/10、日志干净；两者 MySQL 同为 192.168.250.160:3306/anwei-office。application.yaml 默认 `spring.profiles.active: prod`（Redis db5），local/dev 需显式指定。

## Related files
- d:/code/anwei-inner/build-full.txt
- d:/code/anwei-inner/build-package.txt
- d:/code/anwei-inner/build-boot.txt
- d:/code/anwei-inner/build-clean-boot.txt
- d:/code/anwei-inner/run-app.log
- d:/code/anwei-inner/run-app2.log

## Notes
- PowerShell 会把 `-Dmaven.test.skip=true` 拆坏并报 Unknown lifecycle phase '.test.skip=true'，必须整体加引号 `"-Dmaven.test.skip=true"`；`-DskipTests` 无此问题。
- 前端 anwei-inner-office-vue 的 .env / .env.local / .env.dev 为加密或二进制态，读不出接口地址，无法据此判断前端对应哪套后端端口。
- 启动日志残留 2 条 ERROR 为公众号（mp）模块表结构未导入提示，非致命。
- 诊断产物写在仓库根目录之外（d:\code\anwei-inner），已提示用户可自行删除。
- ## Notes（2026-10-12 后续会话补充）
- 启动入口唯一确认：单体方式运行 yudao-server 模块的 cn.iocoder.yudao.server.YudaoServerApplication（端口 48080，application.yaml 默认 spring.profiles.active: prod，无需显式指定）；workspace.xml 中的 BpmServerApplication/CrmServerApplication 等属于框架自带微服务方式，本项目不采用。
- 除本档案记录的"打包 profile"问题外，还存在第二类启动失败：外部 mvn clean/package 后 IDEA 增量编译缓存与磁盘脱节，某模块（如 bpm-server）target/classes 只剩资源文件、0 个 class → IDEA 启动报某 Api bean "could not be found"；进一步恶化时 yudao-server/target/classes 连主类都不存在 → IDEA 启动报 ClassNotFoundException: 找不到或无法加载主类。详见「IDEA 增量编译致模块 target/classes 为空引发启动 bean 缺失」。
- 分流判据：同一份代码 java -jar 能起而 IDEA 不能起 → IDEA 编译输出问题；jar 也报缺 bean → 打包 profile 问题（本档案主体）。
- 杀旧进程释放端口命令：Stop-Process -Id <PID> -Force，或按端口动态查杀 Get-NetTCPConnection -LocalPort 48080 -State Listen | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force }；IDEA/命令行重启前须确认 48080 空闲，否则端口冲突。
- 工程内无 .iml 文件属 IDEA 2023.1+ 正常行为（模块配置移入系统目录 external_build_system）；本地仓库无本工程 2026.01-SNAPSHOT 制品，单模块 mvn 编译必须 `-pl <module> -am`。

## Task overview
已完成：确认源码与依赖本身零缺失（75 模块编译 0 错误）；定位并修复"缺 bean/缺符号"的真实根因——cloud profile 造成的子模块嵌套 fat jar；用 clean + -P boot 重建后应用成功启动（48080，29 秒），健康检查与登录链路全部实测通过；同时交付 local/dev/prod 三套 profile 的差异说明。留给用户：IDEA 侧 Reload All Maven Projects（必要时 Invalidate Caches）、今后统一用 -P boot 打包、按需停止后台进程或导入 mp 模块 SQL。

## Notes（打包时机的适用边界补充）
- 需要打 jar 交付/部署的场景，确实只要在执行打包的那一步带上正确 profile 即可，即 `mvn clean package -P boot -pl yudao-server -am "-Dmaven.test.skip=true"`，且必须先 clean，否则 maven-jar-plugin 会沿用旧的 cloud 态 fat jar。
- 但该做法只修正 Maven 产物，不会改变 IDEA 的运行态：IDEA 直接 Run 启动类走的是各模块 target/classes，而非 fat jar。若此前已被 cloud profile 污染过，IDEA 侧仍需 Reload All Maven Projects（必要时 Invalidate Caches）才能恢复正常。
- 因此"只在打包时处理"仅对 `java -jar` 方式成立；以 IDEA 为主要运行方式时还需同步处理 IDEA 编译缓存，判据见上文分流判据。
