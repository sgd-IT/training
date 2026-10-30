---
title: "Android移动端安全措施签字修复与生产配置回切"
usage_scenario:
    - "When packaging an Android application for testing or release"
    - "When diagnosing and fixing persistent '安全措施未落实签字完成' errors despite UI showing completion"
    - "When ensuring production environment configurations are correctly set before deployment"
keywords:
    - "Android打包"
    - "安全措施签字"
    - "isChose"
    - "FragmentJobSlipMeasure"
    - "versionCode"
    - "生产环境地址"
source: "auto"
---

## Task description
- Core requirement: Fix the persistent '安全措施未落实签字完成' error and package the Android app for testing/release; ensure production environment configurations are correctly set before deployment.
- Task background: The frontend displays signatures as complete, but backend validation fails because `isChose` remains NULL when only `personFileId` is present. Additionally, development-time API/database addresses must be reverted to production ones before release.

## Execution process
1. Confirmed Android project configuration: AGP 8.2.0, compileSdk 36, signing config (other1.jks), SDK path, and gradlew.bat presence.
2. Provided two packaging methods: Android Studio GUI (Build → Build Bundle(s)/APK(s)) and command line (`gradlew.bat assembleRelease`/`assembleDebug`).
3. Diagnosed root cause: backend `selectWorkStepFinish` validation requires both `isChose` and `personFileId`; UI only shows signature image, so `isChose` stays null unless explicitly set.
4. Implemented Android-side fix in `FragmentJobSlipMeasure.java`: moved `isChose="1"` auto-fill logic into the `save()` method to cover both new signings and legacy data.
5. Updated version number in `app/build.gradle`: `versionCode` from 87 to 88, `versionName` from "1.1.87" to "1.1.88".
6. Verified production API address (`http://220.203.23.144:28080`) and database connection settings must be restored before production deployment.

## Related files
- d:/code/hc-standard/other1/app/src/main/java/com/sckj/other1/fragment/FragmentJobSlipMeasure.java
- d:/code/hc-standard/other1/app/build.gradle

## Notes
- None

## Task overview
Successfully packaged the Android app and fixed the '安全措施未落实签字完成' error. The fix ensures all signed safety measures persist `isChose="1"`, resolving the state mismatch between UI and backend. Version numbers were correctly incremented, and production environment addresses were confirmed.

## Notes
- 第 6 步"生产环境地址已确认"的结论后续已被推翻：三端地址核查一轮实读磁盘，移动端 HttpInterface.IP 生效行仍是本机联调地址、后端 application-druid.yml 的 master 生效行仍是开发库，两处均未回切；且出现过编辑器显示已改为生产地址但未按 Ctrl+S 落盘的情形（git hash-object 与 HEAD blob 一致、文件 mtime 未变）。凡引用本条做上线前核对，必须现读生效行并用读盘证据确认，不能据本条认为回切已完成。
- 打包口径补充：本项目历史上实际产出的都是 debug 包（构建命令 assembleDebug，产物 app-debug.apk），debug 与 release 共用同一份 other1.jks 签名故可互相覆盖安装；buildTypes.release 未挂 signingConfig，直接 assembleRelease 会产出不可安装的未签名包。
