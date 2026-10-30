---
title: "APP端项目位置与构建配置"
usage_scenario:
    - "When debugging or developing the APP端"
    - "When setting up the development environment for the mobile application"
keywords:
    - "APP端"
    - "other1"
    - "Android"
    - "Gradle"
    - "build configuration"
    - "IDEA模块未注册"
    - "独立项目"
source: "user"
---

The APP端 (mobile application) is located in the d:\code\hc-standard\other1\ directory. It is an Android Gradle project, with files such as app/build.gradle and gradlew present in the root directory. other1 是从外部拷贝进来的独立项目（源路径 E:\Android\AndroidStudioProjects\3_other\other1），在 hc-standard 主项目（Maven）的 IDEA 窗口中打开时，.idea/modules.xml 不包含 other1 的任何模块记录——IDEA 将其视为普通文件夹而非 Gradle 模块。正确做法是在 IDEA 中单独以 Gradle 项目方式打开 other1 目录（或直接用 Android Studio）。

【2026-09-28 构建实操定稿】命令行打包（已实测通过）：cd d:\code\hc-standard\other1; $env:JAVA_HOME='C:\Users\Administrator\.jdks\ms-17.0.19'（Gradle 8.5 + AGP 8.2 必须 JDK 17，本机该路径有效）; .\gradlew.bat assembleDebug --console=plain -q → 产物 app\build\outputs\apk\debug\app-debug.apk（约 14MB）；assembleRelease 同法。工程配置：SDK 路径 local.properties 指向 D:\Tool\sdk；debug 与 release 共用同一签名 other1.jks（密码明文在 app/build.gradle 的 signingConfigs，别名 key0）→ 两包签名一致、同包名 com.sckj.other1，可直接互相覆盖安装且覆盖客户现装版本不丢数据；两者均 minifyEnabled false（不混淆），实际差异仅 debug 带可调试标记。版本号已由 89/1.1.89 升至 90/1.1.90（2026-09-28，发布指向 220 的版本）。重要特性：APK 为多 dex（classes.dex+classes2~11.dex 共 11 个），校验 APK 内字符串（如服务器地址）必须逐 dex 搜索——解包用 tar -C <dir> -xf <apk> --include "classes*.dex"（Windows 自带 tar 的 -C 须放在 -xf 前），再用 .NET ReadAllBytes+ASCII.GetString+Contains 搜索（findstr 对二进制 dex 会漏报）。
