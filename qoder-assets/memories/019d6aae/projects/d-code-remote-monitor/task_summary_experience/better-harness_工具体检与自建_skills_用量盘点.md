---
title: "better-harness 工具体检与自建 skills 用量盘点"
usage_scenario:
    - "评估某 AI 插件工具在本机是否有可用数据支撑"
    - "盘点自建 skills 的真实使用情况"
    - "解读 observed=0 类覆盖率不足的诊断输出"
keywords:
    - "better-harness用量核对"
    - "skill使用频次扫描"
    - "transcript本地归档"
    - "unobserved不等于unused"
    - "pdf重复注册"
source: "auto"
---

## Task description
- Core requirement: 回答两个问题 —— ① better-harness 插件对本机环境到底有没有用；② 用户自建的 skills 里哪些是不常用的。
- Task background: 用户级 skills 位于 C:\Users\Administrator\.qoder\skills\（8 个自建 + pdf 副本），第三方/打包 skills 位于 C:\Users\Administrator\.agents\skills\（约 29 个）；better-harness CLI 在 ~/.qoder/plugins/cache/qoder-bundler/better-harness/scripts/better-harness.mjs。此前记忆结论认为 Qoder 会话记录只存服务端、本地无 transcript，需再次核实。

## Execution process
1. 定位并验证 CLI 存在（node --version + 列 scripts 目录），确认可执行。
2. 列出 .agents\skills 与 .qoder\skills 全量目录名与创建时间，划分"自建 vs 打包"边界：自建判定为 code-comment-style、code-conciseness-check、decision-driven-coding、frontend-interaction-design、frontend-visual-craft、hand-typing-training、project-map、work-log 八个，pdf 为复制品。
3. 运行 harness evidence-bundle（--platform qoder --depth normal，窗口 2026-09-26~2026-10-26），首次因 PowerShell `>` 重定向写出 UTF-16LE+BOM 导致 JSON.parse 失败，改用 [IO.File]::WriteAllText + UTF8Encoding($false) 后成功。
4. bundle 返回 status=failed：session lane eligibleSessions=0、projectHarness lane unavailable，按 skill 自身规则停止渲染报告，未伪造结论。
5. 改跑 harness checkup --phase scan 取到有效数据：configured skills 37 / observed 0 / unobserved 37，全部 unobserved 原因为 insufficient-session-coverage（minimumSessions=5）；lint 23 条（1 warning：docx SKILL.md 590 行超硬上限；22 advisory 含 algorithmic-art/claude-api/doc-coauthoring/mcp-builder 缺 references）；3 个 MCP 以 npx/uvx 调用且未锁版本（fetch、Figma、mysql）。
6. 关键纠偏：发现本机确有本地会话归档 C:\Users\Administrator\.qoder\projects\<项目>\transcript\*.jsonl（163 个文件、61.3MB），但最新时间戳停在 2026-09-11 16:36，故 harness 的 observed=0 是"覆盖率不足"而非"未被使用"。
7. 写 _temp\_skill_usage_scan.mjs 全量扫描所有项目 transcript 统计各 skill 命中数与去重会话数：hand-typing-training 129 次/6 会话（唯一高频）、code-conciseness-check 5 次/2 会话、decision-driven-coding 3 次/2 会话，其余 5 个（含 work-log、project-map、code-comment-style、两个 frontend-*）均 0 命中。
8. 对低命中的 decision-driven-coding 与 code-conciseness-check 逐行取证上下文，确认其命中全部来自"能力清单罗列/是否合并的讨论"，非真实调用；结合记忆库中两条 frontend-* skill 建设记录自带的"尚未试跑"结论交叉印证。
9. 核查 pdf 重复：.qoder\skills\pdf 与 .agents\skills\pdf 各 13 个文件、SKILL.md MD5 完全一致（08B131C7...），判定为重复注册资产。
10. 按用户级约定把三个坑写入 work-log\03-踩坑手册.md 的"基础设施 / 部署"分区（H3 标题 + 问题/根因/触发条件/做法四行）。
11. 清理自造的阻塞产物：删除首轮失败留下的 canvas.json（它会触发 CANVAS_OUTPUT_EXISTS 使后续 run 直接不可用），保留 bundle.json 与 checkup.json 作为证据。

## Related files
- /d:/code/remote-monitor/_temp/_skill_usage_scan.mjs — 跨项目 transcript 扫描计数脚本
- /d:/code/remote-monitor/.qoder/better-harness-runs/20261026-skills-audit/checkup.json — checkup 扫描结果留档
- /d:/code/remote-monitor/.qoder/better-harness-runs/20261026-skills-audit/bundle.json — evidence-bundle 结果留档
- /d:/code/remote-monitor/work-log/03-踩坑手册.md — 追加三条工具链踩坑条目

## Notes
- harness 报 observed=0/unobserved=all 时不能直接读成"这些 skill 没人用"，必须先查它实际读取的会话源路径与时间覆盖范围；本机 transcript 只到 2026-09-11，晚于该日期建成的 skill 天然是零覆盖盲区。
- 判断 skill 是否真被用过，光看关键词命中数会误判：命中可能只是 skill 清单罗列或讨论，必须抽取命中行上下文确认是否存在显式调用痕迹。
- 同一 run 目录重复传 --canvas-out 会让 lead lane 整体不可用，重跑前要先清掉上轮的 canvas.json。

## Task overview
已闭环（未生成渲染报告，原因符合 skill 自身停止规则）：Q1 结论为 better-harness 的 lint 与资产清单部分有真实价值（查出 docx 超行数硬上限、4 个长 skill 缺 references、3 个 MCP 未锁版本），但用量核对维度在当前环境下拿不到数据，不能用它判定 skill 闲置；Q2 结论为 hand-typing-training 是唯一有强使用证据的 skill，work-log/code-comment-style 靠记忆库与数据文件证明在用，project-map 属一次性使用后不再触发，decision-driven-coding 与 code-conciseness-check 更像"已建未用"，frontend-interaction-design 与 frontend-visual-craft 建成后从未用真实页面跑过。附带发现 pdf 双份重复注册、.agents\skills 大量打包 skill 稀释触发面。后续动作（删 pdf 副本、启用原始会话源授权、锁定 MCP 版本）均以选项形式交用户决定，未擅自执行。
