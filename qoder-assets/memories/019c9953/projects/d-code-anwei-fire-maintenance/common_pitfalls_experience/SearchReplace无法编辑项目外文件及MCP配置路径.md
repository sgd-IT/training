---
title: "SearchReplace无法编辑项目外文件及MCP配置路径"
usage_scenario:
    - "尝试用SearchReplace修改Qoder/MCP全局配置文件失败"
    - "SearchReplace报错can not edit the file outside the projects"
keywords:
    - "SearchReplace"
    - "项目外文件"
    - "MCP配置路径"
    - "Qoder"
---

SearchReplace工具无法编辑项目目录外的文件。MCP配置文件位于`C:\Users\Administrator\AppData\Roaming\Qoder\SharedClientCache\mcp.json`，该路径不在当前项目`d:\code\anwei-fire-maintenance`内，因此调用失败。必须手动编辑此绝对路径文件并重启MCP服务才能生效。（来源：SearchReplace）
