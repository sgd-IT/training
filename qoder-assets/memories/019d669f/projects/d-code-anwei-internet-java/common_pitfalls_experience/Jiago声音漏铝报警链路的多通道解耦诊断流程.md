---
title: "Jiago声音漏铝报警链路的多通道解耦诊断流程"
usage_scenario:
    - "诊断现场反馈非铸造状态报警"
    - "核对声音漏铝过滤链"
    - "排查铸造状态判定失效"
keywords:
    - "Jiago"
    - "声音漏铝"
    - "多通道解耦"
    - "感知层"
    - "记录层"
    - "配置层"
source: "auto"
---

现象：现场反馈非铸造状态仍报警（声音漏铝链路 /lyxl/push status=0 → videoEventUp）。已核实的三条路径：
(1) 感知层绕过：音柱 sendIpVoice 仅由 status=0 触发，不校验铸造状态(getCastingStatus)与复位按钮，即使 eventUp 把记录拦掉音柱仍播报"监测到漏铝"；
(2) 记录层漏洞：getCastingStatus 直读 em_device_realtime 当前值(ax0047+水泵ax0024/ax0048)，无 update_time 新鲜度校验（DeviceRealtimeServiceImpl.getSignal 则有3分钟约束），采集卡死且最后值为1时恒判"铸造中"；
(3) 配置层：preSignalDelayEnable=true 时用 PRE_SIGNAL_KEY(ax0083前期信号)+300s 延时整体覆盖 PLC 判定（dev=true, prod=false）。
排查：查 mon_aluminum_leakage_alarm_record(alarm_type=3) 与 em_device_realtime.update_time；音柱查日志"sendIpVoice 推送成功"；确认部署profile。教训：报警过滤链存在多感知通道解耦（记录/音柱/设备自身），诊断"过滤失效"须按通道逐一核对。
