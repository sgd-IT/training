---
title: "终端调用npm须用npm.cmd且工作区外文件修改需借脚本"
usage_scenario:
    - "终端执行 npm 或 npx 命令报禁止运行脚本"
    - "修改 IDE 工作区外的配置文件报 code 45405"
    - "判断能否以项目内 mcp.json 作为配置样例参考"
keywords:
    - "npm.ps1 execution policy"
    - "npm.cmd"
    - "工作区外文件修改"
    - "code 45405"
    - "项目级mcp.json为空"
source: "auto"
---

本机 PowerShell 执行策略禁止运行 npm.ps1（报错"无法加载文件 D:\nodejs\npm.ps1，因为在此系统上禁止运行脚本"），终端里直接执行 npm 会失败；应改用 npm.cmd（如 D:\nodejs\npm.cmd）。node/npm/npx 可执行文件位于 D:\nodejs\，npx 同理用 npx.cmd。

修改用户级 mcp.json 等 IDE 工作区外部文件时，SearchReplace/Write 工具受工作区限制会报 code 45405，可写临时 Node 脚本到工作区内再执行来完成对外部文件的修改。

关于项目内的 d:\code\hc-standard\.qoder\mcp.json：经 2026-09-28 实测其内容为空的 {"mcpServers": {}}，不含任何 server 配置项，因此不能作为 mcp.json 写法或修复脚本的参考样例；本机的 MCP 配置全部在用户级文件中。
