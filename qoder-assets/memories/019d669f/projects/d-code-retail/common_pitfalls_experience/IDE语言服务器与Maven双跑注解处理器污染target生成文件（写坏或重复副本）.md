---
title: "IDE语言服务器与Maven双跑注解处理器污染target生成文件（写坏或重复副本）"
usage_scenario:
    - "命令行编译报生成文件莫名语法错误"
    - "target生成目录出现__1后缀重复副本"
    - "MapStruct生成的Impl文件异常"
    - "IDE语言服务器与Maven双跑注解处理器污染target生成文件"
keywords:
    - "JDT generated-sources corruption"
    - "MapStruct Impl truncated"
    - "clean compile fix"
    - "IDE background build pollution"
    - "未命名类预览功能"
source: "auto"
---

## Bug 类型
变体一（写坏）：Maven 命令行编译报大量生成文件语法错误（"需要<标识符>"、"未命名类和预览功能，默认禁用"），错误全部集中在 target/generated-sources/annotations/**.java。
变体二（重复副本）：同一生成目录出现成对的 `XxxMapper__1.java`/`XxxMapper__1Impl.java` 及多套 `AutoMapperConfig__N.java`（如 __129、__130、__136 递增），无报错但目录凌乱，clean 后仍会再次出现。

## 根因
IDE 的 Eclipse JDT 语言服务器（Qoder/VS Code Java 插件）与 Maven 共用同一 target 目录并各自运行注解处理器（APT）：
- 变体一：JDT 后台构建向 target/generated-sources 写入截断损坏的生成文件；
- 变体二：双方对同一源码各自生成同名类，JDT 侧以 `__1` 后缀避让、AutoMapperConfig 按编号递增避让，且会互相覆盖（无后缀 Impl 的 @Generated comments 可能被改写成 Eclipse JDT (IDE)）。

## 识别方法
看生成文件头 `@Generated` 的 comments 字段：`compiler: Eclipse JDT (IDE) 3.46.100...` 为 IDE 侧产物，`compiler: javac` 为 Maven 侧产物。

## 修复模式
语法错误场景：`mvn -pl <module> -am clean compile` 清理 target 重建即可，不要逐行修复生成文件。重复副本场景：无害，可忽略；打包发版用 clean package 避免 IDE 残留 class 进入 jar。仅删单个文件不彻底，IDE 可能再次写入。

## 可复用教训与边界
命令行编译失败时先看错误文件路径：若错误全部位于 target/generated-sources 等生成目录而非 src 源码，优先怀疑 IDE 后台构建产物污染，直接 clean 重编。适用于 IDE 语言服务器与 Maven 共用同一 target 目录的 Java 项目。
