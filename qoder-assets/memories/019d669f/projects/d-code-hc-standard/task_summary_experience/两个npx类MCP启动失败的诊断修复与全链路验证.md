---
title: "两个npx类MCP启动失败的诊断修复与全链路验证"
usage_scenario:
    - "回顾MCP红色不可用的诊断修复全过程"
    - "Figma MCP配置token后验证可用性"
    - "CallMcpTool报serverName未找到时排查"
keywords:
    - "MCP启动失败诊断"
    - "npx缓存损坏"
    - "serverName元数据"
    - "Figma token验证"
    - "Figma社区移动端资源"
    - "get_figma_data实测"
    - "fileKey寻址"
source: "auto"
---

注：在原「两个npx类MCP启动失败的诊断修复与全链路验证」基础上，补充后续 Figma 社区资源获取与端到端实测环节。

## Task description
- Core requirement：用户为 Figma 新手，连续追问"哪里可以看社区手机（移动端设计）"→"你看看有没有"→复制后弹窗该选哪项→"复制之后必须给链接吗，你不能自己获取"，最终提供文件链接要求实测读取，目标是把 Figma MCP 从配置可用推进到真正读通设计稿。
- Task background：Framelink MCP for Figma 已在 mcp.json 配好 --cache 与有效 token；本机账号 handle 2865215722；待验证的是社区资源定位与 API 实际读取链路。

## Execution process
1. 澄清用户截图中的左侧 Tools/Suggested 面板是编辑器插件面板，不含社区；给出两种进入社区方式：浏览器直开 figma.com/community，或客户端左上角菜单 → Back to files → 左栏 Community。
2. WebSearch 检索并确认真实存在的移动端资源：Material 3 Design Kit（file/1035203688168086460）、官方移动端分类 community/mobile-apps、iPhone UI Kits 分类 community/ui-kits/iphone、中文移动端 UI Kit（file/1267400479316951212）。
3. WebFetch 抓取 community/mobile-apps 与 community/ui-kits 均返回 code 60001 / 403（Figma 对爬虫防护），改用 WebSearch 途径获取具体资源清单。
4. 解释 Apple 官方 iOS and iPadOS 26 UI Kit 弹窗两选项差异："Use UI Kit in a new file"仅新建空文件并接入素材库；"Make a copy of this file"完整复制文件到本人账号，测试读取需选后者。
5. 解释 Figma REST API 按 fileKey 寻址、官方无"列出账号所有文件"接口，故每次读取须由用户提供含 fileKey 的链接；同一会话内可复用该 fileKey。
6. 实测：解析用户链接得 fileKey 为 3XEac6rNlnL7y6dk5CTBxs、节点 221:56229，以原始服务名调用 get_figma_data 成功返回文件名 "iOS and iPadOS 26 (Community)" 及 CANVAS "Cover" 结构，全链路打通。

## Related files
- c:/Users/Administrator/.qoder/mcp.json（沿用上一环节的 --cache 与 token 配置，本环节未再改动）

## Notes
- WebFetch 直接抓 Figma 社区页面会被 403 拦截，检索社区具体资源应走搜索引擎而非抓取社区页。
- Figma API 只能按 fileKey 读取单个文件，无法枚举账号下文件列表；因此"自动获取用户复制了什么文件"不可行，必须由用户提供链接。
- 社区公开可浏览不等于 API 可读，仍需先 Make a copy 到本人账号；链接中的 node-id 决定读到哪个节点，本次 221-56229 恰为封面 Cover 节点。

## Task overview
已完成端到端验证：MCP 进程正常、token 生效、账号对文件有权限、get_figma_data 成功返回节点结构与图片引用；同时向用户交付了经确认存在的 Figma 移动端设计资源清单与使用流程。遗留：用户当前链接指向封面节点，如需读取具体界面须换带其他 node-id 的链接。
