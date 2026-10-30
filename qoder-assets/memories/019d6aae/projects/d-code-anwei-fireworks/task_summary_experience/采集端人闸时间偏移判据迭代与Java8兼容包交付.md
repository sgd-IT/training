---
title: "采集端人闸时间偏移判据迭代与Java8兼容包交付"
usage_scenario:
    - "再次修改大华采集端出入事件时间处理逻辑"
    - "现场报UnsupportedClassVersionError需重打Java8包"
    - "核对人闸车闸方向与时间字段的归属侧"
keywords:
    - "人闸时间早8小时"
    - "时差比对判据"
    - "设备时钟偏差兜底"
    - "maven-compiler-plugin锁定1.8"
    - "字节码major版本验证"
source: "auto"
---

## Task description
- Core requirement: 让山东企业（company_code=3708319579）大华闸机数据在现场 JDK 8 环境可运行，并使人闸/车闸的进出记录时间与方向在云端正确。
- Task background: 采集端 fireworks-dhsdk-local 分支 EventHandler 订阅设备事件后 HTTP 上报；平台后端对 eventDate.after(new Date()) 的记录静默丢弃；设备 Web 管理页确认设备时区 UTC+8 且时钟正确，但 SDK 上报的时间字段是 UTC 化的。

## Execution process
1. 现场报 UnsupportedClassVersionError（61.0 > 52.0）：读 jar 内 class 第 7-8 字节确认 major=61，即产物为 Java 17 字节码。
2. 用 `mvn help:evaluate -Dexpression=maven.compiler.source` 实测生效值为 17、java.version 被解析为 JVM 版本 21.0.12.1，排除"IDEA 污染 target/classes"猜测（class 时间戳与 mvn 编译时刻一致）。
3. 定位真凶：本机 Maven 全局 settings.xml（apache-maven-3.9.15/conf）中 id=jdk-17 的 profile 设 activeByDefault=true 并把 maven.compiler.source/target 固定为 17，其 user properties 优先级高于 pom。
4. 不改全局配置，在 pom 的 maven-compiler-plugin `<configuration>` 显式写 source/target=1.8，重打后 jar 内 major=52；同时验证 EventHandler.class 含车闸方向映射常量（CE068ADPAJF9DF9）。
5. 人闸时间第一版修复（offset.before(now) ? offset : date）部署后出现"17 点测试显示 8 点"：查库对比 in_out_time(08:47) 与 gmt_create(16:46) 得偏差恰为早 8 小时，推断设备钟比采集机快 30~48 秒导致加 8 被判为未来而不加。
6. 第二版修复改为按"时差大小"判定：Math.abs(now-eventDate) > 4 小时则 +8 还原北京时间，否则直接使用；末尾加"结果仍晚于当前时间则取当前时间"的防未来兜底；重打（17:20，major=52）交付。
7. 提交推送：仅提交 src/main/java/com/netsdk/module/handler/EventHandler.java（+23/-2），提交信息 `fix(dhsdk): 修复人闸事件时间被云端丢弃及车闸进出方向错乱`，快进推送 origin/fireworks-dhsdk-local（cae4dbe2→65be0c80）；pom.xml 按惯例保留工作区未提交。
8. 方向实测：模拟出口设备 CE00E50PAJ2E1A0 上报 → 入库 in_out_state=2；入口 CE00E50PAJ49313 → in_out_state=1，测试行随即 DELETE 清理。

## Related files
- /src/main/java/com/netsdk/module/handler/EventHandler.java
- /pom.xml

## Notes
- 环境级 Maven settings.xml 的 profile 会静默覆盖项目 pom 的编译目标，排查产物字节码异常应先用 help:evaluate 看生效属性，再查 settings.xml，而非怀疑 IDE 污染。
- 任何以"是否晚于当前时间"作为时区判据的写法都不抗时钟偏差（几十秒即误判）；跨时区还原应按时差量级判断，并单独加一道防未来兜底。
- PowerShell 控制台会把 git 输出的 UTF-8 中文按 GBK 显示成乱码，且把 git 的正常 stderr 渲染为红色错误样式，不代表提交或推送失败。
- 人闸方向由后端设备管理表决定（可靠），车闸方向由采集端上报值决定（必须按设备分别配置），二者排障路径不同。

## Task overview
已完成闭环：字节码问题通过 pom 显式锁定 1.8 解决并产出 major=52 的可部署包（17:20 版本为准，此前包全部作废）；人闸时间经两轮迭代最终采用时差比对+防未来兜底；车闸方向修复已提交推送；人员进出方向经双设备实测确认正确。第二版时间修复代码尚在工作区未提交，待现场刷脸验证时间显示为北京时间后再提交。
