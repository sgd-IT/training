---
title: "数据库字符集与SQL导入配置"
usage_scenario:
    - "导入SQL建表脚本前检查并设置字符集声明"
    - "排查数据库中文注释乱码问题时确认导入方式"
    - "新成员初始化数据库环境时避免重复乱码"
keywords:
    - "utf8mb4"
    - "SET NAMES"
    - "SQL导入"
    - "字符集乱码"
---

数据库使用utf8mb4字符集，SQL脚本导入前需在文件头部添加`SET NAMES utf8mb4;`语句，防止中文注释乱码
