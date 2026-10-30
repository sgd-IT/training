---
title: "XML同级块缩进不一致导致SearchReplace失配的匹配规避法"
usage_scenario:
    - "SearchReplace报精确匹配失败时"
    - "改动pom或mapper配置文件插入依赖块"
    - "需要修正某行缩进层级时"
keywords:
    - "同级块缩进不一致"
    - "行首空白不可作为锚点"
    - "缩进叠加错位"
    - "合并上行替换调缩进"
    - "pom XML编辑"
source: "auto"
---

Bug class: SearchReplace 精确匹配失败（original_text 无法命中目标文件），或匹配成功但产生缩进错位/重复内容块。
Root cause: 同一文件内相邻同构语句块缩进不一致（BaseWorkSpecialMapper.xml 一条 update 有 4 空格另一条顶格；ruoyi-common/pom.xml 的 <artifactId> 行 grep 显示无缩进而 Read 显示 12 空格），按含行首空白的多行块构造 original_text 必然失配；跨行大段匹配放大该问题。
Fix pattern: ① 匹配"单行内的短唯一片段"且**不带任何行首空白**（先确认片段在文件中唯一）；② 需要插入新块时，把 anchor 设为该片段本身，new_text = 新块 + 原片段——注意工具会保留 anchor 前的原有缩进，若 new_text 内又写缩进就会叠加成双倍缩进，因此新块的后续行要自己核对基准缩进；③ **调整某行缩进（如把 24 空格改 12 空格）时不要带行首空白做 original_text**，改为把该行与其上一行合并替换（上一行末尾 \n + 本行剩余文本 → 上一行末尾 \n + 正确缩进 + 本行文本），否则永远匹配不上；④ 一次替换失败后立刻 Read 复核实际字节，不要凭猜的空格数重试；⑤ 批量编辑前先 grep -n 探测精确缩进。
Reusable lesson: Don't construct original_text by copying a multi-line block with assumed indentation, and never try to shrink a line's leading whitespace using that line alone as the anchor; match a unique fragment free of leading whitespace and fold the whitespace change into the preceding line's replacement. Applies to XML/properties/Java files with inconsistent or unknown sibling indentation; uniformly indented blocks are still safe for block matching.
