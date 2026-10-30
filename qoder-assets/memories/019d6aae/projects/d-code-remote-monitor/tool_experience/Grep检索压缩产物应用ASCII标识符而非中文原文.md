---
title: "Grep检索压缩产物应用ASCII标识符而非中文原文"
usage_scenario:
    - "确认前端生产包是否含某功能时"
    - "排查构建产物内容与设计不一致时"
keywords:
    - "压缩产物检索"
    - "ASCII标识符优先"
    - "unicode转义序列"
    - "terser输出特性"
    - "构建结果核验"
source: "auto"
---

## Usage Scenario
Description: 需要确认压缩后的前端构建产物（js chunk）是否包含某项功能时
Examples: 校验生产包是否已打进某个新页面/弹窗文案；核对 API 路径字符串是否存在于打包结果中

## Usage Method
Grep: 在压缩产物目录中检索时优先使用 ASCII 标识符（接口路径片段、变量名、组件文件名片段如 xxx.api、Dialog 名）作为关键词，或先按文件名特征列出候选 chunk 再逐个读取内容判断；需要确认中文文案时改为匹配 \uXXXX 转义序列（terser 输出的十六进制字母为大写），不要用原文中文直接检索

## Notes:
- 在压缩产物里直接用中文原文做关键词会导致零命中并被误判为"功能未打进包"，必须改用 ASCII 标识符或 unicode 转义序列核验后再下结论
- 在 PowerShell 中通过 node -e 内联脚本构造含反斜杠的 unicode 匹配串会因多层转义而失效，应改用正则匹配固定字符片段或读取文件后打印片段确认
