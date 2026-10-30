---
title: "MCP配置两级落点与作用范围：用户级绑定Windows用户而非登录账号"
usage_scenario:
    - "判断切换登录账号后MCP配置是否保留"
    - "新增MCP时选择写入用户级还是项目级文件"
    - "解释某台机器上MCP对哪些项目生效"
keywords:
    - "mcp.json 两级配置"
    - "用户级配置作用范围"
    - "工作区mcp.json为空"
    - "Windows用户而非登录账号"
    - "换账号配置保留"
source: "auto"
---

Qoder 的 MCP 配置分两级，全部 MCP（sequential-thinking、fetch、Framelink MCP for Figma、各 MySQL）实际配置在用户级文件 C:\Users\Administrator\.qoder\mcp.json 中；本项目的工作区级文件 d:\code\hc-standard\.qoder\mcp.json 内容为空（{"mcpServers": {}}），即无任何项目专属 MCP，也不存在两级同名冲突。

作用范围关键结论：
- "用户级"绑定的是本机 Windows 登录用户（Administrator），不是 Qoder/IDE 的登录账号。Qoder 启动时直接读取该本地文件，因此切换 Qoder 登录账号后所有用户级 MCP 配置原样保留并继续可用。
- 用户级配置对本机所有项目/工作区通用，打开其他工程无需重新配置。
- 项目级配置随项目目录走，仅对该工作区生效（若随代码提交则团队成员可共用），适合只服务单个项目的 MCP。
- 会随账号变化而失效的是外部服务自身的凭证与资源归属（如 Figma 个人 access token 与其账号内的文件），这类不属于 Qoder 配置层级问题。
