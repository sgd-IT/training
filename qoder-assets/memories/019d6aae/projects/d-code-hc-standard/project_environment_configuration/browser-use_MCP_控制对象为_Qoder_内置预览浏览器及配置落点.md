---
title: "browser-use MCP 控制对象为 Qoder 内置预览浏览器及配置落点"
usage_scenario:
    - "需要操作或核验本地前端页面时"
    - "排查 MCP 服务未注册或找不到工具定义时"
    - "判断能否接管外部浏览器窗口时"
keywords:
    - "browser-use MCP"
    - "Qoder内置预览浏览器"
    - "mcp.json配置路径"
    - "remote-debugging-port"
    - "take_snapshot工具"
source: "auto"
---

Qoder 的 MCP 服务分两层配置：项目级 d:\code\hc-standard\.qoder\mcp.json（本工作区该文件为空），用户/扩展级 C:\Users\Administrator\AppData\Roaming\Qoder\SharedClientCache\extension\local\mcp.json；已注册服务的工具定义与元数据落在 SharedClientCache\projects\<workspace-slug>\mcps\<server>\tools\*.json 与 SERVER_METADATA.json。browser-use 服务未出现在上述任一份 mcp.json 中，属 Qoder 内置注册项，共 16 个工具（list_pages / select_page / navigate_page / take_snapshot / take_screenshot / click / fill / upload_file / handle_dialog / evaluate_script / list_console_messages / list_network_requests 等）。实测其控制对象是 Qoder IDE 内置预览浏览器（evaluate_script 返回的 UA 含 QoderIDE、Electron/42.2.0、Chrome/148），会复用其中已打开且 selected 的标签页，而非用户本机独立启动的 Chrome/Edge；后者需以 --remote-debugging-port 启动并由 MCP 走 --cdp-endpoint 才能接管。因此在该浏览器上的点击与跳转会直接改变用户屏幕所见页面。
