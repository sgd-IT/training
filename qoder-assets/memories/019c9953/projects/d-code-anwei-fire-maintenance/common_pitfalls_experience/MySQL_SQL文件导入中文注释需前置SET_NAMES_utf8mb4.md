---
title: "MySQL SQL文件导入中文注释需前置SET NAMES utf8mb4"
usage_scenario:
    - "MySQL数据库表注释或字段注释显示为乱码（如'涓婚敭'）"
    - "重新导入SQL脚本后中文注释仍乱码"
    - "排查发现@@character_set_client为utf8mb4但@@character_set_server为utf8"
keywords:
    - "MySQL"
    - "SET NAMES"
    - "utf8mb4"
    - "SQL导入乱码"
---

MySQL SQL脚本导入中文注释时发生乱码，根本原因是导入时未声明字符集，导致UTF-8字节被当作latin1解析后存入utf8mb4字段，形成双重编码。解决方案：在SQL文件第一行添加 `SET NAMES utf8mb4;` 和 `SET CHARACTER SET utf8mb4;`（来源：SearchReplace）
