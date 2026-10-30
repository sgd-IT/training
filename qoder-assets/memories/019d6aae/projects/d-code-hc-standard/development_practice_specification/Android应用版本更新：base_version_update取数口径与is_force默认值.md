---
title: "Android应用版本更新：base_version_update取数口径与is_force默认值"
usage_scenario:
    - "发布新版本后同步更新后台版本表"
    - "App未弹出更新提示时检查记录与下载地址"
    - "判定契约不匹配是否因App落后引起"
keywords:
    - "版本更新表取数规则"
    - "is_force默认值1"
    - "version_no纯整数"
    - "强制更新判定"
    - "download_url可下载校验"
source: "auto"
---

APP 版本更新提示由 base_version_update 表驱动，PC 页「版本更新」维护 version_no/version_name/update_reason/download_url，APP 调 POST /api/base/getVersion，本地 versionCode 小于记录值才弹窗。

2026-10-28 实查库口径：接口用 order by create_time desc limit 1 取「最新一条」，不看 version_no 大小，补录早时间戳的记录不生效；version_no 是 varchar(20) 但必须纯整数，填 1.1.91 会让 APP 端 Integer.parseInt 抛异常被 catch 掉、静默跳过检查（id 64/88/89/90 就是这种无效脏数据）；is_force 为 int 且 DEFAULT 1，页面无「是否强制」字段，新增省略该列即落 1（强制更新，只给立即更新并屏蔽返回键），要非强制须手工置 0，不会出现 NULL；update_reason 为 varchar(100) 单行展示，为空时兜底“对已知问题进行了修复”。

排查顺序：先查最新记录 version_no 是否纯整数、download_url 是否可下载（nginx 以 application/vnd.android.package-archive 直出），再确认现场设备包版本。发布后务必核对 apk 是 release 还是 debug 产物。
