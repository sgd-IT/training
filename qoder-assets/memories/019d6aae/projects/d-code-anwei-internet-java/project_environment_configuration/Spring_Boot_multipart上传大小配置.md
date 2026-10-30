---
title: "Spring Boot multipart上传大小配置"
usage_scenario:
    - "排查文件上传超限错误时检查关键配置项"
    - "新环境部署时同步上传限制参数"
    - "核对客户现场application.yml完整性时"
keywords:
    - "multipart"
    - "max-file-size"
    - "max-request-size"
    - "成对配置"
    - "FileSizeLimitExceededException"
    - "现场yml核对"
source: "auto"
---

项目文件上传大小限制需**成对**设置 `spring.servlet.multipart.max-file-size` 与 `spring.servlet.multipart.max-request-size`。

诊断要点：只配 max-file-size 而漏配 max-request-size 时，单文件校验虽通过，但整个 multipart 请求体仍会被以 max-request-size 的默认值（Spring Boot 默认同为 10MB）拦截，抛 FileSizeLimitExceededException / SizeLimitExceededException，表现为音频/图片上传接口报"上传失败"而日志里看不到业务异常。

取值现状（勿假定统一）：dev/test/prod 三份 profile 配的是 100MB，而客户现场本地化部署的 yml 实测只写了 max-file-size: 10MB 且缺 max-request-size —— 现场配置并不等于仓库 profile，核对时必须逐份检查两项是否成对出现且取值一致。漏铝声音识别的 15 秒音频接近上限时最容易暴露此问题。
