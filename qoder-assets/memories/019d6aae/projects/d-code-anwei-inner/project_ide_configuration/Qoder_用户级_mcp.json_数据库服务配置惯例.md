---
title: "Qoder 用户级 mcp.json 数据库服务配置惯例"
usage_scenario:
    - "为某个项目新增数据库 MCP 服务条目"
    - "需要修改工作区外的 Qoder 配置文件"
    - "MCP 服务改动后不生效需排查加载时机"
keywords:
    - "Qoder MCP 配置文件"
    - "mcp.json 路径"
    - "mcp-server-mysql"
    - "工作区外一次性脚本回写"
    - "disabled 开关"
source: "auto"
---

## Usage Scenario
Description: 需要修改位于当前工作区之外的配置文件（如用户级/全局配置、其他目录下的 JSON 或 YAML）时，选择写入手段。
Examples: 往用户级 mcp.json 里新增服务条目；修改全局设置文件。

## Usage Method
SearchReplace / Write：只能作用于工作区内路径，对工作区外文件直接调用会被沙箱拒绝。可行做法是在工作区内创建一个一次性脚本（读取原文件、解析结构、插入或修改目标节点、按原有缩进风格序列化回写），以需用户批准的越权许可方式执行该脚本完成写入，随后删除脚本，并重新读取目标文件的改动区间做校验。

## Notes
- 直接对工作区外路径发起编辑类工具调用 causes 写入被沙箱拒绝、任务卡住，must 改走「工作区内临时脚本 + 经批准执行 + 事后清理 + 回读校验」的路径。
- 用脚本重写整个配置文件时未保持原有格式化参数 causes 全文 diff 噪声甚至破坏他人手工排版，must 按原文件的缩进与换行约定回写并在完成后抽查改动区间。
