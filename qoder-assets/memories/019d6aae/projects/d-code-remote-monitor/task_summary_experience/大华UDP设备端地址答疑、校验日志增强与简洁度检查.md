---
title: "大华UDP设备端地址答疑、校验日志增强与简洁度检查"
usage_scenario:
    - "为大华UDP终端配置设备端平台地址时"
    - "复查UDP组件简洁度报告所列缺陷是否已修时"
    - "排查autoRegisterCidrs配置后注册失败时"
keywords:
    - "设备端地址配置"
    - "udp://前缀"
    - "strictCenterAddrCheck日志"
    - "reportRawData回归"
    - "简洁度分级报告"
source: "auto"
---

## Task description
- Core requirement: 回答大华 UDP 消防终端设备端应配置什么地址（含 udp:// 前缀疑问），并对本轮 UDP 方案改动做代码简洁度检查
- Task background: 前序会话已完成大华 UDP 公网接入方案实施（UdpConfig 四字段、IpWhitelist/IpRateLimiter、DahuaEventNormalizer 等）；用户现场设备配置界面的"平台地址"字段要求填 udp:// 前缀串

## Execution process
1. 实测确认设备端填法：nslookup 得 fire-remote-admin.anxunwang.cn → 14.22.72.94，Test-NetConnection 18080 通；设备端填 udp://14.22.72.94:48888（scheme 由设备侧 UI 约定，全仓 grep udp:// 零命中，平台不下发 URI）
2. 澄清两套地址：网络地址（IP:端口，不进协议帧）与协议中心地址（6字节HEX 020000000001，进帧目的地址字段，须与平台 centerAddr 对齐）
3. 生产库 MCP 查询发现风险：在跑的 TCP 组件（GB/T 26875 id=8、四信 id=25）config 全无 centerAddr（全零），说明现网设备大概率未配中心地址
4. UdpComponent.java 修改 strictCenterAddrCheck 失败告警日志：原仅打 src，改为同时打出"设备dst"与"平台centerAddr"，联调时可直接照日志值对齐两侧配置；mvn -o compile BUILD SUCCESS（23 文件）
5. code-conciseness-check 报告（评分 B，10 项发现：严重2/建议6/可选4，净减约42行）：严重项①reportRawData 走 4 参 processData 丢 senderAddr，致 autoRegisterCidrs 非空时注册帧(CMD=0x00)被 isAutoRegisterAllowed(null) 拒绝、自动注册彻底失效（本轮白名单改动引入的回归）；严重项②三处死代码（computeSignature(UWBDataBaseDTO)、productApi 注入未用、3参 getDeviceInfoByDnAndPk 重载）；建议项含 handleDahua 90行拆分、两处解析尾部重复、intValue 跨类重复、UdpConfig @Builder 与字段初始化互斥产 8 条警告且零调用方、sendPacket 三变量冗余
6. 报告全程未改代码，按用户规矩询问落地范围

## Related files
- /anwei-fire-remote-monitor/module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpComponent.java

## Notes
- 简洁度检查会顺带暴露真回归：本轮 reportRawData 丢 senderAddr 即上轮新增安全配置功能引入的功能性缺陷，"检查冗余"与"核对每条参数透传路径"应合并执行
- @Builder 与字段初始值并存时 Lombok 明确警告 builder 路径忽略默认值；删 @Builder 前先 grep 确认零调用方
- 前端 3 行×2 文件的重复不建议抽 composable——间接层成本高于收益（沿用反过度简化判断）
- CGNAT 是 UDP 方案硬前提：设备若挂 4G 卡后无公网 UDP 出口则只能回退 GB/T 26875 TCP（端口 18888）

## Notes（后续整改轮更新）
- "报告所列改动均未落盘"已不成立：用户指定 4 项（严重①reportRawData 透传 senderAddr 并补齐 4 个调用点、严重②三处死代码清理、建议③handleDahua 拆出 handleRegisterCmd/handleResponse/handleParamResponse 三方法并由 90 行降至 53 行、建议④抽出公共尾部 reportParsed）均已实施；mvn -o clean compile BUILD SUCCESS，UdpComponent.java 从 869 行降至 851 行。
- 尚未实施的清洁度项：intValue 跨类重复、UdpConfig 删 @Builder、sendPacket 三重中间变量、IpRateLimiter.size() 死方法；代码累计 10 个文件仍未提交，提交信息待用户确认。
- reportRawData 修复使 autoRegisterCidrs 真正生效：配置该白名单后设备出口 IP 必须落在网段内，否则日志报"来源不在自动注册白名单，拒绝注册设备"。
