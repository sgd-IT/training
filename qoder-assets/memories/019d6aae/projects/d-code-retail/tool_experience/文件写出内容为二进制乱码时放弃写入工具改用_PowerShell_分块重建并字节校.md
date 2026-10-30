---
title: "文件写出内容为二进制乱码时放弃写入工具改用 PowerShell 分块重建并字节校验"
usage_scenario:
    - "写入脚本文件后内容变成乱码或二进制"
    - "换路径重写仍复现文件损坏"
    - "需要可靠生成文件并校验语法"
keywords:
    - "文件内容二进制乱码"
    - "ReadAllBytes >127 字节数校验"
    - "Set-Content Add-Content 分块写入"
    - "PSParser Tokenize 语法校验"
    - "双引号字符串规避转义"
source: "auto"
---

## Usage Scenario / 使用场景
用文件写入工具（Write/SearchReplace）保存脚本类文件后，读取发现内容变成乱码或二进制：文件长度约为预期的 2 倍、非 ASCII 字节数异常偏大、开头字节是随机二进制而非文本或 BOM、语法解析器报大量错误；换新路径重写或删除后重建仍会复现，但同目录下用命令行写入的文件是正常的。

## Usage Method / 使用方法
放弃文件写入工具，改用 PowerShell 命令分块生成文件：Set-Content 写首块、Add-Content 追加后续块，单条命令控制在 500 字符以内；内容优先使用双引号字符串以规避单引号转义，长列表可拆成相邻字符串用 + 连接（保持语法上仍是一行）。写完后先做字节校验（[IO.File]::ReadAllBytes 统计长度与 >127 字节数）和语法校验（PSParser::Tokenize 的错误数应为 0），再实跑一次验证。

## Notes
不要反复用文件工具试探同一路径，症状会在新路径复现，说明是写入通道本身的问题而非文件残留。
