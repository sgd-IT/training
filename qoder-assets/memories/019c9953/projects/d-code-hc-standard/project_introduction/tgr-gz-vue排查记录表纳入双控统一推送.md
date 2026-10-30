---
title: "tgr-gz-vue排查记录表纳入双控统一推送"
usage_scenario:
    - "定位双控数据各模块对应的具体数据库表名"
    - "排查记录推送失败时确认目标表是否正确"
    - "跨前后端协作时明确数据归属前端项目"
keywords:
    - "tgr-gz-vue"
    - "base_risk_task_record"
    - "双控推送"
---

双控数据中的排查记录表 `tgr-gz-vue.base_risk_task_record` 已纳入统一推送接口 `/push/riskTask` 的第7步执行，按日期范围全量推送（不限 sync_status 和 check_status）
