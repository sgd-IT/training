---
title: "透明加密环境下IDE写文件对命令行工具呈密文需shell改写"
usage_scenario:
    - "命令行读取IDE生成文件出现乱码或报错"
    - "IDE大量无法解析符号但代码与编译产物均正常"
    - "给mysql等命令行工具准备SQL脚本文件"
keywords:
    - "透明加密"
    - "密文文件"
    - "DLP白名单进程"
    - "IDE无法解析符号"
    - "Invalidate Caches"
    - "mysql source报错"
    - "PowerShell写文件"
    - "UTF8无BOM"
source: "auto"
---

## Usage Scenario
开发机装有透明加密软件时，需要让命令行工具（如 mysql.exe 执行 source、其他非白名单进程）读取由 IDE/编辑器生成的文件（SQL 脚本、数据文件）的场景。

## Usage Method
IDE 进程（含文件编辑工具）写入磁盘的文件实际存储为密文，外部进程读取时报错或乱码（如 mysql 报 "ASCII '\0' appeared in the statement"）；而 PowerShell 等 shell 进程写入的文件是明文、可被正常读取。因此给命令行工具消费的文件应由 shell 直接写入：[IO.File]::WriteAllText/AppendAllText + [Text.UTF8Encoding]::new($false)（无 BOM），单条命令超长时按 ≤400 字符内容分段追加，完成后先校验字节再执行。

## Notes
校验文件是否明文：读前几字节，正常 SQL 文本应以 ASCII 十六进制（如 2D 2D 20 即 "-- "）开头，出现其他高位乱码字节即为密文；PowerShell 显示外部程序 UTF-8 中文输出前先设 [Console]::OutputEncoding=[Text.UTF8Encoding]::new($false)，否则仅显示乱码（不影响文件本身）。
