---
title: "PowerShell写JSON去BOM与mysql CLI绝对路径兜底"
usage_scenario:
    - "PowerShell生成JSON配置后被第三方工具解析失败"
    - "旧版PowerShell写JSON报参数绑定错误需替代写法"
    - "MySQL CLI兜底通道需绝对路径定位"
keywords:
    - "UTF-8 BOM"
    - "ConvertTo-Json"
    - "Set-Content参数兼容"
    - "mysql.exe绝对路径"
    - "WriteAllText"
source: "auto"
---

## Usage Scenario
Windows PowerShell 下写配置文件或落地 JSON 时的编码与语法坑：JSON 被写入 BOM、旧版 Set-Content 参数绑定报错、含连字符库名语法报错。命令行中文输出乱码属显示层问题，另有专门记录。

## Usage Method
- 无 BOM 写 JSON：用 `New-Object System.Text.UTF8Encoding($false)` 配合 `[System.IO.File]::WriteAllText(path, content, encoding)`；禁用默认 `[System.Text.Encoding]::UTF8`（会加 EF BB BF，导致 Qoder 报 Unexpected end of JSON input）。
- 旧版 PowerShell（v5.x）中 `Set-Content -Encoding UTF8` 写含双引号/反斜杠的 JSON 会报 ParameterBindingException（该版本不支持此参数组合），且多行 here-string `@'...'@` 前后有多余空格或换行会解析失败。此时改用 `ConvertTo-Json -Compress` 生成紧凑字符串再 `Out-File -Encoding UTF8` 落盘：
  ```powershell
  $json = @{ key = "val\"ue"; pass = "p@ss#1" } | ConvertTo-Json -Compress
  $json | Out-File -FilePath 'config.json' -Encoding UTF8
  ```
  `ConvertTo-Json` 默认缩进输出，必须加 `-Compress`，否则多余空白导致下游解析异常。
- CLI 兜底通道绝对路径：`D:\Tool\mysql-8.0.46-winx64\bin\mysql.exe`。
- 含连字符库名（如 tgr-gz-vue）必须用单反引号包裹，否则报语法错误。

## Notes
- Out-File -Encoding UTF8 仍带 BOM，需要无 BOM 时只能用 UTF8Encoding($false) + WriteAllText
- 多库场景只查一个库就下结论，会把测试库结论当成正式库；须逐库分别只读比对列名/类型/默认值/注释四项
