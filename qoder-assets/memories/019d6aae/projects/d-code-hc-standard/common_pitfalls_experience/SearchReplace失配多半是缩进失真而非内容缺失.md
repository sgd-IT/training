---
title: "SearchReplace失配多半是缩进失真而非内容缺失"
usage_scenario:
    - "SearchReplace精确替换反复失配时"
    - "编辑tab缩进的Java文件时"
    - "确认源码真实缩进与换行符"
keywords:
    - "SearchReplace失配"
    - "缩进失真"
    - "tab缩进"
    - "字节校验"
    - "original_text"
source: "auto"
---

## 问题
用 SearchReplace 精确替换源码片段连续失败（原文本在 Read/Grep 输出里"看起来"完全一致），改用不含行首空白的子串才匹配成功，且替换后缩进比预期多了一层。

## 根因
Read/Grep 的行号前缀输出会吃掉或压缩行首制表符，方法体实际是 2 个 tab 时输出里可能显示成 0 或 1 个 tab；拿"看起来"的文本当原文去匹配必然失配。另外若 original_text 不含行首空白，工具会保留原有前导空白再叠加 new_text 的空白，缩进直接翻倍。

## 触发条件
文件用 tab 缩进（Java 项目常见）且 Read/Grep 输出被压缩显示；对含行首缩进的 original_text 做精确匹配。

## 做法
1. 先用只读脚本按字节打印目标区域（node 读文件后 JSON.stringify 每行，制表符会显示成 \t），确认真实缩进与换行符（CRLF/LF）后再写 original_text；
2. original_text 必须自带完整的行首 tab，不要只截行内子串，否则替换后缩进会叠加；
3. 一次调用里把"修正缩进"和"插入新方法"拆成互不重叠的替换项，先改缩进再插内容，替换后回读验证。
