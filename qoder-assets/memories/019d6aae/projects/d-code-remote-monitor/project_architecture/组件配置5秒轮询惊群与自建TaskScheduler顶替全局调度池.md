---
title: "组件配置5秒轮询惊群与自建TaskScheduler顶替全局调度池"
usage_scenario:
    - "排查 @Scheduled 任务并发异常或周期查询风暴"
    - "改造组件配置下发方式或调整轮询间隔前评估影响面"
    - "核对配置生效延迟与前端提示是否同口径"
keywords:
    - "ThingComponent定时轮询"
    - "TaskScheduler bean顶替默认池"
    - "eiot_component全表扫描"
    - "调度惊群最大并发"
    - "配置生效延迟"
source: "auto"
---

协议组件配置轮询的调度结构约束（实测于 Druid 监控 + 日志 + 源码）：

- ThingComponent.config() 标注 `@Scheduled(fixedRate = 5000)`，父类上的该注解会被每个子类 bean 各自注册调度。线上 eiot_component 共 11 行、type 全局唯一，对应约 11 个组件实例，因此每 5 秒产生一轮"按 type 查 eiot_component"的查询风暴：89 分钟窗口内实测 11,793 次（占全部 SQL 执行量 62%），总耗时 18,088ms（占 DB 时间 33.5%）。该量与业务流量无关，进程开着就一直跑。另一份 34 分钟窗口快照为 4,543 次 / 8,745ms / 最慢 14ms / 最大并发 11，可用「次数 ÷ 组件数 × 5 秒」反推统计窗口。
- 惊群成因：ModbusComponent 通过 iot-component-modbus-custom 的 TaskConfiguration 自建了名为 ModbusTaskScheduler 的 ThreadPoolTaskScheduler bean（池大小 = CPU×2）。Spring Boot 在容器中已存在 TaskScheduler bean 时不再创建默认调度池，于是所有 @Scheduled 任务都跑在这个 modbus 池上，11 个任务在同一毫秒并发启动——Druid 里该 SQL 的"最大并发"恰为 11，即组件数本身。日志实锤形如 `[modbus-task-pool-26] ComponentMapper.selectList`。
- 副作用：该 5 秒惊群会与写操作争抢连接池/IO，使本应 <1ms 的 eiot_component 查询最慢冲到 151ms，与 UPDATE eiot_device_info 的 169ms 同量级。
- eiot_component 表仅有 PRIMARY(id)、无 type 索引，`selectOne(ComponentDO::getType, type)` 走全表扫（表仅 11 行故单次成本可忽略）；同时 selectOne 语义要求 type 唯一，跨租户同名 type 会抛 TooManyResultsException。
- 轮询的目的与语义（源码核查）：config() 是配置热加载器，查出自己那行后只比对 status 与 config 两个字符串，未变即 return；变更才调抽象方法 stateChange(enable, config)，子类实现（HTTP/CoAP 等）为 parseObject 新配置 → stopServer() → startServer(cfg)，即每次配置变更必然伴随一次服务重启，中间有短暂断连。没有这套轮询，改配置就得重启整个进程（同时断开 11 个协议组件、规则引擎与 WebSocket）。
- 缓存化的结构性障碍：ComponentServiceImpl 整个类没有任何 @Cache/@Cacheable/@CacheEvict（已 grep 确证），而写路径有四条——updateComponent、createComponent、deleteComponent，以及 ComponentManager.componentDiscover 里的自动建组件；漏任一条即"配置改了永不生效"。且缓存 key 是 type，而 updateComponent/deleteComponent 入参是 id，evict 前须先查出 type 才能删对 key。
- 改造方向（已定稿排序）：首选最小改动 `fixedRate=5000` → `fixedDelay=30000`，既降频 6 倍又因各任务实际耗时不同而自然错峰（并发 11 → 1~2），唯一代价是配置生效延迟至 30 秒，且不影响重启次数（只在真的变更时触发）；其次才是缓存+四条写路径全覆盖失效；彻底方案为事件驱动（项目已有 COMPONENT_DISCOVER_TOPIC 机制可扩展）或单任务批量查询后内存分发，但跨 module-eiot-biz ↔ iot-component-core 模块边界。注意成本模型是「组件数 × 频率」，只要保持每组件各自轮询就随组件数线性增长。
