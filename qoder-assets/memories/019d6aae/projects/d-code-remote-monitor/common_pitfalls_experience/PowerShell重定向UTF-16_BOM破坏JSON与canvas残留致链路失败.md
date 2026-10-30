---
title: "PowerShell重定向UTF-16 BOM破坏JSON与canvas残留致链路失败"
usage_scenario:
    - "PowerShell下把命令输出落盘后供程序解析"
    - "重跑带产物存在性检查的CLI前清理残留"
    - "诊断证据链路整体failed的伪故障"
keywords:
    - "PowerShell重定向UTF-16 BOM"
    - "JSON.parse失败"
    - "WriteAllText无BOM"
    - "CANVAS_OUTPUT_EXISTS"
    - "harness evidence-bundle"
source: "auto"
---

Bug class: CLI 输出的 JSON 证据文件无法解析，以及同一 run 目录重跑时整条分析链路被判不可用。

Root cause: 两点独立机制。① Windows PowerShell 5.1 的 `>` / `Out-File` 默认把管道内容写成 UTF-16LE 带 BOM，Node 侧 `JSON.parse(fs.readFileSync(...,'utf8'))` 会在第一个字符直接抛错，看起来像 CLI 输出了坏 JSON，其实是落盘编码问题；② better-harness 的 `harness evidence-bundle --canvas-out <path>` 在该 path 已存在时报 `CANVAS_OUTPUT_EXISTS`，导致 projectHarness lane 变 unavailable，进而使整个 bundle 的 status=failed、后续渲染步骤被规则强制中止。

Fix pattern: 落盘改为在内存收集 stdout 后用 `[IO.File]::WriteAllText($path, ($out -join "`n"), (New-Object System.Text.UTF8Encoding($false)))` 写无 BOM 的 UTF-8；重跑前先删除上一轮生成的 canvas.json（或直接换新 run 目录），再传 --canvas-out。

Reusable lesson: Don't 用 PowerShell 的 `>` 重定向保存需要被 Node/其他程序解析的文本输出，因为会得到 UTF-16LE+BOM 而使 JSON.parse 失败；instead 用 .NET WriteAllText 显式指定 UTF8Encoding($false)。同理，Don't 在复用同一 run 目录时保留旧的 --canvas-out 目标文件，因为单个可选产物残留会让整条证据链路失效并被误读成"工具不可用"。适用于 PowerShell 5.1 下调用 node CLI 并把输出落盘再解析的场景，以及任何带 --canvas-out/--output 幂等检查的 CLI 重跑；不适用于 pwsh 7+（默认已是无 BOM UTF-8）与纯内存消费 stdout 的调用。
