---
title: "COS附件下载失败的两大原因：权限私有与网络不通"
usage_scenario: []
keywords:
    - "COS"
    - "附件下载"
    - "HttpUtil"
    - "私有权限"
    - "网络连通性"
source: "user"
---

腾讯云COS存储桶若设为私有读写权限，外部服务（如省平台推送服务）直接通过HTTP URL下载会失败；即使URL可公开访问，也需确保推送服务器网络能连通 `*.cos.ap-guangzhou.myqcloud.com` 域名。（来源：PushingGZDataService.java 中 processAttachment() 的 HttpUtil.downloadFile 调用逻辑）
