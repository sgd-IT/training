---
title: "Android 平台以 APK 形式打包发布"
usage_scenario:
    - "确定 Android 客户端最终交付物形态"
    - "按目标平台选择对应打包链路"
keywords:
    - "APK"
    - "Android 打包"
    - "交付形态"
    - "构建目标平台"
source: "auto"
---

项目支持以 APK 形式打包发布，仅适用于 Android 平台客户端交付；该打包方式与后端 Java（Maven profile boot/cloud 打 jar）及前端 Web（pnpm run build + tsdown）的打包链路相互独立，选择打包方式时须先确认目标交付平台。
