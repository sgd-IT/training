---
title: "本机默认 npm 缓存的 npx 安装损坏需用 --cache 指向 npm-cache-new"
usage_scenario:
    - "排查 MCP 显示红色不可用"
    - "MCP 启动报 ERR_MODULE_NOT_FOUND"
    - "确认本机 MCP 配置的的用户级文件落点与作用范围"
keywords:
    - "npx cache corruption"
    - "ERR_MODULE_NOT_FOUND"
    - "npm-cache-new"
    - "mcp.json 修复"
    - "MCP 启动失败"
    - "用户级mcp.json落点"
    - "MCP配置作用范围"
source: "auto"
---

本机(Windows)用户级 C:\Users\Administrator\.qoder\mcp.json 中,默认 npm 缓存 C:\Users\Administrator\AppData\Local\npm-cache 的 _npx 缓存存在损坏:部分依赖包(zod、await-to-js 等)只剩残骸目录,package.json/index.js 入口文件缺失,导致未配 --cache 的 npx 类 MCP(sequential-thinking、Framelink MCP for Figma)启动即报 ERR_MODULE_NOT_FOUND、客户端显示红色不可用。修复:在两个 MCP 的 args 头部加 "--cache C:\Users\Administrator\AppData\Local\npm-cache-new"(与其他可用 MCP 一致),实测两者均可正常启动。uvx 类 MCP(fetch)不经过 npm 缓存不受影响。今后新增 npx 类 MCP 时应默认带上该 --cache 参数。
