---
title: "特殊作业票导出PDF的前后端链路改造"
usage_scenario:
    - "再次改造作业票打印或PDF导出链路时"
    - "实现浏览器直接下载文件流接口时"
    - "排查blob导出下载到坏文件问题时"
keywords:
    - "xdocreport替换documents4j"
    - "PdfUtil返回pdf路径"
    - "Content-Disposition attachment下载"
    - "blob响应JSON错误拦截"
    - "作业票导出接口"
    - "风险编码三处硬编码"
source: "auto"
---

hc-standard 作业票 docx 模板（poi-tl / XWPFTemplate）对占位数据零容错，两类失效形态：① 值为 null 或空串均致渲染异常——未签字安全措施的 Pictures.ofUrl(personFile) 传 null 抛 NPE、传空串抛 Unsupported picture；DateUtils.parseDateToStr 在日期字段为 null 时抛 "date must not be null"。正确做法是键为空时根本不放该键让模板留空，图片类与日期类占位须用 StringUtils.isNotEmpty / 三元空值保护包裹。② 占位符名写错不抛任何异常，只把该位置渲染成空白，排查成本远高于报错。模板占位符全部是中文命名（如 {{编号}}、{{气体分析}}、{{措施}}、{{现场会签}}），其中措施/气体分析/现场会签等表格类占位绑定了 LoopRowTableRenderPolicy 做循环行。改模板时必须把占位符名与服务层 finalMap.put(...) 的键逐一对齐（特殊作业见 BaseWorkSpecialServiceImpl.renderWorkSpecialDocx）。适用于本仓库所有 poi-tl docx 打印模板；纯文本无表格循环的模板仍受第②条约束。
