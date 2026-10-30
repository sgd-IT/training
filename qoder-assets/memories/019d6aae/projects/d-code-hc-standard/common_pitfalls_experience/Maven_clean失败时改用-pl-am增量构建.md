---
title: "Maven clean失败时改用-pl-am增量构建"
usage_scenario:
    - "Maven clean失败提示Failed to delete target目录"
    - "线上服务运行中需快速重新打包特定模块"
    - "多模块项目中避免全量clean耗时和文件锁冲突"
keywords:
    - "Maven"
    - "clean失败"
    - "target锁定"
    - "增量构建"
source: "auto"
---

Maven 执行 `clean` 时若 `target` 目录被运行中的服务占用，会报 Failed to delete 错误；此时应跳过 clean，直接使用 `mvn package -pl <module> -am` 进行模块及其依赖的增量构建。（来源：Bash）
