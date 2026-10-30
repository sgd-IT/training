---
title: "Figma个人access token与文件账号归属绑定致换账号即失效"
usage_scenario:
    - "使用Figma MCP读取设计稿前确认前置条件"
    - "Figma API返回403或无法列出文件时排查"
    - "用户切换Figma账号后判断配置与设计稿是否仍可用"
keywords:
    - "Figma access token"
    - "2865215722"
    - "fileKey清单"
    - "Make a copy"
    - "换Figma账号需重新生成token"
source: "auto"
---

本机用户级 mcp.json 中的 Framelink MCP for Figma 配置了个人 access token（账号 handle 2865215722 / 2865215722@qq.com，2026-09-28 经验证有效），可调用 get_figma_data / download_figma_images。token 本体为敏感凭证，不记录于此。

适用边界（重要）：上述有效性与下列 fileKey 可读性仅在继续使用该 Figma 账号时成立。该账号系他人账号；用户已表明将在同一台机器登录自己的 Figma 账号。一旦切换 Figma 账号：
- 现有 token 属旧账号，对新账号下的文件无权限，需在新账号 Settings → Security → Personal access tokens 重新生成并替换 mcp.json 中的 token；
- 下列社区 UI Kit 均 Make a copy 到旧账号名下，新账号看不到也读不到，需在新账号内对每个文件重新点一次 Make a copy of this file 后再由用户提供带 fileKey 的链接。

注意区分两类"换账号"影响：Qoder/IDE 登录账号变更不影响任何本地 MCP 配置（配置绑定 Windows 用户而非登录账号，见「MCP配置两级落点与作用范围」）；只有外部服务自身的凭证与资源归属会随其账号变更而失效。

可用性前提（工具参数与调用细节见「Figma MCP 读取设计稿的参数与前置条件要点」）：被读文件必须拥有或共享给 token 对应账号，社区资源需先复制到本人账号后才可读，公开可浏览不等于 API 可读；每次读取须由用户提供带 fileKey 的链接（API 无法枚举账号文件）。

旧账号（2865215722）内曾确认可读的社区 UI Kit 及 fileKey（换账号后需重新复制方可复用）：
- iOS and iPadOS 26 (Community)：fileKey 3XEac6rNlnL7y6dk5CTBxs（Apple 官方，iOS 端人气第一，约 269k users）。
- Material 3 Design Kit (Community)：fileKey IU90BzYn056FpzmCi8iKLf（Google 官方 Android 标准；主色 #6750A4，正文 Roboto；Getting started 画布节点 11:1833）。
- Element Plus Design System for Figma UI Kit (Community)：fileKey kmVpnpJ6IunubfX8bCMwTr（与 hc-standard 若依系统 1:1 对应；主题蓝 #409EFF、成功绿 #67C23A、主文字 #303133；含 70+ 组件画布，按 Basic/Form/Data/Navigation/Feedback/Others 分类；封面节点 137:25883）。
- Simple Design System (Community)：fileKey 8RC8pPd0SgxlmNfNZEglHW（Figma 官方示范系统，主打 Code Connect 设计代码打通；黑白底 + 品牌色青 #00B6FF、蓝紫 #4D49FC，封面字体 Whyte；Cover 节点 3:5）。

上述链路已于 2026-09-28 起多次实测通过：get_figma_data 成功返回各文件节点结构。
