---
title: "大华拉流URL强制token鉴权规范"
usage_scenario: []
keywords:
    - "大华拉流"
    - "token鉴权"
    - "URL拼接"
    - "iccAuthService"
---

大华ICC平台所有拉流接口返回的URL必须在末尾拼接`?token=<access_token>`才能正常播放，该token由iccAuthService.getAuthorization()获取并截取纯值后拼接，且需根据URL是否已含查询参数选择`?`或`&`分隔符。
