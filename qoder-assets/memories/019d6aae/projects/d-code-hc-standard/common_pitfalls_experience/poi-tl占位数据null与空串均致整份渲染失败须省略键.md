---
title: "poi-tl占位数据null与空串均致整份渲染失败须省略键"
usage_scenario:
    - "排查模板导出接口500且异常含picture或date字样"
    - "组装docx/PDF模板数据Map时处理可选字段"
    - "新增模板图片占位或可选时间占位时预防缺陷"
keywords:
    - "poi-tl占位符容错"
    - "Pictures.ofUrl NPE"
    - "Unsupported picture空串"
    - "不放键降级"
    - "可选日期字段保护"
source: "auto"
---

Bug class: poi-tl 模板渲染时占位数据为 null 或空串导致整份文档生成失败（接口恒 500）。
Root cause: 向图片类占位符组装数据时，传 null（如 Pictures.ofUrl(null)）在渲染阶段抛 NullPointerException；改传空字符串 "" 又会被 poi-tl 当作图片格式解析，抛 "Unsupported picture: . Expected emf|wmf|jpeg|png..."；同理 DateUtils.parseDateToStr(fmt, null) 抛 "date must not be null"。三种异常都发生在 finalMap 组装/渲染期，任何一个可选字段为空即让整份 PDF/docx 导出报废，而非仅该位置留白。
Fix pattern: 对每个"可选展示"的键做条件放入——值非空才 put 该键（键缺失时模板对应标签自动留空），禁止用 "" 或 null 兜底喂给类型化对象（Pictures/日期格式化）；日期类用 `obj == null ? "" : format(obj)` 三元保护。以 BaseWorkSpecialServiceImpl.renderWorkSpecialDocx 中签字图片、附件图片、编制时间/开始时间的处理为已验证范例。
Reusable lesson: Don't substitute "" or pass null into typed placeholder data (pictures, dates) just to "make the key present", because poi-tl/date utilities fail fast and one empty optional field kills the whole document render; instead omit the key entirely when the value is empty. Applies to poi-tl（及类似按类型绑定占位数据的模板引擎）渲染前的 Map/List 组装逻辑，尤其是签字图片、附件、可选时间字段；不适用于纯文本占位（文本传空串安全）与非模板链路的普通 JSON 序列化。
