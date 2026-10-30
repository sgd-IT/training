---
title: "deepseek-harness-desktop 核心引擎升级修复插件加载失败"
usage_scenario:
    - "桌面端启动报插件模块表缺失"
    - "内置插件加载失败排查"
    - "升级 dsh 核心版本"
keywords:
    - "dsh-tauri-model-config"
    - "dsh-client-store"
    - "模块表 seed word"
    - "version-recommend"
    - "核心版本切换"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the "Failed to load plugins failed to import loader entry 80ba5156 (dsh-tauri-model-config): client-modules: require("@deepseek-ai/dsh-client-store") missed the module table" startup error.
- Task background: The desktop app version is 0.15.8, which bundles internal plugins compiled against dsh 0.1.5-rc.2; however, the user's machine was running an older dsh core (0.1.1-rc.2), whose module table did not include the required "@deepseek-ai/dsh-client-store" seed word.

## Execution process
1. Diagnosed the root cause by analyzing the plugin's client.cjs bundle (which requires "@deepseek-ai/dsh-client-store") and comparing it with the active core's module table (which lacked the seed word).
2. Confirmed the version mismatch by checking the .store.dat file (dsh_pkg_tag: dsh-0.1.1-rc.2) and the resources/version-recommend.json file ("dsh": "0.1.5-rc.2").
3. Downloaded the correct dsh core package (dsh-0.1.5-rc.2-34495473237, 42.5MB) from GitHub using the ghfast.top mirror for speed.
4. Verified the downloaded zip's SHA256 hash matched the official release.
5. Stopped the deepseek-harness-desktop application and its associated dsh node process.
6. Extracted the new core package into the dependencies directory and performed a directory swap to activate it.
7. Updated the .store.dat configuration file to record the new dsh_pkg_tag, dsh_pkg_commit, and active_core setting.
8. Restarted the application, which automatically re-linked all 11 internal plugins to the new core.

## Related files
- C:\Users\Administrator\AppData\Roaming\io.github.hairyf.deepseek-harness-desktop\.store.dat
- E:\Deepseek Harness Desktop\resources\version-recommend.json
- C:\Users\Administrator\AppData\Roaming\io.github.hairyf.deepseek-harness-desktop\dependencies\dsh

## Notes
- None

## Task overview
Successfully resolved the startup failure: upgraded the dsh core from 0.1.1-rc.2 to 0.1.5-rc.2, which added the missing "@deepseek-ai/dsh-client-store" seed word to the module table; the application now reports "healthy - 63/63 client modules ready" in its logs and loads all plugins without error.
