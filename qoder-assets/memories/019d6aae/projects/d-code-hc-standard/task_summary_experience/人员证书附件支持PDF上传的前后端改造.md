---
title: "人员证书附件支持PDF上传的前后端改造"
usage_scenario:
    - "再次改造人员证书附件上传或推送链路时"
    - "排查平台附件推送文件名或通道错误时"
    - "复盘文件格式放开类前后端改造时"
keywords:
    - "人员证书PDF"
    - "file-upload改造"
    - "uploadLocalFiles"
    - "certFiles越界修复"
    - "平台附件推送"
source: "auto"
---

## Task description
- Core requirement: 让人员证书（contractorPeopleCert）的证书附件支持 PDF 格式上传，并按用户要求"不改移动端，直接修改前后端"
- Task background: Web 端证书附件用 image-upload 组件（默认 fileType 仅 png/jpg/jpeg），前端拦截 PDF；后端 /common/upload 白名单其实允许 pdf；但 3 条向外部平台推送证书的链路均存在".jpg"文件名硬编码或 URL 解析 bug；数据库 file_path 实测存的是完整 http URL（如 http://220.203.23.144:18080/profile/upload/...jpg）

## Execution process
1. 前期核查：确认 PC 端拦截点为 ImageUpload 组件默认 fileType；后端 DEFAULT_ALLOWED_EXTENSION 含 pdf；Android 端无证书上传入口且所有上传强制走 BitmapUtil.compressImage（JPEG），天然不支持 PDF
2. contractorPeopleCert/index.vue：证书附件由 image-upload 换成 file-upload，fileType=['png','jpg','jpeg','pdf']、file-size=20MB；存库格式不变（均为完整 URL）
3. PlatementPractitionersApi.java：抽出 toCertFileIds() 辅助方法，certFiles 含 profile/upload 的路径走 uploadLocalFiles 换平台 ID、已是 ID 的原样带回（顺带修掉"传 ID 再 split('profile/upload')[1] 数组越界导致同步必挂"的老坑），删除 RuoYiConfig/DateUtils 冗余 import
4. BaseContractorPeopleCertServiceImpl.syncToPlatform：不再预上传并拼"时间戳+.jpg"，改为把 filePath 路径原文放入 certFiles 交由统一转换处理
5. BaseWorkSpecialServiceImpl.buildOneCertificate：原把 http URL 直喂 convertToBase64（Paths.get 恒失败→base64 空→降级占位 fileId=certNo），改为 fileApi.uploadLocalFiles(filePath)
6. BaseTicketServiceImpl.selectBaseTicketById + PlatementTicketApi：params["certFile"] 改存 DB 原始 URL，上传调用改为 uploadLocalFiles(certFile)
7. 验证：JDK17（D:\Tool\jdk\jdk17）执行 mvn -B -pl ruoyi-common,ruoyi-system -am compile → BUILD SUCCESS；git diff 复核全部改动

## Related files
- /ruoyi-ui/src/views/base/contractorPeopleCert/index.vue
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementPractitionersApi.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementTicketApi.java
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseContractorPeopleCertServiceImpl.java
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseTicketServiceImpl.java

## Notes
- "后端不需要改"的预判不成立：3 条证书推送链路都有 .jpg 硬编码或 URL→本地路径解析 bug，只改前端会造成 PDF 推平台文件名/通道错误
- SearchReplace 工具对缩进空白有容错（tab/空格混排文件仍能匹配成功并保留原风格），批量编辑前应用单一分支的正则 grep 探测精确缩进以免误判
- PowerShell 回退环境读中文源码会输出 GBK 乱码，不能据此判断文件内容损坏，应以 Read 工具和编译结果为准
- platement/emp"证书图片"入口仍是 image-upload 未纳入本次范围；四色图/营业执照等其他 .jpg 硬编码同样未动

## Task overview
已完成：前端证书附件换成 file-upload 支持 png/jpg/jpeg/pdf（20MB）；后端 5 处统一改走 PlatementFileApi.uploadLocalFiles（URL/相对路径解析 + 图片/文档按扩展名分流 + 保留真实文件名），并修复 practitioners 同步越界和作业票证书 URL 直喂 base64 恒空两个存量缺陷；ruoyi-common/ruoyi-system 编译通过。遗留：emp 页面与移动端如需 PDF 需另行改造。
