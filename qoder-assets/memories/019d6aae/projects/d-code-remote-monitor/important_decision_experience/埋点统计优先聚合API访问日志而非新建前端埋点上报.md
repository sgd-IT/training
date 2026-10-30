---
title: "埋点统计优先聚合API访问日志而非新建前端埋点上报"
usage_scenario:
    - "设计功能使用次数或入口热度统计方案"
    - "评估是否需要新增埋点采集链路"
    - "解读访问日志统计结果的口径局限"
keywords:
    - "infra_api_access_log"
    - "ApiAccessLogFilter"
    - "入口热度统计"
    - "request_url分组"
    - "前端埋点上报"
source: "auto"
---

Conclusion: "展示埋点数据（使用次数/哪个入口频繁）"类需求的数据源选定为直接聚合 infra_api_access_log（API 访问日志），不新建前端埋点表和上报接口；统计口径为接口调用次数按 request_url 分组。
Rationale (trade-off): ApiAccessLogFilter 全局写库、dev/prod 均已开启，零采集改造即可出排行/趋势/用户 TOP；代价是只覆盖"会调接口的行为"且受清理任务保留期约束（实测该任务未在跑，库里留有约 5 个月数据）。私有化部署不引第三方统计 SDK。
Rejected alternatives: 新增前端埋点上报（建埋点表 + 上报接口）——本身合理，能记录纯前端点击并长期保留，但需额外采集工程，在"先看入口热度"诉求下性价比低，留作后续需要更细粒度时再上。
Applicable / expiry conditions: 适用于以接口调用量为口径的使用统计场景；当需求升级为"必须统计不调接口的纯前端点击"或"要求严格长期留存"时须重估，回到埋点上报方案。已知口径边界：0 调用接口统计不到（最少排行最低为 1 次）、大屏轮询接口会霸占最多排行、带路径参数的 URL 按不同 ID 拆行。
