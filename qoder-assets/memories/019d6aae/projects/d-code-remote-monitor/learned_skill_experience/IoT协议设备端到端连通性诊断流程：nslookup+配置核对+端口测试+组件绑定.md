---
title: "IoT协议设备端到端连通性诊断流程：nslookup+配置核对+端口测试+组件绑定"
usage_scenario:
    - "当诊断IoT设备无法建立TCP/UDP连接时"
    - "为新IoT协议设备生产部署做连通性验证"
    - "排查设备端到端数据无法上行或命令无法下发"
keywords:
    - "IoT协议"
    - "connectivity diagnosis"
    - "port mapping"
    - "firewall rules"
    - "host binding"
source: "auto"
---

## Goal
Diagnose end-to-end connectivity for IoT protocol devices (TCP/UDP) to ensure successful data ingestion and command execution.

## Steps
Step 1: nslookup - Verify the online server's public IP address and domain name, rationale: The device-side platform address must match the server's public endpoint; without this, all subsequent checks are invalid, input: domain name, output: public IP address
Step 2: fetchMemory - Retrieve the current component configuration from the eiot_component database table, rationale: The database stores the authoritative configuration for which ports the components listen on; this is the source of truth for server-side setup, input: none, output: component config JSON
Step 3: Test-NetConnection - Perform external TCP port reachability tests, rationale: Confirms that the port mapping in docker-compose.yml and the cloud security group/firewall rules are correctly configured; this is the first line of defense for external connectivity, input: server IP + port, output: connection status (True/False)
Step 4: Phase check - Confirm that the server-side configuration, port mapping, and external connectivity are all verified; if the external test fails, the issue is either in the docker-compose mapping or the firewall rules; if the external test passes but the device still cannot connect, the issue is likely on the device-side configuration or network
Step 5: Read - Inspect the component's configuration class (e.g., GbtTcpConfig.java, UdpConfig.java), rationale: Confirms the expected 'host' binding address and other protocol-specific fields (e.g., centerAddr); this ensures the server is listening on the correct interface, input: component config class path, output: host binding and protocol parameters

## Notes
- Configuring the component 'host' as '127.0.0.1' causes external device connections to fail; must configure it as '0.0.0.0' to bind to all interfaces
- UDP port 48888 requires explicit firewall rule configuration on both the cloud security group and the server OS level, unlike TCP ports; failing to configure both will result in silent packet drops
- centerAddr 的真实运行值来自数据库 eiot_component 表的 config JSON 字段（经 JsonUtils 解析为 UdpConfig/GbtTcpConfig），不是 deploy/.env 注入；代码里的默认值仅是 JSON 缺失该字段时的 fallback。设备侧"平台中心地址"必须与该配置值逐字节对齐。
