---
title: "dev环境相比prod缺失的配置项清单"
usage_scenario:
    - "新成员初始化dev环境时补全缺失配置"
    - "排查dev启动失败时对照prod检查遗漏项"
    - "CI流水线校验dev配置完整性"
keywords:
    - "monkey.security"
    - "jt808.server.port"
    - "gdupload"
    - "qing-tian.chargeType"
    - "dev环境配置缺失"
source: "auto"
---

项目 dev 环境相比 prod 缺失的关键配置项清单（各模块 host/账号等具体值以对应的线上/prod 配置记忆为准，此处不重复）：
- `monkey.security`（OAuth2 认证配置，含 clients、token 有效期等）
- `monkey.fileupload`、`monkey.qr`、`monkey.wx`（文件上传、二维码、微信相关服务地址与密钥）
- `jt808.server.port`（JT808 协议监听端口）
- `gdupload`（广东省数据上传客户端 ID/密钥/接口地址）
- `qing-tian.chargeType`（青天计费类型）
