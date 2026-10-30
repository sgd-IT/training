---
title: "AEP平台接入安全与协议规范：HTTPS强制、结构化JSON推送及客户端证书双向认证"
usage_scenario:
    - "部署AEP回调服务时配置TLS证书与HTTPS监听端口"
    - "评审AEP接入方案安全性时确认是否满足HTTPS强制要求"
    - "编写AEP接入合规性检查清单"
keywords:
    - "AEP"
    - "HTTPS"
    - "结构化JSON"
    - "客户端证书"
    - "双向认证"
    - "Nginx配置"
    - "AEP eventType"
    - "JSON payload structure"
source: "auto"
---

AEP平台接入必须同时满足以下三方面要求：

1. **HTTPS强制要求**：AEP平台HTTP消息推送必须使用HTTPS协议，禁止使用HTTP。因为HTTP采用明文传输，存在推送消息被窃听或篡改的安全风险；HTTPS具备数字签名和通讯加密能力，具有较高安全性。仅在开发调试阶段可临时使用HTTP，生产环境必须启用HTTPS。

2. **结构化JSON推送**：AEP向业务系统推送消息必须采用HTTP(S)协议直接推送结构化JSON数据（如{'temperature':35,'smoke':1}），禁止使用CoAP协议或透传模式（Base64 TLV）。该约定是AEP→业务平台通信的底层协议规范。

3. **客户端证书双向认证**：当AEP平台通过HTTPS推送消息时，北向消息接收服务（如Nginx）必须启用客户端证书认证，以验证AEP平台身份。参考Nginx配置：
server {
  listen 443 ssl;
  ssl_certificate /xxx/server.crt;
  ssl_certificate_key /xxx/server.key;
  #平台安全证书
  ssl_client_certificate /xxx/aep-msgpush.crt;
  ssl_verify_client on;
  location / {
    root html;
    index index.html index.htm;
  }
}

[This memory has not been used for a while and may be outdated — verify before use.]

## Relevance to AEP告警链路
- AEP告警链路的可靠性与安全性直接依赖于HTTPS强制和客户端证书双向认证。
- WebSocket推送是AEP告警链路实现Web端实时提示的核心机制，其稳定性受HTTPS和TLS配置影响。
