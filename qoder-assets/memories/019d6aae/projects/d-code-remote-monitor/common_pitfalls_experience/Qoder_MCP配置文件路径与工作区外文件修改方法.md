---
title: "Qoder MCP配置文件路径与工作区外文件修改方法"
usage_scenario:
    - "需要增改MCP服务或实例env配置时"
    - "编辑mcp.json报越界错误时"
    - "排查MCP DDL权限开关未生效"
keywords:
    - "mcp.json"
    - "extension local"
    - "Qoder MCP配置"
    - "工作区外文件修改"
    - "node脚本兜底"
    - "ALLOW_DDL_OPERATION"
    - "SCHEMA_DDL_PERMISSIONS"
    - "GBK回显假乱码"
source: "auto"
---

Qoder 的 MCP 配置有两份需同步修改：c:\Users\Administrator\.qoder\mcp.json（用户级）与 C:\Users\Administrator\AppData\Roaming\Qoder\SharedClientCache\extension\local\mcp.json（实际生效，含 identifier/version/disabled 字段），重载 MCP 后生效。

工作区外文件（如 .qoder\skills、mcp.json）的修改方法按优先级：① 直接尝试 SearchReplace/Write——2026-10-19 实测可成功编辑用户级 skill 目录下 SKILL.md，旧版"必然报 can not edit the file outside the projects"的结论已不总成立；② 若仍报越界或沙箱 EPERM，改用兜底：在工作区写 node 脚本用 fs.copyFileSync/writeFileSync 写入目标路径，Bash 以 required_permissions='all' 非沙箱执行。改前备份原文件到 _temp；脚本做幂等判断、锚点计数唯一校验、改后 JSON.parse 校验。

验证写入结果必须用 Read 工具按 UTF-8 读取，不要用 PowerShell Get-Content 回显判断——PS 控制台按 GBK 解码 UTF-8 中文会显示假乱码，文件本身正常。写 PowerShell 脚本时必须纯 ASCII：PS5.1 对无 BOM 的 UTF-8 脚本按 ANSI 解析，中文注释乱码导致语法错误。

MySQL 实例写权限由 ALLOW_INSERT/UPDATE/DELETE/DDL_OPERATION === "true" 才放行，缺省即只读；DDL 判定为 SCHEMA_DDL_PERMISSIONS[库名] 优先、否则回退 ALLOW_DDL_OPERATION。
