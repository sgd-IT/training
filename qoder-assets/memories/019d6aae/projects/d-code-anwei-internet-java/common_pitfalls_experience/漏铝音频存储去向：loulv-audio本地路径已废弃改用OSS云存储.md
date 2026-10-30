---
title: "漏铝音频存储去向：loulv-audio本地路径已废弃改用OSS云存储"
usage_scenario:
    - "排查漏铝音频文件未落地时确认存储去向"
    - "核对现场部署isStorePhoto开关取值"
    - "跨项目对比音频上传实现差异"
keywords:
    - "loulv-audio历史路径"
    - "isStorePhoto开关"
    - "OSSFactory云存储"
    - "sys_oss表"
    - "音频存储去向"
source: "auto"
---

「漏铝音频接收时保存到本地固定目录 D:\project\java\monkey-aluminium-api\loulv-audio（命名 {deviceSn}_{yyyyMMddHHmmss}_{原始名}）」是 monkey-aluminium-api 项目的**历史实现**，现已不适用：

- anwei-internet-java（monkey-monitor / monkey-monitor-api）工作区从未有该硬编码路径，音频一律经 OSSFactory.build().uploadSuffix(bytes, suffix) 走云存储抽象，落地位置由 sys_oss 表配置决定，排查音频未保存应查该表而非找本地目录。
- 即使在 monkey-aluminium-api，本地保存也已改为受配置开关 monkey.leak.isStorePhoto 控制；客户现场（青海桥头，端口 9090）application.yml 实测 isStorePhoto: false，即默认不再写本地磁盘。

结论：不要在任何漏铝音频问题上按 loulv-audio 目录去找文件或据此推断行为。适用边界：漏铝/嘉果音频存储去向判断；不适用于项目通用上传目录 FileUploadConfig（前缀 monkey.fileupload）。
