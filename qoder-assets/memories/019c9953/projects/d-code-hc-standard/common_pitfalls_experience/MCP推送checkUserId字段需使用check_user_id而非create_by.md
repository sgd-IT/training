---
title: "MCP推送checkUserId字段需使用check_user_id而非create_by"
usage_scenario:
    - "MCP同步失败且sync_result提示联系管理员"
    - "检查记录推送后状态异常，需核对字段映射逻辑"
    - "修复MCP数据同步问题时定位到用户ID字段传值错误"
keywords:
    - "MCP"
    - "checkUserId"
    - "字段映射"
    - "同步失败"
---

MCP系统推送检查记录时，checkUserId字段必须传数据库中的check_user_id值（如'691,693,840'），而非create_by字段的用户名（如'李小姑'）；否则因ID格式非法导致同步失败并返回'出错了，请联系管理员'。（来源：CallMcpTool）
