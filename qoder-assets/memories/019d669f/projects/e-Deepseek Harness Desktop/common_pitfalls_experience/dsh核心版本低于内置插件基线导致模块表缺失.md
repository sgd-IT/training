---
title: "dsh核心版本低于内置插件基线导致模块表缺失"
usage_scenario:
    - "桌面端启动页'Failed to load plugins'报错排查"
    - "dsh核心版本与内置插件基线不匹配诊断"
    - "模块表缺失错误修复"
keywords:
    - "dsh核心版本"
    - "内置插件基线"
    - "模块表缺失"
    - "version-recommend.json"
    - ".store.dat"
source: "auto"
---

Bug class: deepseek-harness-desktop 启动报 "Failed to load plugins failed to import loader entry 80ba5156 (dsh-tauri-model-config): client-modules: require("@deepseek-ai/dsh-client-store") missed the module table"
Root cause: 应用桌面端 0.15.8 自带的内置插件（如 resources/node_modules/dsh-tauri-model-config）是按 dsh 0.1.5-rc.2 编译的，其客户端 bundle 中 require 了 "@deepseek-ai/dsh-client-store"；而用户机器上运行的核心引擎仍是旧版 dsh 0.1.1-rc.2，其模块表种子词（seed word）中不包含该模块。
Fix pattern: 通过检查 resources/version-recommend.json 文件确认应用要求的 dsh 基线版本（"dsh": "0.1.5-rc.2"），再比对 .store.dat 中的 dsh_pkg_tag 字段，定位版本差异；然后从 GitHub releases 下载对应版本的 deepseek-harness-pkg-windows.zip 包，解压到 APPDATA\io.github.hairyf.deepseek-harness-desktop\dependencies\ 目录下，最后通过设置 → 核心面板切换版本或手动修改 .store.dat 的 dsh_pkg_tag 和 active_core 字段来激活新核心。
Reusable lesson: Don't assume desktop app upgrade automatically upgrades its bundled dsh core because the core is downloaded separately; always verify the core version in .store.dat against version-recommend.json. Applies when troubleshooting plugin loading failures or missing seed words in the client module table; does not apply to issues with the desktop app's own UI or process management.
