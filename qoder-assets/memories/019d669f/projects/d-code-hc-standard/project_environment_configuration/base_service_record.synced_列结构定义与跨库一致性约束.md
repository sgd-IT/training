---
title: "base_service_record.synced 列结构定义与跨库一致性约束"
usage_scenario:
    - "排查同步路由逻辑时核对synced列是否存在及初始化正确"
    - "多环境部署后校验数据库列结构一致性"
keywords:
    - "base_service_record"
    - "synced"
    - "column schema"
    - "cross-database consistency"
    - "mysql"
source: "auto"
---

base_service_record 表的 synced 列定义：`synced varchar(2) DEFAULT '0' COMMENT '是否已成功上报过平台(0否1是，用于区分新增save与修改update)'`，位于 sync_result 之后。历史核对记录显示该列在正式库（220.203.23.144）与测试库（192.168.250.160）均存在且结构、注释一致；当前维护时仍需重新核对实际环境。synced 是 save（'0'）/update（'1'）路由的历史成功标记，瞬时失败不应清零。跨库一致性以分别只读查询列名、类型、默认值及注释为准，不能由单库结果推断另一库。历史数据初始化依据 sync_status='1' 识别已成功上报记录。环境权限口径：160 是测试库；220 是正式库，未经用户确认不得写入。涉及数据库变更时核对正式库兼容性，并登记待上正式库变更清单。
