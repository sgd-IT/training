---
title: "YWN与Dahua UDP组件监听端口"
usage_scenario:
    - "When configuring device-side platform addresses for YWN or Dahua protocols"
    - "When diagnosing UDP port connectivity issues"
keywords:
    - "YWN"
    - "Dahua"
    - "UDP"
    - "port 48888"
    - "port 8839"
source: "auto"
---

The YWN TCP component listens on port 8839. The Dahua UDP component listens on port 48888. Both components' configurations are stored in the eiot_component table of the MySQL database.
