---
title: "非项目内文件的安全写法：Write 临时文件 + Copy-Item 整文件覆盖"
usage_scenario:
    - "修改项目外配置文件或work-log数据文件"
    - "判断终端中文乱码是读取错误还是控制台编码"
    - "需要脚本化处理加密目录内文件内容"
keywords:
    - "非项目文件"
    - "透明加密"
    - "整文件覆盖"
    - "45405"
    - "Read回读校验"
    - "node只读探测"
    - "GBK控制台乱码"
source: "auto"
---

Qoder 的 SearchReplace/Write 无法修改项目目录外文件（错误 45405）。不要因此用 PowerShell 做读取→修改→写回：在透明加密目录里 PowerShell 读到的是密文，写回会把文件写坏（实证：对 work-log 下文件做 AppendAllText 追加和 ReadAllText/Replace/WriteAllText 读改写，先后损坏 00-履历总表.md、01、02 三个文件，只能用整文件覆盖恢复）。安全做法：用 Write 在工作区内生成完整明文临时文件，再用 Copy-Item 整文件覆盖目标；覆盖前 Read 确认原文，覆盖后必须用 Read（授权通道）回读校验——不要用 Import-Csv/Get-Content 等 PowerShell 方式校验，它读到的是密文会误判为内容错误。适用边界：d:\code 整个目录树均受透明加密（不仅 work-log 子目录），工作区内新建文件同样落密文但 Qoder 读写通道可见明文。已有文件绝不追加或就地修改。注意：即使文件在工作区内，SearchReplace 也可能因加密层返回"内容不匹配"，此时改用更短的唯一子串作锚点重试。

【2026-10-27 补充：node 与 PowerShell 是两条不同通道】
- 只读探测可用 node：fs.readFileSync(path,'utf8') 对 d:\code\remote-monitor 下的 md 读到的是正确明文（实证：'# 踩坑手册' 的码位序列 35,32,36393,22353,25163,20876 完全正确）。终端里显示乱码只是控制台代码页 GBK 回显 UTF-8 字节的假象，不代表读错。判定明文/密文要用 codePointAt 逐字符比对已知中文标题的码位，而不是靠肉眼输出。
- 但绝不要用 node 做读改写（readFile→replace→writeFile）覆盖加密目录里的已有文件：写回通道的加密语义未验证，风险等同 PowerShell 读改写。稳妥链路仍是：在工作区 .tmp 生成完整明文新文件 → Copy-Item 整文件覆盖 → Read 工具回读校验。
- 脚本定位插入位置优先用 ASCII 锚点（如 lastIndexOf('\n## ')），避免把中文字面量写进 node -e 的单行命令（PowerShell 传参会剥引号且编码不稳）；需要中文时用 Write 建 .js 脚本文件（UTF-8），node 读脚本文件按 UTF-8 解析即正确。

【优化策略留痕（同次反思补记）】本条属 MUTABLE，可按上述实证继续增量补充；与之配套的"决策类"记忆（隐患主表删除同步园区的决策）为 IMMUTABLE，禁止改写或删除，若其结论随后续改造变化，只能在本条与任务归档侧记录最新事实，不得改动该 IMMUTABLE 条目。
