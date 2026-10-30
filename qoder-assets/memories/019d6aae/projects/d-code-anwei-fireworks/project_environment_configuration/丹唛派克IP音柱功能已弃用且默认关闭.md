---
title: "丹唛派克IP音柱功能已弃用且默认关闭"
usage_scenario:
    - "排查语音播报未触发原因时检查该设备开关配置"
    - "启用丹唛派克音柱前需确认并修改isopen=true"
keywords:
    - "丹唛派克"
    - "IP音柱"
    - "功能弃用"
    - "默认关闭"
source: "auto"
---

丹唛派克IP音柱功能已被明确弃用，不参与当前生产环境播报流程；其配置项 dmpk.isopen 默认为 false，语音播报会跳过该设备。
