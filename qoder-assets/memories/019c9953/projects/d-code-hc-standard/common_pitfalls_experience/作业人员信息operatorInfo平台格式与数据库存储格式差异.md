---
title: "作业人员信息operatorInfo平台格式与数据库存储格式差异"
usage_scenario: []
keywords:
    - "operatorInfo"
    - "格式转换"
    - "PlatementTicketNewApi"
    - "作业人员信息"
    - "分号分隔"
---

平台要求的作业人员信息（operatorInfo）格式为：'姓名:身份证号,特殊作业工种,特殊工作证号'（多人用英文分号;隔开）。数据库中多人记录实际用逗号,分隔（如'张三:ID1,证名1,证号1,李四:ID2,证名2,证号2'），需通过检测'逗号后是否为中文姓名+冒号'模式智能拆分多人，再用分号;重新连接。所有票种类型统一保留完整格式，不做区分。（来源：PlatementTicketNewApi.java transformOperatorInfo + splitPeople方法）
