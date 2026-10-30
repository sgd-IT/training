---
title: "大屏统计接口前端调用方分布与 duty-screen 端点空置事实"
usage_scenario:
    - "改造大屏统计接口缓存或限流时确定影响面"
    - "排查超管切换租户后统计数字不变化"
    - "判断某个后端端点是否还有前端调用方"
keywords:
    - "dashboard/statistics"
    - "duty-screen/statistics已下线"
    - "大屏统计接口调用方"
    - "轮询间隔60s与30s"
    - "活跃租户预热ZSet"
source: "auto"
---

消防大屏统计接口的前端调用方分布（grep + 生产访问日志双向核实）：alarm-screen、duty-screen、monitoring-center 以及 src/test/baidu-map 四处调用的都是同一个 `/eiot/firewatch/dashboard/statistics`，因此对该接口做的租户级缓存与并发闸门会同时影响这三块正式大屏。命名陷阱：后端 `FireDashboardController` 里另有一套 `/eiot/firewatch/duty-screen/*`（对应 getDutyScreenStatistics），字面看像"值守大屏专用"，实际前端零调用；2026-10-30 该方法的接口声明、实现与 Controller 端点三处已一并注释下线，改造时不必为其增加任何缓存分支，但要注意注释方法必须连 Controller 调用点一起处理，否则编译报"找不到符号"。

各页面轮询间隔并不一致（实测代码值，勿统一按 30 秒假设）：duty-screen/index.vue 是 setInterval(loadData, 60000)，monitoring-center/index.vue 与 alarm-screen/index.vue 是 30000（useVisiblePolling）。生产近 24h statistics 稳定约 182 次/小时，正好等于 duty-screen 60 次 + monitoring-center 120 次，可用作轮询组数反推校验。据此，缓存新鲜期取 70 秒的理由是"盖住最慢的值守大屏 60 秒周期"，而非"盖住 30 秒轮询"。

tenantId 透传口径：duty-screen 请求 statistics 时会带 params，且仅在「超管 + 已选租户」时才传 tenantId；monitoring-center / alarm-screen 不传。故后端按实际生效租户分缓存 key 的口径必须与这条透传参数保持一致，否则超管切租户会读到同一份缓存。但线上实测 4442 次调用中 97% 的 request_params 为 {"query":null,"body":null}（完全不带 tenantId、全部 user_id=1 超管），即绝大多数流量落在 all 全量视角那份 key 上——定时预热优先刷 all 收益最大。判据来源是前端各 .vue 的调用点、后端 controller 注解与 infra_api_access_log 聚合，而非文档约定。

补充（定时预热场景）：因四个页面共用同一端点，一次租户级预热即可同时覆盖三块大屏，无需按页面分别刷；缓存 key 的后缀就是生效租户 id（全量视角用 all），所以"活跃租户集合"里存的成员应是这个后缀本身而不是完整缓存 key，预热侧按后缀拼 key 才能命中；且活跃时间只能由真实前端请求写入，预热侧若回写会自我续命使窗口失效。

补充（2026-10-30 预热任务落地后的结构事实）：预热周期 @Scheduled(fixedDelay = WARM_UP_INTERVAL_MILLIS) 取 60 秒，硬约束是必须小于缓存新鲜期 70 秒，否则预热完请求进来仍判过期；每轮跑完须把 30 分钟窗口外的成员从 ZSet 移除，否则集合只增不减；单个租户预热失败只记 error 不中断整轮。已知遗留瑕疵两处（不影响功能，改造该类时可顺手清理）：FireDashboardWarmUpTask.java 第 3 行 import TenantIgnore 全文只出现这一次即 import 本身，属死 import；getDutyScreenStatistics 是以注释形式保留而非删除，接手人若看到注释掉的 @GetMapping 不要误判接口仍可用。
