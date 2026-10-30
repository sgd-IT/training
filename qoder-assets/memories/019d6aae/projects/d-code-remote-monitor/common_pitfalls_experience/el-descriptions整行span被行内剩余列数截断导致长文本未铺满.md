---
title: "el-descriptions整行span被行内剩余列数截断导致长文本未铺满"
usage_scenario:
    - "改造详情弹窗描述列表为多列混排布局时"
    - "排查长文本字段未按预期独占整行时"
    - "核验descriptions行列对齐与colspan时"
keywords:
    - "el-descriptions"
    - "span截断"
    - "整行布局"
    - "列对齐"
    - "Element Plus弹层"
source: "auto"
---

Bug class: Element Plus el-descriptions 整行项（span 等于 column 值）不生效，长文本字段被挤在窄列里折行。

Root cause: 组件按行内剩余列数对 span 做截断——当某个 item 声明的 span 大于当前行剩余可用列数时，实际渲染宽度被收窄到剩余列数而非声明值；因此"整行"效果只在该项恰好位于行首时才成立。前面若有 span=1 的项未凑满整行，就会把后续整行项顶到行中间而被截断。

Fix pattern: 排布前先算列对齐——统计每个 item 占用的列数，确保需要整行的项前面正好有若干组填满整行的 item；若前面某项导致行未满，给其中一项显式补 span（如 span=2）凑满该行，使整行项重新回到行首。修复后用 DOM 量测核验该行的单元格 colspan 合计等于 column×2（label+value 各计一列）。

Reusable lesson: 不要以为声明了整行 span 就一定整行铺开，因为行内剩余列不足时 span 会被静默截断、页面看不出报错只能靠视觉/DOM 核验发现；应保证整行项从行首开始，必要时给前序项补 span。适用于 el-descriptions 多列且混用不同 span 的详情类弹窗/表格布局改造；不适用于所有项 span 均为 1 且总数正好为列数倍数的规整排布。
