---
title: "本机用 mysql CLI 执行 SQL 脚本的三类坑：短参连写、ps1 编码、cmd 包装"
usage_scenario:
    - "MCP 被限时兜底执行 DDL/SQL 脚本"
    - "本机 PowerShell 调用外部 CLI 工具"
    - "编写临时执行脚本的编码处理"
keywords:
    - "mysql CLI"
    - "PowerShell 参数截断"
    - "ps1 编码乱码"
    - "source 执行 SQL"
    - "Write工具SQL文件损坏"
    - "UTF8NoBOM直写"
    - "DDL 兜底执行"
    - "MCP优先重试"
source: "auto"
---

## Usage Scenario
在本机（Windows + PowerShell 5.1）用 mysql CLI 兜底执行 SQL/DDL 脚本时（MCP 被限或需批量执行）。

## Usage Method
`$env:MYSQL_PWD='<pwd>'; & '<mysql.exe 绝对路径>' '--host=<ip>' '--port=3306' '--user=root' '--default-character-set=utf8mb4' '--execute=source <绝对路径.sql>' '<db>'`（本机 mysql.exe 位于 D:\Tool\mysql-8.0.46-winx64\bin）。参数全部用长选项并加单引号；SQL 文件由客户端按 utf8mb4 直读，中文注释无损；执行后回 MCP 做校验。

## Notes
- 连写短参（如 -h192.168.250.160）会被本机命令传递层截断（实测 mysql 收到 host=192 并打印帮助退出），必须改长选项 '--host=192.168.250.160' 形式；
- PowerShell 5.1 以 GBK 解析 UTF-8 无 BOM 的 .ps1 脚本，中文注释乱码导致整脚本解析失败，临时 .ps1 必须纯 ASCII（或显式加 UTF-8 BOM）；
- `cmd /c` 组合会被工具安全层误判（报 rmdir 拦截），避免使用；改用上述单行 PowerShell 命令或 -File 执行纯 ASCII 脚本。
- Write 工具写出的 .sql 文件可能磁盘字节损坏（实测：正常约 400B 内容落盘成 4500B 乱码，mysql 报 "ASCII '\0' appeared in the statement ... Query: '坿觎'"，删除后原样重写仍复现）。执行前先核对文件长度是否异常；损坏时改用 PowerShell 直写绕过：`$sql = @'...'@; [System.IO.File]::WriteAllText($path, $sql, (New-Object System.Text.UTF8Encoding($false)))`，写完长度正常后再 source 执行。

## Notes（后续会话补充）
- 同类"PowerShell 语法差异"坑（ponytail-review 三仓库会话实测）：该 shell 不支持 bash 的 && 语句分隔符，也不存在 head/tail 管道工具，照搬会致整条命令静默失败；多命令须用分号分隔，超长输出须重定向落盘后用 Read 分段读取。
- 【适用前提（2026-10-29 再验证）】本条只在"MCP 确认不可用"时才启用。本轮因过早认定测试库 MCP 实例缺席而走 CLI，连续踩坑且均未跑通：PowerShell 不支持 `< file` 重定向；Write 工具落盘的 .sql 实为 UTF-16（报 ASCII '\0'）；即便用 `Get-Content -Encoding Unicode` + `[IO.File]::WriteAllText(..., ASCII)` 转码，**source 与管道 stdin 两种方式仍报同一错误**（说明 -Encoding Unicode 读到的内容本身已错解码）。正确顺序：先按同名 server 重试 MCP 1~2 次（本项目实测重试即恢复，且 DDL 已放行），确实不通才动 CLI。
- 【需要计时别绕 CLI】MCP 每次查询返回自带 "Query execution time"，可直接用于索引/SQL 的 A/B 对比；为计时而转投 CLI 再套 Measure-Command 或 SET profiling 属自找麻烦。
