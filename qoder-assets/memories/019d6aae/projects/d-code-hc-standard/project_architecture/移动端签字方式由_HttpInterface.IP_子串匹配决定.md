---
title: "移动端签字方式由 HttpInterface.IP 子串匹配决定"
usage_scenario:
    - "切换移动端API地址后核对签字流程"
    - "排查作业票签字走错设备或签名页异常"
    - "重构环境判断为统一配置标记"
keywords:
    - "FragmentJobSlipSign"
    - "ActivitySignature"
    - "ActivityBluetooth"
    - "IP子串匹配"
    - "签字方式切换"
source: "auto"
---

Android 移动端作业票签字入口的签名方式选择，由 HttpInterface.IP 字符串内容硬编码判断决定：other1/app/src/main/java/com/sckj/other1/fragment/FragmentJobSlipSign.java 中存在多处 `HttpInterface.IP.contains("192.168.0.200")` 判断——命中时跳转 ActivitySignature（App 内屏幕手写签名页），未命中时一律跳转 ActivityBluetooth（蓝牙签名设备）。由此产生隐式耦合：切换 API 地址常量会静默改变签字硬件流程，任何非 192.168.0.200 的地址（含生产 220.203.23.144、内网 192.168.0.202）都走蓝牙签字分支。修改 IP 或新增环境地址时必须同步复核该判断，否则会出现"能连上服务但签字设备不对"的失配。根因是缺少统一的环境标记抽象，以 IP 子串匹配代替配置开关。

## Notes
- 补充实测证据（动火票完工签字会话）：本机联调地址 HttpInterface.IP = http://192.168.250.130:28080/ 不含 "192.168.0.200"，点"点击签字"确认"是"后确实跳入 ActivityBluetooth「我的蓝牙」页并强制要求开启蓝牙做 iBeacon 现场校验，验证了"未命中即走蓝牙分支"的判定方向正确。连带后果：MuMu 模拟器无蓝牙硬件（dumpsys bluetooth_manager 恒为 enabled=false/OFF），该地址下所有签字在模拟器上必然卡死，只能改用 /api/special/updApprove 接口补签绕过；若要在模拟器上做屏幕手写签名路径的 UI 验证，需临时把 IP 改为含 192.168.0.200 的地址（但会同时失去后端连接，需权衡）。
