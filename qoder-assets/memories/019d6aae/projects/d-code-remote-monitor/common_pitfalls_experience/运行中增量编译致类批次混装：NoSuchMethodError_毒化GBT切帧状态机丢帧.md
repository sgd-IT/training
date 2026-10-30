---
title: "运行中增量编译致类批次混装：NoSuchMethodError 毒化GBT切帧状态机丢帧"
usage_scenario:
    - "运行中报 NoSuchMethodError 或 LinkageError 排查"
    - "修改代码重编译重启后接口行为仍是旧版"
    - "IDE增量编译与命令行Maven共用target/classes"
keywords:
    - "NoSuchMethodError"
    - "跨批次类混装"
    - "IDEA与Maven并行编译"
    - "target/classes加载旧字节码"
    - "改码后行为未生效"
source: "auto"
---

Bug 类型：应用运行中突发 `NoSuchMethodError: DeviceStateChange$DeviceStateChangeBuilderImpl.<init>(DeviceStateChange$1)`，紧跟 GBT26875 TCP 切帧器报"无效的结束符"并把下一条完整帧丢弃，且每 8 秒随设备重发循环出现（炸一条、吞一条）。

根因：① 应用运行期间 target/classes 被另一次编译覆盖（实测同一目录混存 Java8 批次 major 52 与 Java17 批次 major 61，Bridge 类 `$1.class` 残留），运行中 JVM 按需加载类造成跨批次混装：调用点来自旧批（Java8 javac 的 `<init>($1)` 桥接），实际加载的 BuilderImpl 来自新批（nestmates，无该构造器）；② NoSuchMethodError 是 Error，穿透 GbtDataReader 只捕 Exception 的 try-catch，resetState() 被跳过，切帧状态机残留（phase=3 + 旧帧头 + RecordParser 停留 fixed(L+4) 模式），下一条帧的前 L+4 字节被吞拼成末尾非 ## 的伪帧；③ 异常同时中断了确认帧发送，设备超时重发。

修复模式：停止进程 → `$env:JAVA_HOME='D:\Tool\jdk\jdk21'; mvn -o clean install -DskipTests` 全量重建 → 重启；GbtDataReader resetState() 已移入 finally。

可复用教训：运行中的 Java 应用出现 NoSuchMethodError/LinkageError，先怀疑"运行中增量编译致类版本混装"，Rebuild+重启即可自愈，别翻业务代码；框架级状态机/资源释放必须放 finally，catch(Exception) 拦不住 Error。适用边界：IDE 与 Maven 共用 target/classes 且运行期间会触发编译的项目。
