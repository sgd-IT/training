---
title: "Prod数据库MCP配置添加任务"
usage_scenario:
    - "When configuring MCP access to production environment databases"
    - "When adding new database connections to Qoder's global mcp.json configuration"
keywords:
    - "prod database"
    - "MCP"
    - "mcp.json"
    - "Qoder"
    - "MySQL"
source: "auto"
---

## Task description
- Core requirement: add the current prod database to MCP for direct access
- Task background: application-prod.yml configures prod databases as anwei_mon_aluminum_local (master) and anwei_iot_db_local (iot), but the address 127.0.0.1 is relative to the customer's production server; investigation confirmed that 192.168.250.160 hosts these exact databases with data, making it the appropriate target for MCP configuration

## Execution process
1. Searched and read application-prod.yml to extract the prod database names and connection details
2. Verified that 127.0.0.1:3306 on the local machine does not host the required databases, while 192.168.250.160:3306 does host both anwei_mon_aluminum_local (38 tables) and anwei_iot_db_local (17 tables)
3. Asked the user to confirm whether to add one or both databases to MCP
4. Created and executed a Node.js script to safely append two new MCP server entries (anwei-mon-aluminum-prod and anwei-iot-db-prod) to C:\Users\Administrator\.qoder\mcp.json, using verified connection configuration and default read-only permissions. Authentication information is not retained in memory.
5. Verified the new entries in mcp.json and cleaned up the temporary script

## Related files
- C:\Users\Administrator\.qoder\mcp.json
- d:\code\anwei-internet-java\add-mcp-prod.js

## Notes
- The MCP configuration requires manual reload (IDE restart or MCP panel reconnection) to take effect in the current session
- Both new MCP entries are configured in read-only mode by default (no ALLOW_INSERT/UPDATE/DELETE/DDL); write permissions can be enabled upon request

## Task overview
Successfully added two MCP entries for the prod databases: anwei-mon-aluminum-prod pointing to anwei_mon_aluminum_local and anwei-iot-db-prod pointing to anwei_iot_db_local, both connecting to 192.168.250.160:3306 with verified connectivity and data.

## Notes
- This memory records the initial addition of prod DBs to MCP; subsequent tasks have updated these entries with Tencent Cloud CDB connection information, including OCR error correction and SSL troubleshooting.

## Corrections
- On 2026-09-18, a read-only SELECT DATABASE() through anwei-mon-aluminum-prod returned anwei_mon_aluminum, not the historical local database. The current target must be verified before attributing query results to the customer's local installation.
