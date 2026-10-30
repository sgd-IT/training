---
title: "零售店配置表（retail_config）推送开关字段定义"
usage_scenario:
    - "When implementing or modifying notification logic for retail stores"
    - "When querying or updating retail store-specific notification preferences"
keywords:
    - "retail_config"
    - "notification switches"
    - "voice_broadcast_enabled"
    - "sms_push_enabled"
    - "app_push_enabled"
    - "night_dnd_enabled"
source: "auto"
---

The retail_config table contains the following boolean switch fields for controlling notification delivery: voice_broadcast_enabled (in-store voice broadcast), sms_push_enabled (SMS notifications), app_push_enabled (mobile app push notifications), and night_dnd_enabled (Do Not Disturb mode during nighttime hours). These switches are used to dynamically enable or disable specific notification channels per retail store.
