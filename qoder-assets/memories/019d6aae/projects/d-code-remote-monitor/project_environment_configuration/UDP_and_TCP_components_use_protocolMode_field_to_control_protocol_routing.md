---
title: "UDP and TCP components use protocolMode field to control protocol routing"
usage_scenario:
    - "When adding multi-protocol support to device access modules"
    - "When debugging device packet parsing failures, confirming whether the current protocolMode value matches the device's actual protocol"
keywords:
    - "protocolMode"
    - "UDP component"
    - "TCP component"
    - "protocol routing"
    - "dahua"
    - "GBT26875"
    - "Fourfaith"
    - "JSON configuration"
source: "auto"
---

In FourfaithGbtTcpComponent's configuration class FourfaithGbtTcpConfig and UDP component's configuration, the protocolMode field controls protocol routing mode: when protocolMode = 'fourfaith', existing Fourfaith protocol routing logic is enabled; when protocolMode = 'dahua', Dahua Fire Protocol v1.04 two-dimensional routing logic is enabled; both constant systems (FourfaithGbtConstants and DahuaProtocolConstants) coexist without interference; this configuration is the core switch for runtime dynamic switching of protocol parsing paths, affecting the branch decision in the processPacket method.

## Notes
- UDP component JSON configuration requires the following fields: port=48888, host="0.0.0.0", protocolMode="dahua", centerAddr="020000000001", enableAutoRegister=true
- TCP component binary protocol logs require DEBUG-level logging in processPacket to record raw hex packet strings for debugging
