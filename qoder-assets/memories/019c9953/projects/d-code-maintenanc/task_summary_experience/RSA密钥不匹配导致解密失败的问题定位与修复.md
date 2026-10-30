---
title: "RSA密钥不匹配导致解密失败的问题定位与修复"
usage_scenario:
    - "排查CryptoFilter或类似加密过滤器抛出BadBlockException/Unable to decrypt block异常"
    - "前后端RSA加解密联调时密钥不生效的根因分析"
    - "新环境部署后加密功能失效时快速验证密钥配对关系"
keywords:
    - "RSA密钥配对"
    - "BadBlockException"
    - "CryptoFilter"
---

## 任务描述
解决 RSA 解密失败异常 `BadBlockException: unable to decrypt block`，根本原因是前后端 RSA 密钥对不匹配。

## 执行过程
```mermaid
graph TD
    A[异常: CryptoFilter RSA 解密失败] --> B[检索加密相关配置文件]
    B --> C[定位前端 .env.development 和后端 application.yml]
    C --> D[比对 VITE_APP_RSA_PUBLIC_KEY 与 api-decrypt.privateKey 位数]
    D --> E[发现前端 512-bit 公钥 ≠ 后端 1024-bit 私钥]
    E --> F[查阅后端配置注释获取配对公钥文本]
    F --> G[确认密钥配对关系：前端请求公钥 ↔ 后端解密私钥]
    G --> H[指导替换 .env.development 中的公钥和私钥为后端注释中提供的配对密钥]
```

## 任务总结
成功定位 RSA 加解密失败根源为密钥位数与配对关系不一致；明确前后端密钥映射规则（请求加密公钥↔解密私钥，响应加密公钥↔解密私钥）；提供可直接复用的密钥替换方案，避免因密钥不匹配导致的 BadBlockException。
