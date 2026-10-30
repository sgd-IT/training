---
title: "UdpConfig centerAddr默认值：解析路径生效但下发路径须显式配置"
usage_scenario:
    - "当UDP组件JSON配置中缺失centerAddr字段时"
    - "当设备拒收平台确认帧排查地址配置时"
    - "当调整strictCenterAddrCheck校验策略时"
keywords:
    - "UdpConfig.java"
    - "centerAddr"
    - "确认帧源地址"
    - "Jackson默认值"
    - "strictCenterAddrCheck"
source: "auto"
---

UdpConfig.java中centerAddr字段默认值为"020000000001"。需区分两条路径：1) Jackson解析路径——JSON配置缺失该字段时使用此默认值，解析本身不报错；2) 运行时下发路径——centerAddr同时用作平台确认帧/控制帧（CMD=0x03）的源地址及上行帧目的地址校验值，若配置显式为全零或缺失导致按全零处理，设备侧大概率因dst不匹配而拒收，链路表现为"能收到上行但设备收不到确认"。故生产配置必须在eiot_component.config中显式写入centerAddr并与设备侧"平台中心地址"对齐；另外strictCenterAddrCheck=true时上行帧dst必须精确等于centerAddr（dst全零放行）。
