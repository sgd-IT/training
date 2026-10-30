---
title: "Figma MCP 读取设计稿的参数与前置条件要点"
usage_scenario:
    - "通过 MCP 读取 Figma 设计稿节点结构"
    - "盘点大型 UI Kit 的全部页面组件清单"
    - "用户询问能否自动获取其设计文件"
keywords:
    - "get_figma_data"
    - "fileKey解析"
    - "depth=1页面清单"
    - "node-id子树读取"
    - "Make a copy前提"
    - "download_figma_images"
source: "auto"
---

## Usage Scenario
Description: 需要读取 Figma 设计稿的节点结构、布局、样式或下载其中图片时；以及向用户解释"为什么不能自动拿到他的文件"，或需要盘点一份大型 UI Kit 究竟包含哪些页面/组件时。
Examples: 用户发来设计稿链接要求还原成页面代码；用户复制了社区 UI Kit 后要验证 MCP 是否读得通；用户想知道某份 Kit 都有哪些组件页可看。

## Usage Method
Framelink MCP for Figma：两个工具都必须显式传 fileKey，fileKey 只能从用户提供的文件链接解析，官方 API 没有"列出账号下文件"的能力，因此不可承诺自动获取用户最近复制的文件，需请用户给一次链接（同一会话内可复用该 fileKey）。链接 query 中的 node-id 会被解析为读取的起始节点，其连字符形式需转成冒号形式再传参；不传则读文件整体。被读文件必须属于或共享给 token 对应账号，社区资源虽公开可浏览，仍需在社区页点 Make a copy of this file 复制到本人账号后才可读（另一选项只新建空文件并接入素材库，没有可读内容）。

要枚举一份大型 UI Kit 的全部页面/画布清单（不深入任何单页内容）时，传 depth=1 且不带 nodeId 调用 get_figma_data，即可一次性拿到文件下所有 CANVAS 的名称与其节点 id（如"1-Button 按钮 #12:2479"），数据量小且完整，适合作为后续按页精读的索引；用户确认后再针对感兴趣的画布用其 node-id 只取子树。

## Notes
- 用网页抓取工具直接抓 Figma 社区分类页会返回 403 拦截导致拿不到资源清单，必须改用搜索引擎检索具体社区文件链接再逐个确认。
- 未指定起始节点也不限制 depth 就读取大型 UI Kit，会得到整棵巨型节点树而超出输出限制；要么带 node-id 只取子树，要么用 depth=1 只取顶层画布清单。
- 把 node-id 的连字符原样传参有不被识别的风险，必须按接口习惯转成冒号分隔形式。
- 社区文件的 node-id 常恰好指向封面（Cover）节点，读到的是封面装饰内容而非组件本体；判断"这份 Kit 有什么"应先用 depth=1 扫页面清单，不要凭单个封面节点下结论。
- 用户连续发多个不同 fileKey 的设计稿链接时，每次仍是独立单文件读取，不得跨文件复用上一轮的 fileKey 或节点 id；同一文件内的多个节点可复用其 fileKey 逐个精读。
