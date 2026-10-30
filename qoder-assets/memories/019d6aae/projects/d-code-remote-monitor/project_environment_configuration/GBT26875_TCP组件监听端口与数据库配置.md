---
title: "GBT26875 TCP组件监听端口与数据库配置"
usage_scenario:
    - "When diagnosing TCP port connectivity issues"
    - "When configuring or updating the GBT26875 TCP component"
keywords:
    - "GBT26875"
    - "TCP"
    - "port 8103"
    - "eiot_component"
source: "auto"
---

The GBT26875 TCP component listens on port 8103. Its configuration is stored in the eiot_component table of the MySQL database, with the config JSON containing the 'port' field set to 8103.
