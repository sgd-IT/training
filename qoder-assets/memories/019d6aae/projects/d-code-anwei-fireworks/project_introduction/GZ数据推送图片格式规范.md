---
title: "GZ数据推送图片格式规范"
usage_scenario:
    - "实现或修复GZ数据推送功能时确定图片字段和打包规则"
    - "验证推送数据是否符合贵州平台验收标准"
    - "排查图片上传失败问题时对照规范检查字段和文件结构"
keywords:
    - "GZ推送"
    - "贵州格式"
    - "image_file"
    - "相对路径"
    - "ZIP打包"
source: "auto"
---

数据库中存储的是OSS图片URL，但向贵州省平台推送时必须满足格式规范：JSON中image_file字段需替换为files/xxx.jpg相对路径，图片文件需打包进ZIP的files/目录。
