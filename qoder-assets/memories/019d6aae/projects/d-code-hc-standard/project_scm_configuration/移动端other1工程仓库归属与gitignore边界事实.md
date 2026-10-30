---
title: "移动端other1工程仓库归属与gitignore边界事实"
usage_scenario:
    - "定位移动端代码所在目录与仓库归属"
    - "调整或核验other1的gitignore排除范围"
    - "区分活跃工程与旧副本或其他移动仓"
keywords:
    - "other1目录"
    - "根gitignore忽略规则"
    - "jks与local.properties排除"
    - "app/build排除缺口"
    - "安卓旧副本"
source: "auto"
---

hc-standard 的移动端 Android 工程在 `d:\code\hc-standard\other1`（模块名 other1，包名 com.sckj.other1，AGP 8.2.0 / compileSdk 36），它不是独立 git 仓库：`git -C other1 rev-parse --show-toplevel` 返回的是父仓库 hc-standard，工程内无 .git。

历史上该目录被根 `.gitignore` 的 `/other1/` 规则整体忽略（同一区块还有 `/android/`、`.lck`），因此长期不进版本管理；2026-10 已删除该规则，other1 源码正式归入 hc-standard 单仓共管，后端 ruoyi-* 模块与移动端在同一仓库同一分支。

排除边界由两层 .gitignore 共同保证：other1/.gitignore 原生排除 `local.properties`（本机 SDK 路径）、`*.jks`（签名密钥 other1.jks）、`*.keystore`、`.idea/`、`*.iml`、`/gradle-app.setting`；其 `/build` 规则只匹配工程根目录，**必须另补 `build/` 才能覆盖 app/build**，否则 3300+ 个 Gradle 产物会入库。

工程内文件构成：app/src/main/java 下 193 个 java；res 下 layout 125、mipmap-xxhdpi 102、drawable 38 等资源；唯一的 jar 是 gradle/wrapper/gradle-wrapper.jar（wrapper 自带，需入库），app 目录下没有 libs 子目录，第三方依赖全部走远程仓库声明。

需区分的同名干扰物：`d:\code\安卓\安卓\other1` 是 2026-03 的旧副本（同样不是 git 仓库，仅作历史参考，diff 价值有限）；`d:\code\android` 是空目录；`d:\code\remote-monitor\anwei-fire-remote-monitor-mobile` 是另一个项目的移动端独立仓库，与 hc-standard 无关。判断"当前活跃移动端工程"要用文件 LastWriteTime 而非目录名。
