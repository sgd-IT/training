---
title: "Qoder 本地会话转录位置、时间断层与 skills 双根重复注册"
usage_scenario:
    - "统计历史对话或工具使用频次时选择数据源"
    - "排查本地会话记录缺失或时间断点"
    - "核查 skill 是否被重复注册或多根共存"
keywords:
    - "qoder projects transcript jsonl"
    - "会话归档时间断层"
    - "globalStorage空目录"
    - "skills双根扫描"
    - "同名skill重复注册"
source: "auto"
---

Qoder 在本机的会话与配置分多处存放，做用量/历史类分析时必须按下列边界取数，单看一处会得出错误结论：

1. 本地会话转录：`C:\Users\Administrator\.qoder\projects\<项目目录名>\transcript\<uuid>.jsonl`，一行一个事件，type 可为 session_meta / progress / user_message / assistant_message / assistant_thinking / tool_use / tool_result，其中 session_id 与 uuid 等于文件名。按项目分目录（如 d-code-remote-monitor、D-Code-hc-standard、D-Tool-Qoder、undefined）。
2. 该归档存在硬性时间断层：全机 163 个 jsonl、共 61.3MB，最新一条只到 2026-09-11 16:36，此后的对话不再写本地 jsonl。因此任何基于 transcript 的统计只能覆盖 2026-09-11 之前的行为，晚于此日创建的资产必然呈现"零命中"，不能据此判定未被使用。
3. 另一处会话库 `%APPDATA%\Qoder\User\globalStorage\aicoding.aicoding-agent\` 只有一个空的 schedule 目录，不含对话数据；`%APPDATA%\Qoder\SharedClientCache\cache\db\local.db` 存的是会话元数据表，消息正文加密不可直读。三者不是同一份数据，勿互相替代。
4. skill 根的可见性由 IDE 扫描决定：`.qoder\skills` 与 `.agents\skills` 两个根会被同时扫进可用列表，同名 skill 在两处都实体存在即构成重复注册（例：pdf 两侧各 13 文件、SKILL.md MD5 一致），只会增加触发歧义，清理时保留本体一侧。
