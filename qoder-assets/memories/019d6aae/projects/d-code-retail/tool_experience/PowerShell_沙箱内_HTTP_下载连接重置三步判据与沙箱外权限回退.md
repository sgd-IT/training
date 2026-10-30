---
title: "PowerShell 沙箱内 HTTP 下载连接重置三步判据与沙箱外权限回退"
usage_scenario:
    - "沙箱内下载网络资源报连接重置异常"
    - "判断 TLS 报错是否实为沙箱限制"
    - "需要提升权限在沙箱外重试命令"
keywords:
    - "sandbox network reset"
    - "required_permissions all"
    - "Invoke-WebRequest 连接重置"
    - "TLS 误判排除"
    - "Test-NetConnection 对照实验"
source: "auto"
---

## Usage Scenario / 使用场景
在沙箱内用 PowerShell 下载 HTTP(S) 资源（Invoke-WebRequest、WebClient.DownloadFile）时报"基础连接已经关闭: 接收时发生错误"，内层异常为 Win32Exception"安全包中没有可用的凭证"；此时连明文 HTTP 请求也同样失败，但 Test-NetConnection 对目标端口返回 True，且浏览器可正常访问同一 URL。

## Usage Method / 使用方法
该症状表明是沙箱网络出口在重置数据流，而非 TLS 版本、证书或服务器问题：不要再去调 SecurityProtocol、加证书回调或换下载 API（三者都会同样失败）。改用 Bash 工具并设置 required_permissions='all' 在沙箱外执行同一命令，即可正常完成下载。对照实验可用"同一 URL 不带 -OutFile 的 GET"与"带 -OutFile 的 GET"，若两者结果一致（都失败）即可排除写文件权限因素。

## Notes
判断要点是"TCP 可达 + 数据传输失败 + 沙箱外正常"三者同时成立；若只有 https 失败而 http 成功，才应怀疑 TLS 配置。
