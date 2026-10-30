---
title: "小程序视频起播优化选按需预热、https反代缓行"
usage_scenario:
    - "决定视频保活方案的触发形态时"
    - "评估小程序是否需要HLS反向代理时"
    - "起播优化方案复审或升级时"
keywords:
    - "按需预热"
    - "定时保活"
    - "https反代"
    - "体验版实测"
    - "方案取舍"
source: "auto"
---

Conclusion 1：消控室监控起播优化采用"小程序首页 onShow 触发后端预热（按需保活）"为主方案，7×24 后端定时保活降级为可选升级。
Rationale (trade-off)：主流用户行为是"打开小程序→浏览首页 20s~几分钟→点视频"，预热可用浏览时间隐藏 15-30s 建流耗时且按需零常驻成本；秒点场景仍有窗口，若不可接受再叠加一个定时器即可（两者共用同一保活引擎，升级成本约等于一个 cron 注解）。
Rejected alternatives：直接上 7×24 定时保活（覆盖无死角但常驻流量）；web-view+WSPlayer 重型方案（热流已能 3-5s，没必要）；折腾 ICC 分片参数（热流快说明分片不是矛盾）。
Applicable / expiry conditions：当 ICC 空闲释放超时较短、预热保持时长盖不住用户停留间隔，或出现要求任何时刻秒开的领导级使用场景时，应升级为定时保活。

Conclusion 2：nginx https 反代暂不实施，先以体验版实测 http 直连 HLS。
Rationale (trade-off)：video 组件的 src 属媒体资源不受小程序域名白名单校验，http 明文只是终端兼容性风险而非配置违规；体验版不能跳过网络校验、最接近正式版，可低成本定夺。
Rejected alternatives：上线前直接配反代（确定性最高但需半天工作量，且对起播速度零改善）。
Applicable / expiry conditions：体验版或正式版出现部分 Android 机型播不了、或微信收紧明文媒体策略时，反代必须补做（改动点：nginx 加 /hls/ location 反代 7086 端口 + 后端 live/url 返回值前缀重写为 https 域名）。
