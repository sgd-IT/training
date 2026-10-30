---
title: "用户负责的 Java 文件署名统一用 @author sgd"
usage_scenario:
    - "新建或补充用户负责的Java文件时"
    - "修改文件Javadoc署名或检查署名一致性时"
    - "区分用户个人产出与项目模板文件时"
keywords:
    - "@author sgd"
    - "署名规范"
    - "Javadoc 作者"
    - "FireSpace"
    - "EnjoyIot 模板署名"
source: "user"
---

用户 sgd 要求自己负责的后端 Java 文件 Javadoc 统一使用 @author sgd 署名，不用项目模板默认的 @author EnjoyIot。已执行：FireSpace 全链路 6 个文件（Controller、Service、ServiceImpl、Mapper、DO、DTO）全部改为 @author sgd，DTO 原无 javadoc 头也补上了署名。仓库中既有的 sgd 署名文件（AepHttpPushAlarmStrategy、FirewatchScheduledTask、FirePartolRecordMapper 等）亦为用户个人产出。后续新建或补充用户负责的文件时，署名按此规范写 sgd。
