---
title: "线上服务器公网IP与域名及证书可信访问要求"
usage_scenario:
    - "配置设备侧TCP/UDP协议的平台地址"
    - "设置防火墙规则或云安全组"
    - "小程序直连环境报证书错误时判定能否靠配置绕过"
keywords:
    - "online server"
    - "public IP"
    - "domain name"
    - "nslookup"
    - "ERR_CERT_AUTHORITY_INVALID"
source: "auto"
---

线上服务器公网IP为 14.22.72.94，域名为 fire-remote-admin.anxunwang.cn，两者均经 nslookup 核验。

配套约束：微信开发者工具 project.config.json 的 urlCheck=false 仅关闭域名校验，不关闭 https 证书校验；直连上述 IP 或使用自签证书的环境会报 800059 ERR_CERT_AUTHORITY_INVALID，必须改用受信任域名的 https 地址（即 https://fire-remote-admin.anxunwang.cn），无法靠 urlCheck 配置绕过证书。注意端口须按实际环境取值，不要把本地 dev 端口当作线上地址的一部分。
