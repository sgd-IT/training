---
title: "SearchReplace/Write工具禁止编辑项目外文件"
usage_scenario:
    - "尝试修改用户家目录或系统路径下的配置文件时工具报错45405"
    - "工具返回'can not edit the file outside the projects'错误"
    - "需修改.qoder等全局配置但工具权限受限"
keywords:
    - "45405错误"
    - "项目外文件"
    - "SearchReplace"
    - "Write"
---

工具 SearchReplace 和 Write 无法编辑项目目录外的文件，会返回错误 code = 45405，message = 'can not edit the file outside the projects'。该限制适用于所有非项目根目录下的绝对路径文件（如用户家目录或系统路径）。需提醒用户手动编辑并重启相关服务。（来源：SearchReplace, Write）
