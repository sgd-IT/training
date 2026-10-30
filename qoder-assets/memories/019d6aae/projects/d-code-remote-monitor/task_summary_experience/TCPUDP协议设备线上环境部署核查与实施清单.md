---
title: "TCP/UDP协议设备线上环境部署核查与实施清单"
usage_scenario:
    - "When deploying TCP/UDP protocol devices to the production environment"
    - "When diagnosing port mapping and firewall issues for IoT protocol components"
keywords:
    - "TCP/UDP deployment"
    - "online environment"
    - "port mapping"
    - "docker-compose"
    - "eiot_component"
source: "auto"
---

## Task description
- Core requirement: deploy TCP/UDP protocol devices to the online environment, ensuring end-to-end connectivity and data ingestion
- Task background: AEP chain is already operational; TCP/UDP chains require port exposure, database configuration, product/device setup, and device-side address configuration; the task involved diagnosing port mapping gaps, verifying component activation, and confirming external connectivity

## Execution process
1. Verified current docker-compose port mappings and identified missing mappings for GBT26875 TCP (8103) and YWN V2 (8840)
2. Confirmed enabled components in the eiot_component database table: GBT26875-tcp (id=8, port=8103), ywn-tcp (id=26, port=8839), udp (id=27, port=48888)
3. Checked server/pom.xml dependencies to confirm active TCP/UDP component modules
4. Performed external TCP port reachability tests using Test-NetConnection, confirming 8839 is reachable and 8103/8840 are not (due to missing mappings)
5. Provided a comprehensive online deployment checklist covering port exposure, database configuration, product/device setup, and device-side configuration

## Related files
- /deploy/docker-compose.yml
- /server/pom.xml
- /module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/model/GbtTcpConfig.java
- /module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/model/UdpConfig.java

## Notes
- Server-side component 'host' must be configured as '0.0.0.0', not '127.0.0.1', to allow external device connections
- UDP port 48888 requires explicit firewall rule configuration on both cloud security group and server OS level, unlike TCP ports
- Device-side 'platform center address' is a protocol-layer field and must match the server's centerAddr configuration

## Task overview
Successfully diagnosed and documented the TCP/UDP online deployment requirements: identified port mapping gaps, confirmed database component configuration, validated external connectivity, and delivered a complete, actionable implementation checklist for production deployment.
