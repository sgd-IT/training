---
title: "BigDecimal序列化为JSON数字时丢失小数位"
usage_scenario:
    - "下游接口严格校验小数位数但接收值被截断"
    - "本地已格式化2位小数但请求体显示0或1位"
    - "FastJSON/Jackson序列化BigDecimal需保证精度原样传输"
keywords:
    - "BigDecimal"
    - "JSON序列化"
    - "小数位丢失"
    - "toPlainString"
    - "末尾零被丢弃"
source: "auto"
---

BigDecimal 经 JSON 序列化会变成 JSON 数字类型并自动丢弃末尾零（如 28260.00 → 28260、500.50 → 500.5），导致下游平台按小数位校验失败；FastJSON 默认行为即如此。

## 关键坑点（本项目实测）
- 要保留原始精度必须显式调用 `toPlainString()` 转为字符串，但**转成带引号的字符串会违反要求 JSON 数字格式的接口**（例如 SD 平台 dosage 字段禁止 `"dosage":"28260.00"`）。
- 因此正确做法是：先正常序列化，再在生成的 JSON 字符串上用正则对目标数值字段补零至固定小数位（如 dosage 补至 2 位小数），既保持无引号的数字格式又保住小数位（来源：RequestSDMethod.java 中 FastJSON 使用）。
