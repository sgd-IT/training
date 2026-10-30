---
title: "personType空值默认处理规范"
usage_scenario:
    - "排查选择作业人后提示数据异常的字段校验问题"
    - "处理人员进出或人员信息推送时填充personType默认值"
    - "代码审查时检查personType空值是否按规范赋默认值"
keywords:
    - "personType"
    - "空值处理"
    - "默认值"
    - "SD-RYCRJL"
    - "作业人员"
    - "证件信息"
source: "auto"
---

人员进出记录（SD-RYCRJL）中，personType字段若为null，需默认赋值为3（其他）；人员信息推送中personType为null时默认赋值为5，两类场景需保持各自合理的默认值。
