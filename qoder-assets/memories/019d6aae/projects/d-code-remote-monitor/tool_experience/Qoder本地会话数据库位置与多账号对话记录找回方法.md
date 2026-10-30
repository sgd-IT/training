---
title: "Qoder本地会话数据库位置与多账号对话记录找回方法"
usage_scenario:
    - "找回切换账号后的历史对话记录"
    - "统计历史对话中的工具或技能使用频次"
    - "排查对话记录丢失或本地数据完整性"
keywords:
    - "会话记录找回"
    - "SQLite只读查询"
    - "node:sqlite"
    - "transcript明文jsonl"
    - "消息正文密文仅限local.db"
    - "多账号会话共存"
    - "SharedClientCache"
source: "auto"
---

## 使用场景
需要找回或核查 Qoder 历史对话记录（例如切换过账号、怀疑记录丢失），或需要统计历史行为频次（工具/技能使用情况）时。

## 使用方法
- **两份存储要分清，能力完全不同**：
  1. `%APPDATA%\Qoder\SharedClientCache\cache\db\local.db`（SQLite）：存会话元数据索引，核心表 chat_session（含 user_id、session_title、project_uri、project_name）、chat_message、chat_record。可用 Node 22 自带 node:sqlite（DatabaseSync + readOnly:true）只读查询，Qoder 运行期间也能安全访问。会话按 user_id 区分账号；即使当前登录其他账号，旧账号的历史会话仍完整保留在同一库中，重新登录原账号并打开同一项目即可在 UI 历史列表找回。此处消息正文为 base64 密文，直接可读的只有标题、账号、项目、消息计数等元数据。
  2. `C:\Users\<用户>\.qoder\projects\<项目目录名>\transcript\<uuid>.jsonl`：**明文**逐行事件流，type 可为 session_meta / progress / user_message / assistant_message / assistant_thinking / tool_use / tool_result，session_id 与 uuid 等于文件名。要做内容级检索或频次统计应读这里，用 Read/Grep 或 node 脚本均可，不必解密。
- `%APPDATA%\Qoder\User\globalStorage\aicoding.aicoding-agent\` 只有一个空 schedule 目录，不含对话数据，不要在此找会话。

## 注意事项
- 断言"对话正文本地读不到"之前必须先查 transcript 目录：该结论只对 local.db 成立，对 jsonl 不成立，误判会导致放弃可行的取证路径。
- transcript 存在时间断层（本机全量 163 个文件最新只到 2026-09-11 16:36），零命中不等于未发生，统计口径必须写明覆盖区间。
- 避免清理 Qoder 缓存或卸载重装导致会话丢失；重要时可先退出 Qoder 再整体备份 local.db 与 .qoder\projects。
