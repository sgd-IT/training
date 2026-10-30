---
title: "Qoder中Oracle Java扩展DLL兼容性问题"
usage_scenario:
    - "Qoder中启用oracle.oracle-java扩展后报nbexec64.dll加载失败"
    - "Java语言功能异常且日志提示DLL相关错误"
    - "需在Qoder中安全启用Java开发支持"
keywords:
    - "Qoder"
    - "Oracle Java扩展"
    - "nbexec64.dll"
    - "Extension Pack for Java"
source: "auto"
---

Oracle Java 扩展（`oracle.oracle-java`）在 Qoder 环境中因尝试加载 64 位 `nbexec64.dll` 导致兼容性错误（架构不匹配或 DLL 损坏）。正确做法是：1. `Ctrl+Shift+X` 打开扩展面板 → 搜索并禁用/卸载 `oracle.oracle-java`；2. 搜索安装 `Extension Pack for Java`（发布者：Microsoft）；3. `Ctrl+Shift+P` → 执行 `Developer: Reload Window` 重载窗口。（来源：Qoder 扩展管理）
