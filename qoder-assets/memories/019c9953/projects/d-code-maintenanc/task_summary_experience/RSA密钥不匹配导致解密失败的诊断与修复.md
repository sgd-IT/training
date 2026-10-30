---
title: "RSA密钥不匹配导致解密失败的诊断与修复"
usage_scenario:
    - "排查RSA解密失败BadBlockException等异常"
    - "前后端加密通信调试时验证密钥配对关系"
    - "修复因密钥版本/位数不一致导致的加解密异常"
keywords:
    - "RSA密钥"
    - "BadBlockException"
    - "密钥配对"
---

## 任务描述
解决 RSA 解密失败的 BadBlockException 错误，根本原因是前后端 RSA 密钥对不匹配。

## 执行过程
```mermaid
graph TD
    A[错误:BadBlockException] --> B[定位CryptoFilter解密入口]
    B --> C[搜索前后端密钥配置文件]
    C --> D[读取.env.development与application.yml]
    D --> E[比对密钥位数与配对关系]
    E --> F[发现前端512-bit公钥≠后端1024-bit私钥]
    F --> G[提取application.yml注释中的正确密钥]
    G --> H[修改.env.development和.env.production]
```

## 任务总结
成功修复 RSA 解密失败：通过比对密钥位数与配对关系，确认前端公钥（512-bit）与后端私钥（1024-bit）不匹配；依据后端 application.yml 注释中提供的正确密钥，精准更新了前端两个环境文件中的 VITE_APP_RSA_PUBLIC_KEY 和 VITE_APP_RSA_PRIVATE_KEY。
