---
title: "平台附件上传统一走uploadLocalFiles并按扩展名分流"
usage_scenario:
    - "开发或修改向平台推送证书附件的代码时"
    - "排查附件上传base64为空降级占位时"
    - "扩展人员证书上传文件格式时"
keywords:
    - "uploadLocalFiles"
    - "resolveUploadPath"
    - "附件上传"
    - "图片文档分流"
    - "convertToBase64"
    - "pdf支持"
source: "auto"
---

向外部平台（卢博祥等）上传本地附件（人员证书、资质附件等）统一调用 PlatementFileApi.uploadLocalFiles(urls)：它接受 http(s) URL 或 /profile/upload/... 相对路径（内部 resolveUploadPath 解析到本地上传目录），按扩展名分流——jpg/jpeg/png/gif/bmp 走 uploadFile(type=1 图片通道)，pdf/doc 等走 uploadFileWord(type=2 文档通道)，文件名保留真实扩展名；传入方允许混有已换好的平台 ID（无 profile/upload 的原样带回）。禁止手工拼"时间戳/证书名+.jpg"文件名，也不要把含 http 的 URL 直接传给 convertToBase64（Paths.get 解析失败→base64 恒空→catch 后降级为占位值）。人员证书链路已按此改造：Web 端 contractorPeopleCert 证书附件为 file-upload，允许 png/jpg/jpeg/pdf、20MB。
