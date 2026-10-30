---
title: "贵州推送access_status过滤条件的环境差异化配置"
usage_scenario:
    - "配置monkey.gz_access_status_filter参数时"
    - "测试环境需要重推已推送数据时"
    - "排查贵州推送数据遗漏时核对过滤值"
keywords:
    - "gz_access_status_filter"
    - "monkey.gz_access_status_filter"
    - "贵州推送重推"
    - "access_status取值语义"
    - "环境差异化配置"
source: "auto"
---

贵州推送功能支持环境差异化配置：生产环境默认使用 access_status=0 过滤「尚未推送」的数据；测试环境通过配置项 `monkey.gz_access_status_filter=1` 可切换为重新推送状态为 1（批次失败后回滚为待重推）的数据，无需修改代码逻辑。

## access_status 取值语义（与「批次全部失败时access_status回滚规范」「贵州推送6类数据access_status=2统一更新规范」保持一致）
- 0：尚未推送（生产环境的默认查询过滤值）
- 1：待重推（批次内全部记录推送失败时由 2 回滚为 1，下次调度重新推送）
- 2：已成功推送（推送成功后统一置为 2）
