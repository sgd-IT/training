---
title: "Android应用build.gradle版本标识字段定义"
usage_scenario:
    - "修改Android应用版本号时定位配置字段"
    - "核对打包产物版本标识是否正确"
keywords:
    - "build.gradle"
    - "versionCode"
    - "versionName"
    - "Android打包"
    - "语义化版本"
source: "auto"
---

Android 应用版本由 build.gradle 中的 versionCode 与 versionName 两个字段共同标识：versionCode 为整型递增序号（如 89），versionName 为语义化字符串（如 1.1.89）；该配置是打包产物的实际版本生效依据。发布新包后须把同一 versionCode/versionName 登记到后台 base_version_update 表，App 才会弹出更新提示（登记表结构与判定条件见"Android应用版本号与后台系统同步规范"）。
