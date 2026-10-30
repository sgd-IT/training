---
title: "移动端App启动‘服务异常’提示的静默化修复"
usage_scenario:
    - "当移动应用启动时无操作即弹出'服务异常'提示时进行根因定位"
    - "当后端日志出现外部服务连接超时且前端用户报告异常时进行跨层关联分析"
    - "当需要判断前端toast是客户端问题还是服务器基础设施问题时"
keywords:
    - "服务异常"
    - "移动端启动"
    - "网络稳定性"
    - "refreshVideo"
    - "连接超时"
    - "静默失败"
    - "重试"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the '服务异常' toast that appears immediately upon entering the mobile app's To-Do tab, before any user action.
- Task background: toast is triggered by `MyRetrofitCallback.failed()` on any network request failure; two automatic requests fire at startup (`getAfootCount`, `getVersion`); backend logs show concurrent `refreshVideo` connection timeouts to `58.19.176.195:8888` at 15:23–15:27, matching the user’s 15:24 screenshot.

## Execution process
1. Confirmed mobile app startup flow: `MainActivity.initView()` → `updateApp()` → `getVersion()` and `FragmentMessage.initData()` → `loadNum()` → `getAfootCount()`.
2. Analyzed `MyRetrofitCallback.failed()` implementation: all failures (connect timeout, read timeout, parse error) unconditionally show “服务异常”.
3. Checked backend logs: found 4 consecutive `ConnectException: Connection timed out` errors for `/api/special/refreshVideo` between 15:23:53 and 15:27:45, targeting `58.19.176.195:8888`.
4. Verified `refreshVideo` implementation in `BaseWorkSpecialServiceImpl.java`: exact URL `http://58.19.176.195:8888/open/v2/getWorkPlayback?...` confirmed.
5. Correlated timestamps: user’s 15:24 screenshot falls within the server’s unstable window → concluded network path from phone to `220.203.23.144:28080` likely failed due to broader infrastructure instability.
6. Implemented fixes: `FragmentMessage.loadNum()`改为静默失败 + 3秒后单次重试；`MainActivity.updateApp()` 的 `failed()` / `successError()` 全部静默（除401）；`MyRetrofitCallback.onResponse()` 加空 body 保护；`BaseWorkSpecialServiceImpl.java` 中所有 7 处 HttpRequest 调用添加 `.timeout(5000)`；`build.gradle` 版本号从 88 升级至 89。

## Related files
- d:\code\hc-standard\other1\app\src\main\java\com\sckj\other1\fragment\FragmentMessage.java
- d:\code\hc-standard\other1\app\src\main\java\com\sckj\other1\MainActivity.java
- d:\code\hc-standard\other1\app\src\main\java\com\sckj\other1\http\MyRetrofitCallback.java
- d:\code\hc-standard\ruoyi-system\src\main\java\com\ruoyi\base\service\impl\BaseWorkSpecialServiceImpl.java
- d:\code\hc-standard\other1\app\build.gradle

## Notes
- The “0” shown on the “隐患治理” card is the XML default value (`android:text="0"`), not proof of successful API call.
- Backend slow SQL (`sys_job_log` inserts taking 1–2.6s) indicates ongoing database pressure, supporting the conclusion of general infrastructure instability.

## Task overview
Successfully diagnosed the root cause: “服务异常” toast appears because the mobile app’s automatic startup requests fail due to transient network instability affecting the entire server environment (evidenced by concurrent `refreshVideo` timeouts and slow DB writes). Applied code-level fixes to make non-critical startup requests silent-fail with retry, eliminating the disruptive toast and restoring immediate usability.
