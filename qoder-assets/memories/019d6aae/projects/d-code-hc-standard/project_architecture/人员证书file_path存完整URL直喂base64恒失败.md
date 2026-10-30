---
title: "人员证书file_path存完整URL直喂base64恒失败"
usage_scenario:
    - "读取或推送人员证书附件文件时"
    - "排查平台附件推送成功但远端无文件时"
    - "编写file_path路径拆分转换代码时"
keywords:
    - "file_path完整URL"
    - "convertToBase64解析失败"
    - "resolveUploadPath"
    - "占位值静默降级"
    - "人员证书附件"
source: "auto"
---

base_contractor_people_cert.file_path 存储的是含主机端口的完整 http URL（形如 http://220.203.23.144:18080/profile/upload/日期/文件.jpg），由 /common/upload 返回的 serverConfig.getUrl()+fileName 拼成，并非 /profile/upload/... 相对路径。因此把该字段原值直接传给 PlatementFileApi.convertToBase64（内部 Paths.get + Files.readAllBytes）会解析失败并抛异常，被外层 catch 后静默降级为占位值（如 fileId=certNo），推送看似成功实则附件从未上传；必须先经 resolveUploadPath/uploadLocalFiles 转成本地磁盘路径再读取。同理，对 file_path 执行 split("profile/upload")[1] 的旧式写法在传入已换好的平台 ID 时会数组越界。
