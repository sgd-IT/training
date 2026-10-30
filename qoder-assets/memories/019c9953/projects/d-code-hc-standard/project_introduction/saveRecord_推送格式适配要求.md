---
title: "saveRecord 推送格式适配要求"
usage_scenario:
    - "修复 saveRecord 接口与园区平台格式不兼容问题"
    - "新增类似排查类接口时参考数据格式规范"
    - "对接第三方平台接口时验证请求体结构"
keywords:
    - "saveRecord"
    - "JSONArray"
    - "JSONObject"
    - "园区平台"
    - "格式报错"
---

排查记录 saveRecord 接口推送到园区平台时，原使用 JSONArray 格式 [{...}] 导致报错；经确认园区平台 `/openapi/check_unit_data/troubleshooting` 接口仅接受单个 JSONObject 格式 {...}，需改为逐条发送 JSONObject 并同步返回结果。
