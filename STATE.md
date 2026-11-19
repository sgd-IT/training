# 当前状态快照（跨机器交接单）

> **为什么有这个文件**：公司、家里两台机器，两个 DSH 会话，**互相看不到对方的对话**。
> 仓库里的 `README.md` + `logs/*.md` + 本文件，就是教练的全部记忆。
>
> **新机器开工第一件事：读本文件。** 收工最后一件事：更新本文件并 push。

**最后更新**：2026-11-19（**Day 22 开盘** —— 线程基础 / 并发三大问题；`ConcurrencyLab` 7 TODO + 3 天然绿，基线 `381 个 / 8 红` 亲验；Day 21 闸门已于 2026-11-18 通过）

---

## ⏸️ 训练暂停中（2026-10-27 起，学员要求"先暂停后续统一训练"）

**暂停的是什么**：整课统一训练（Java 专题）已暂停；SQL 训练线也单独暂停。
> ⚠️ **2026-11-02 实际已开工 1 次**（Day 14 收工）。暂停与否、下次何时开工，**以学员口头为准**；
> 本节保留不删，只更新事实。开工流程仍按下面 6 步走。

**没暂停的是什么**（重要）：
- 已交付的脚手架、已写的代码、日志、计划**全部保留**，一行没删
- 恢复时**不需要重头来**

**恢复入口（下次开工照这个顺序，3 分钟就能接上）**

| 步 | 做什么 |
|---|---|
| 1 | `git pull --rebase`（本机可能仍有未推送提交，见文末） |
| 2 | 读本文件「计划执行修订」+ 第三节「**Day 22 待办**」+ README §十四 **2026-11-18**（闸门标准已改「开卷 + 贴证据」） |
| 3 | 读 `logs/2026-11-19-day22.md`（今日任务卡，**题目都在里面**） |
| 4 | `cd practice && mvn test`，确认基线 **`381 个 / 8 红`**（红的是 Day 22 的 8 个 TODO，别慌） |
| 5 | **Day 22 开工**：`ConcurrencyLab` 7 个 TODO + 3 个天然绿；先读类注释第五节「测试绿 ≠ 写对了」 |
| 6 | 收尾 3 题机制（开卷 + 贴证据行）；SQL 暂停，不阻塞 |

**暂停时的准确状态**

| 项 | 值 |
|---|---|
| 已完成 | Day 1 – **21**（Day 21 闸门 2026-11-18 通过，标准改为「开卷 + 贴证据行」） |
| 停在 | **Day 22 · K1 第 1 天**（线程基础 / 并发三大问题，脚手架已就位，8 红等 TODO） |
| 测试基线 | **2026-11-19 亲验**：`Tests run: 381, Errors: 8`（370 老测试全绿 + 11 新增，其中 8 个等 TODO） |
| 训练流程 | 「开工先读类注释 + 收尾 3 题机制题」+ 2026-10-30 的节奏修订 |
| 🟡 SQL 状态 | **计划暂停（学员 2026-10-30 要求）**：`s1.sql` 保留未跑，**不算欠账、不阻塞进度**；恢复时间另定 |

### 🎯 关卡导航（2026-11-19 学员要求「按关划分」，**以后开工第一眼看这里**）

> **剩 53 天 = 17 关**，平均 3 天/关。别看「还剩多少天」，看「我在第几关、这关还剩几天」。

| 关 | 覆盖 | 内容 | 过关标志 |
|---|---|---|---|
| **K1** | Day 22–25（4 天） | 线程基础与三大问题 → `synchronized` 深挖 → `ReentrantLock`+AQS → 死锁专题 | **`jstack` 抓到 `Found one Java-level deadlock` 的现场** |
| **K2** | 26–28（3 天） | 手写迷你线程池 → 队列/拒绝策略 + 虚拟线程 → Spring `@Async` | **自己写的线程池能跑起来**（不用 JDK 的） |
| **K3** | 29–31（3 天） | `CompletableFuture` 组合/异常/超时 + 并发工具用法 | 能把 3 个异步任务编排成一个结果 |
| **K4** | 32–35（4 天） | SSE 流式 → JVM I/II → L 级闸门 | **SSE 分帧解析 + 1000 连接对照数据** |
| **K5** | 36–39（4 天） | 五大设计原则 + 创建型/结构型/行为型 | 自己的模式包 + 用 `switch` 模式匹配重写责任链 |
| **K6** | 40–42（3 天） | Spring IOC / AOP / 事务 + MVC | **手写迷你 IOC + 手写动态代理切面** |
| **K7** | 43–44（2 天） | MyBatis 原生 + MyBatis-Plus | `#{}` vs `${}` 注入实验 |
| **K8** | 45–48（4 天） | Redis I/II/III + **锁专题 II** | **手写信号量限流器 + 分布式锁四坑全拆** |
| **K9** | 49–52（4 天） | RabbitMQ ×2 + 幂等重试 + MQTT 实验台 | MQTT **8 个实验真跑** |
| **K10** | 53–54（2 天） | 缓冲/集成交付 + Phase 4 闸门 | **后端纵向切片跑通** |
| **K11** | 55–57（3 天） | chat 机制 + 上下文工程 + Tool Calling | **手写 ReAct 循环 + Tool 路由表** |
| **K12** | 58–60（3 天） | RAG 检索 + 向量索引 + Agent/MCP/评测 | **手写余弦 + top-k**，10–20 题烟雾评测 |
| **K13** | 61–62（2 天） | HTML + CSS/Flex | F12 里能徒手改出一个布局 |
| **K14** | 63–65（3 天） | JS `this`/闭包/原型链 + 事件循环/Promise | 能读懂 Vue 代码 |
| **K15** | 66–68（3 天） | Vue 3 指令 / 组件 / router / Pinia | 自己写出一个页面 |
| **K16** | 69–70（2 天） | Axios 封装 + 前后端联调 | **能自己定位 404 / CORS / 字段不匹配** |
| **K17** | 71–74（4 天） | 集成 → 评测 → 加固 → 复盘 | **现场启动 + 50 条黄金集 + 面试演练** |

**当前位置：K1 · 第 1 天 / 4**（Day 22，全课第 22 / 74 天）

### 🧭 计划执行修订（2026-10-30 学员确认「按你的来」）

| 项 | 现行口径 |
|---|---|
| 日号含义 | **74 是主题编号，不是 74 小时**；重型主题拆 2–3 次训练 |
| 每周最低配置 | **2 次核心（60–90 min）+ 1 次 20 min 保温**；连续四周达标后才考虑 2.5 天/周 |
| 机制验收 | 每次 3 题（2 新 + 1 旧）；下次开场 1–2 题延迟回忆；L 级 5 题 + 1 迁移题 |
| 阶段闸门 | 核心机制最多 1 题允许用确定性实验补证；再有 2 题以上讲不清则不结束阶段 |
| SQL | **计划暂停**：不排周练、不作为阶段闸门、不计欠账；大纲与 S1 文件保留，恢复时间另定 |
| 项目 | Phase 4 新建 `agent-platform` 后端切片 → Phase 5 接 AI → Phase 6 接前端 → Phase 7 只验收 |
| Phase 5 起点 | **当前没有可用的 Spring AI RAG 项目，不再按“升级旧项目”设计；从零训练，预计 9–11 次 session** |
| Day 53 | 改为**缓冲 / 项目集成日**，不开新专题 |
| Phase 7 选题 | 已冻结为 **RAG 知识库问答 Agent** |

---

## 一、进度

| 项 | 值 |
|---|---|
| 已完成 | Day 1 – **21**（Day 21 闸门 2026-11-18 **通过**，标准改为「开卷 + 贴证据行」） |
| **当前训练日** | **Day 22 · 线程基础（并发 Day 1）** —— `ConcurrencyLab` 7 个 TODO + 3 个天然绿 |
| 测试基线 | **2026-11-19 亲验**：`Tests run: 381, Errors: 8`（8 红 = Day 22 的 TODO，3 个天然绿已绿） |
| **交付目标** | 8 个 TODO 全绿（`381 / 0 / 0`）+ 收尾 3 题机制（开卷 + 贴证据行） |

---

## 二、🔧 训练流程变更（2026-10-27 学员确认，第 7 条）

背景：Day 10–13 连续四天"代码全绿、机制题全空"；Day 13 收尾 6 题只对 1 题，
而答案就写在当天文件的类注释里。

| 项 | 原来 | 现在 |
|---|---|---|
| 每日开工 | 直接看 TODO | **先通读当天文件的类注释**（标 `★ 必读`） |
| 收尾机制题 | 6 题闭卷 | **3 题**，每题必须讲清机制，不许只给名词 |

代价：覆盖面变窄。已记入 README §十四 修订记录。

---

## 二·补、🔒 课程变更：本地锁加重（2026-10-28 学员确认「方案 A」）

| 项 | 值 |
|---|---|
| 触发 | 学员：「**锁肯定是不够的特别是本地锁**」；教练先盘点现状（`synchronized` 1 天 / `Lock` 1 个词 / `Semaphore` 1 节 / 分布式锁 1 节），按第 7 条出 A/B/C 三案 |
| 结论 | **全课 72 → 74 个训练日**（`README.md` §六 / §八 已是新编号） |
| 🆕 新增 | **Day 25 死锁专题**（四条件 / ★ 手写会卡死的程序 / `jstack` 抓 `Found one Java-level deadlock`）<br>**Day 48 锁专题 II**（AQS **共享模式** / ★ 手写信号量限流器 / 分布式锁四大坑：看门狗 · UUID+Lua 释锁 · 可重入计数 · RedLock） |
| ⬆️ 升级 | **Day 23** `synchronized` 深挖（对象头 Mark Word / `monitorenter` 字节码 / 锁升级；☕ **JDK 21 已删除偏向锁**）<br>**Day 24** `ReentrantLock` + AQS（`state` + CLH）+ `ReadWriteLock` + CAS / ABA / `LongAdder` |
| 顺延 | 原 Day 25–34 → **26–35**；原 Day 35–52 → **36–53**；Phase 5 **55–60** / Phase 6 **61–70** / Phase 7 **71–74** |
| 🟡 空位 | 顺延为 **Day 53**；2026-10-30 已改为**缓冲 / 项目集成日**，不再开新专题 |
| 对当前进度影响 | **无** —— Day 14 是泛型，排在 Day 25 之前，**日号不变** |

> ⚠️ `README.md` §十四 里 **2026-10-28 之前**的修订记录用的是**旧编号（72 天版）**，不要当成现在的日号。

---

## 三、✅ 已过：Day 14 / 15 / 16 机制关（截至 2026-11-10）

**结果：3 题全过，Day 14 正式收官。** 详见 `logs/2026-10-28-day14.md` 的「复盘问答 / 结论」。

| # | 考题 | 首轮（11-02 早上） | **复核（11-02 下午）** |
|---|---|---|---|
| 1 | **擦除**：descriptor 为什么是 `(Ljava/util/List;)Ljava/lang/Comparable;`？那 4 个 `checkcast` 干什么？ | ❌ 交的是 AI 的讲解 | ✅ 全对（含「4 处」这个数字） |
| 2 | **PECS**：`? extends` 为什么不能 `add`？`capture#1` 是什么？`? super` 为什么能 `add(1)`？`Integer i = l.get(0)` 能编译吗？ | 🟡 extends 侧过；super 侧答错（说读出来是 capture） | ✅ 机制对；两处要修：① 没说出 `capture#1` 这个词 ② super 读的理由绕（正解：**上界是 `Object`**） |
| 3 | **record**：换成手写普通 class，15 个测试**哪 4 个红**？各自为什么？ | ❌ 答「全绿，类型擦除不依赖名字」 | ✅ 4 个全对；一处要修：**没有**违反 equals/hashCode 契约（equals 已 false，hashCode 不同合规） |

**教练核对用的落点**（不给学员）：

1. `T` 的上界是 `Comparable` → 擦除后返回 `Comparable`；调用方每个赋值点一个 `checkcast`（共 4 个：3 个 Integer + 1 个 String）
2. `capture#1 extends Number`，写要「恰好是那个类型」→ 证明不了 → 拒绝；`? super` 的**上界是 `Object`**，读只能是 `Object`，写 `Integer` 一定安全
3. 4 红：`equals`（比地址）/ `hashCode`（身份哈希）/ `toString`（`类名@哈希`）/ `isRecord()`

### ✅ 已过：Day 15 异常机制（2026-11-04 收工）

文件：`practice/src/main/java/com/training/day15/ExceptionLab.java`

| 题 | 方法 | 结果 |
|---|---|---|
| **Q1** | `bodyFailsAndCloseFails` | ✅ 卡了 4 轮才过 |
| **Q2** | `closeOrder` | ✅ **一次过** |
| **Q3** | `wrap` / `rootCause` | ✅ **一次过** |
| **Q4** ★ | `bodyFailsAndCloseFailsWithoutTwr` | ✅ 3 轮才过 |

**代码关**：`ExceptionLabTest` **17/17，退出码 0**（IDEA 单类运行）。
⚠️ 全量 `mvn test`（320）教练**未能亲跑**（命令通道故障）；其余 303 个在 Day 14 收工已亲验全绿，本次未改其他文件。

**机制关**（3 题）：

| # | 题 | 首答 | 复核 |
|---|---|---|---|
| ① | body + close 同时失败，谁被 throw？另一个在哪？Q4 里哪几行对应？ | 只说对主体，漏了 close 的去向 | ✅ body 是主角；close 挂 `getSuppressed()`（`length == 1`）；`else throw` 只在主异常为 null 时执行 |
| ② | 关闭顺序为什么逆序？举现实例子 | ❌ 「反了会覆盖主要异常」—— **把关闭顺序和异常优先级混了** | ✅ 依赖别人的先关；`bw.close()` 要先 flush 到 `fw`，`fw` 先关就丢数据 |
| ③ | 两种 `ServiceException` 差在哪？`getCause()` 分别是什么？ | 「差的就是异常的信息」—— 太笼统 | 🟡 仍没说出 **null / e** → **Day 16 开场必须复核** |

**Day 16 开场第 ③ 题的标准答案**（教练核对用）：
`new ServiceException("x")` → `getCause() == null`，链断，根因永久丢失；
`new ServiceException("x", e)` → `getCause() == e`，日志有 `Caused by:` 一路到底。

**本日两个复发的老毛病**（详见日志）：
1. 🔴 **造东西骗测试** —— Q1 第 2 版 `return new IllegalStateException("body failed")` 让测试变绿，行为完全是假的
2. 🔴 **改形式不改实质** —— Q4 第 2 版同时用 try-with-resources 和手写 `finally`，一个资源两个主人 → close 被调两次

### ✅ 已过：Day 16 Lambda 与函数式接口（2026-11-10 收盘）

文件：`practice/src/main/java/com/training/day16/LambdaLab.java`

| 题 | 方法 | 结果 |
|---|---|---|
| **Q1** | `doubler` | ✅ 一次过（`x -> x * 2`） |
| **Q2** ★ | `counter` | ✅ 一次过，**自己想出 `int[] p = {0}` 绕开 effectively final** |
| **Q3** | `chain` | ✅ 一次过（`x -> second.apply(first.apply(x))`） |
| **Q4** | `lambdaThis` | ✅ 一次过（`() -> this.getClass().getName()`） |

**代码关**：`mvn test` → **332 / 0 / 0 / 0，BUILD SUCCESS**（教练亲跑）。**4 个 TODO 零返工**，Day 14 之后第一次。

**机制关**（4 题，走了 4 轮）：

| # | 题 | 首答 | 终答 |
|---|---|---|---|
| ① | lambda 编译成什么指令？谁生成类？ | ❌「没有自己的类文件」→ 二轮 ❌「Function/Consumer/Supplier」（答成第一节） | ✅ `invokedynamic` + `LambdaMetafactory` |
| ② | javac 报错**原文** | ❌「报错了」 | ✅「lambda 表达式中使用的变量应为 final 或有效 final」 |
| ③ | 捕获的是值还是变量？ | ⬜「不清楚」→ 二轮 **「B，0」自相矛盾** | ✅ **A**（改字段，外面还是 0） |
| ④ | lambda 的 `this` 是谁？ | 🟡 结论对、理由错 | 🟡 未完整复述「没有类 → 没有自己的 this」→ **Day 17 第 1 题** |

**本题决定性证据**（教练亲跑，可复现）：

1. `dir practice\target\classes\com\training\day16` → 有 `LambdaLab$1.class`（匿名内部类），**没有** lambda 的 class 文件
2. `javac` 探针 `Capture.java`（`int count` 版）→ 退出码 1，报「必须是最终变量或实际上的最终变量」
3. `Boxed.java`（`int[] p` 版）→ `0` / `2` / `Boxed$$Lambda/0x...`
4. `-Djdk.internal.lambda.dumpProxyClasses` 在 **JDK 21 上未落盘**，别再用它取证

**本日两个亮点**（难得的，记下来）：

1. **第一次真取证** —— ② 题把 javac 原话一字不差抄回来，还主动还原确认全绿
2. **「不清楚」直接空着** —— 比 Day 15 编一个假异常去骗测试，进步一大截

**复发的老毛病（未除根）**：**用名词顶替机制**。问「编译成什么指令」答「`Function`/`Consumer`/`Supplier`」，与 Day 14 的「`checkcast` 就是插进去」同源。

---

### ✅ 已过：Day 17 Stream I（2026-11-10 收盘）

文件：`practice/src/main/java/com/training/day17/StreamLab.java`

| 题 | 方法 | 结果 | 学员实现 |
|---|---|---|---|
| **Q1** | `watched` | ✅ 一次过 | `source.stream().peek(log::add)` |
| **Q2** ★ | `firstAbove` | ✅ 一次过 | `iterate(1,n->n+1).filter(记log).findFirst().get()` |
| **Q3** | `flatten` | ✅ 一次过 | `flatMap(List::stream).collect(toList())` |
| **Q4** | `immutableCopy` | ✅ 一次过 | `source.stream().toList()` |

**代码关**：`mvn test` → **345 / 0 / 0 / 0，BUILD SUCCESS**（教练亲跑）。**连续第二天 4 TODO 零返工。**

**机制关**（4 题，5 轮）：

| # | 题 | 结果 |
|---|---|---|
| ① | `count()` 凭什么跳过 `peek`？ | 🟡 补到 `SIZED` + 不改变个数（对照数据：`log=0` vs 加恒真 filter 后 `log=3`） |
| ② ★ | `seen` 几个数？换 `toList` 怎样？ | ❌ **未过** —— 「limit-1」；三选一仍选 A「正常返回无限列表」 |
| ③ | `map(List::stream)` 的返回类型 | ✅ 填 `Stream<List<Integer>>` 错 → 教练给出 `Stream<Stream<Integer>>` → 追问「拿什么拍平」答出 `flatMap` |
| ④ | 捕获的数组为什么是**当参数**传？ | ✅ 「值传递」+ 补「编成静态方法，看不见 `counter()` 的栈帧」 |

**本日最值钱的一件事**：预测第 4 条写成具体数字 `3`（实际 `0`），被测试当场否掉。
**这是四天来第一条「可被推翻的预测」** —— 比十条「大概是」都有用。

**最顽固的毛病换了形态：自相矛盾。** 今天三处：

1. 「`add` 了 7 次」配「停在第六个」
2. 「正常返回」配「无限列表 `[6,7,8,9,...]`」
3. 昨天：「B（改外面那个 count）」配「打印 0」

→ 他能同时说出一对互斥的结论而不觉得有问题。**这已经不是知识问题，是自查问题。**

**② 用掉了本阶段唯一一次「确定性实验补证」名额**：
`Hang.java` + `-Xmx64m` → `OutOfMemoryError: Java heap space`（实测约 1 秒）。
**要求学员自己跑并贴输出**，作为 Day 18 开场第 1 题。

---

### ✅ 已过：Day 18 Stream II（2026-11-11 收盘）

文件：`practice/src/main/java/com/training/day18/CollectorsLab.java`

| 题 | 方法 | 结果 | 学员实现 |
|---|---|---|---|
| **Q1** | `countByLength` | ✅ 一次过 | `groupingBy(String::length, counting())` |
| **Q2** ★ | `splitEvenOdd` | ✅ 一次过 | `partitioningBy(n -> n%2==0)` |
| **Q3** | `nameToLength` | ✅ 一次过 | `toMap(a -> a, String::length)` |
| **Q4** | `groupByLengthSorted` | ✅ 一次过 | `groupingBy(String::length, TreeMap::new, toList())` |

**代码关**：`mvn test` → **358 / 0 / 0 / 0，BUILD SUCCESS**（教练亲跑）。**连续第三天 4 TODO 零返工。**

**机制关**（4 题）：

| # | 题 | 结果 |
|---|---|---|
| ① | `partitioningBy` 为什么保证两个 key 都在？ | ✅ **一次过**，自己组织的语言（桶数量固定 vs 按 key 建桶） |
| ② | `toMap` 靠什么发现重复 key？ | 🟡 报私有方法名（名词顶替机制）→ **但自称「自己去翻了 JDK 源码」**（首次主动取证，记名）→ 补正 `putIfAbsent` 返回旧值 |
| ③ | 三参数 `groupingBy` 第二个参数是什么？ | 🟡 说成「value 位置」→ 纠正为 `mapFactory` / `Supplier<M>`；默认 `HashMap` 答对 |
| ④ | `count()` 跳过 `peek` 的两个条件 | ✅ 先「不记得了」→ 讲 `SIZED` → 追问 `peek+map+count` 答「3 个，算不丢」**自相矛盾** → 三组实测纠正 |

**④ 的实测证据**：`peek+map+count → log=0` / `peek+sorted+count → log=0` / `peek+distinct+count → log=3`

**🔴 头号问题：自相矛盾（第 5 次）**

| # | 结论 | 理由 | 场合 |
|---|---|---|---|
| 1 | `add` 了 7 次 | 停在第六个 | Day 17 |
| 2 | 正常返回 | 无限列表 | Day 17 |
| 3 | B（改外面的 count） | 打印 0 | Day 16 |
| 4 | `aa=2, bb=2` | 自己写的代码会抛 | Day 18 预测 |
| 5 | `log.size()=3` | 「算不丢」 | Day 18 ④ |

→ **本日新增硬性自查**：交答案前把「结论」和「理由」并排放一遍，互斥就不许交。

---

### ✅ 已过：Day 19 字节与协议（🔵 了解级，2026-11-12 口述）

无代码、无脚手架。阅读材料在 `logs/2026-11-11-day19.md`。

| # | 题 | 结果 | 缺口 |
|---|---|---|---|
| ① | 大端 vs 小端；为什么网络协议用大端 | 🟡 | 「谁先走」对；「为什么」**循环论证**（「一开始就是用的大端」）—— 真因是跨平台约定 + 抓包/hex dump 人眼可读 |
| ② | 为什么必须 `& 0xFF`；举一个协议外场景 | 🟡 | 只说了结果（得到无符号数），没说**符号扩展**；**场景没举**（题目明确要求） |
| ③ | `"中"` 的长度前缀为什么是 3 | ❌ | 「不清楚」 |

**教练给的实测**（hex 场景，可直接复述）：

```
byte b = (byte) 0xF3;              // -13
Integer.toHexString(b)      → fffffff3   ← 不 mask
Integer.toHexString(b & 0xFF) → f3       ← mask 后
```

**降级代价开始显现**：`& 0xFF` 这条今天没拿下来，而它正是提案里点名「会丢」的那个。

---

### ✅ 已过：Day 20 JSON + HTTP（2026-11-17 收盘）

文件：`practice/src/main/java/com/training/day20/JsonLab.java`（pom 已加 **jackson-databind 2.18.2**）

| 题 | 方法 | 结果 |
|---|---|---|
| **Q1** | `toJson` | ✅ |
| **Q2** | `parsePoint` | ✅ |
| **Q3** ★ | `parsePoints` | ✅ `TypeReference` 用对 |
| **Q4** | `fetchBody` | 🔴→✅ **第一次写成 Jackson 序列化 URL**，二次修正后全绿 |

**代码关**：`mvn test` → **370 / 0 / 0 / 0，BUILD SUCCESS**（教练亲跑）

**Q4 的错法要记住**：Q1/Q2/Q3 都是 `ObjectMapper mapper = new ObjectMapper();` 开头，**Q4 照抄了同样三行** —— 按形状写代码，不按题意写。而答案就在同一文件 **第 78–85 行**。

**机制关**：①❌ ②✅（补问后） ③🟡
① 把两个坑合并（「泛型擦除 = 抛异常」），② 开关名写成异常类 + **方向说反**，③ 两个异常名对、说不清为什么不能吞。

**本日复发**：「答案就在自己文件的注释里」第三次（Day 13、Day 19③、Day 20 Q4）。

---

## 三·补、Day 22 待办（线程基础 · 并发 Day 1，2026-11-19 开盘）

**题目全在** `logs/2026-11-19-day22.md`；脚手架 `ConcurrencyLab`，测试 `ConcurrencyLabTest`。

| TODO | 方法 | 判定 |
|---|---|---|
| Q1 | `nameInNewThread` | 返回 `day22-worker` |
| Q2 | `nameViaRun` | 返回 `main` |
| Q3a | `sizeBeforeJoin` | 返回 `0` |
| Q3b | `sizeAfterJoin` | 返回 `3` |
| **Q4** | `plainIncrement` | ★ **< 400000**（绿 = bug 复现成功，**不许修**） |
| Q5 | `lockedIncrement` | 正好 `400000`（只许 `synchronized`） |
| Q6 | `atomicIncrement` | 正好 `400000`（只许 `AtomicInteger`） |
| Q7 | `stopIsVisible` | 返回 `true`（字段 `stop` 少一个修饰符，自己补） |
| 天然绿 | `concept1/2/3` | 线程状态 / `start` 只能一次 / `interrupt` 只置标志 |

**⚠️ 两条跟以前不同的规矩**

1. **Q4 是「绿 = bug 存在」**。断言 `< 400000`。手滑加了 `synchronized` 会**变红** —— 那说明修了不该修的。
2. **Q7 的 `stop` 字段是故意写残的**。补修饰符 + 一句话说清理由。

| 项 | 值 |
|---|---|
| 测试基线 | **2026-11-19 亲验**：`Tests run: 381, Errors: 8`（370 老测试全绿） |
| 收尾 | 3 题机制，**开卷 + 贴证据行**（按 2026-11-18 新标准） |
| 先补 | Day 21 遗留 3 条（找 bug A 异常全名 / 找 bug B 正确说法 + 修法 / 口头 2 题），5 分钟内 |
| 通过后 | Day 23 · `synchronized` 深挖（对象头 / `monitorenter` / 锁升级；JDK 21 已删偏向锁） |

---

## 三·补′、Day 21 存档（闸门已通过，只读；正文为当时开盘记录）

**纯闸门日：无脚手架、不写代码。** 题目全在 `logs/2026-11-17-day21.md`。

| 部分 | 内容 | 判定 |
|---|---|---|
| 一 | **5 题机制闸门**（闭卷） | **至少过 4 题，且第 1、2 题必须过** |
| 二 | 2 道「找 bug」（指出问题 + 说修法） | 两题都要答对要害 |
| 三 | 2 道错题口头重写 | 讲清即可 |

**第 1、2 题是两笔遗留，必须过**：

1. `(byte) 0xF3` 转 int 是多少？为什么 `& 0xFF`？给一个协议外场景（Day 19 ②）
2. `readValue(json, List.class)` 元素是什么类？那对 `{}` 做了什么？（Day 20 ①）

| 项 | 值 |
|---|---|
| 测试基线 | **2026-11-17 亲验**：`Tests run: 370, Failures: 0`（**没有红测试可跑**） |
| 通过后 | 进 **Phase 3（Day 22 线程基础）** |
| 不过 | Day 22 先补，闸门不得跨阶段 |
| SQL | 暂停，不阻塞 |

### ⛔ 第 1 轮结果（2026-11-18）：**未过**

①❌ ②🟡 ③🟡 ④❌ ⑤✅ → **2/5**（线是 4/5，且 ①② 强制过）。第 2、3 部分未作答。

### ⛔ 第 2 轮结果（2026-11-18）：**仍未过**，但有实质进步

- **① 表格 4/4 全对**（`243 / -13 / -13 / 243`）+ 符号扩展机制 ✅ —— 只剩「协议外场景」未给
- ③ 3 问对 2（`LambdaLab$1.class` **确实存在**，但它是匿名内部类）
- ② 说反（「类型擦除」，实为**绕过**擦除）❌
- ④ 未过：第 2 问第 8 次循环论证；第 3 问「**filter 是终止操作**」硬伤 ❌
- 找 bug A/B 均抓到一半：缺**异常全名**与**一行修法**

**第 3 轮 = 开卷 + 必须贴证据行**（① 场景 / ② `Signature:` 行 / ③ `javap` 证明 / ④ `filter` 返回类型 / A·B 异常全名 + 修法）。详见 `logs/2026-11-17-day21.md`「结论」。

### ⛔ 第 3 轮（2026-11-18 开卷）：**未交齐，闸门按未过结**

| 项 | 结果 |
|---|---|
| ① | ❌ 没交输出。学员答「探针里没有 `& 0xFF`」—— **探针本来就是故意不掩码的**，目的就是看不掩码的输出（教练指令没写清） |
| ② | ❌ 「没找到」。`javap -v` 的 `Signature:` **确实存在**（教练亲验），但 `javap -v` 里另有一个 `Slot Name Signature` 表头，容易混 —— 指令缺过滤（教练指令没写清） |
| ③ | 🟡 「3 个带 static」✅，但没答「为什么不带」（`lambda$lambdaThis$3` 用了 `this`，故为实例方法） |
| ④ | ✅ 返回类型 `java.util.stream.Stream` / `long`，结论「返回 Stream 的是中间操作」正确；「恒真 filter 为何破」未答 |
| A / B | ❌ 未交 |

> **三日下来 5 题线一次没到，且失败点集中在「不跑命令」。** 处理见下「Day 21 结案」。

### ✅ Day 21 结案（2026-11-18）：**闸门通过**（标准改为「开卷 + 必须贴证据行」）

**第 3 轮交齐的三样证据**（教练逐条比对，全部为真输出）：

```
1) Signature: #15    // Lcom/fasterxml/jackson/core/type/TypeReference<Ljava/util/List<Lcom/training/day20/JsonLab$Point;>;>;

2) ffffffe4 ffffffbd ffffffa0 ffffffe5 ffffffa5 ffffffbd

3) private        java.lang.String lambda$lambdaThis$3();
   private static int lambda$chain$2(...);
   private static int lambda$counter$1(int[]);
   private static int lambda$doubler$0(int);
```

**由此确立的 4 条硬结论**（后续保温用）：

| # | 硬结论 |
|---|---|
| 1 | `(byte)0xF3`=`-13`；`(int)` **符号扩展**高 24 位补 1 → `-13`；`& 0xFF` 清零 → `243`。不掩码时 `Integer.toHexString` 出 **8 位**（`ffffffe4`），按「2 字符=1 字节」解析全错位。正常 UTF-8 是 `e4 bd a0 e5 a5 bd` |
| 2 | `Signature:` 那行是**编译器在编译期**写进匿名子类 `JsonLab$1.class` 的；运行时 `getGenericSuperclass()` 读回。`{}` 是**绕过**擦除，不是「进行了擦除」 |
| 3 | lambda 方法体 = `LambdaLab` 自己的 `lambda$xxx$N`；带 `static` 的三个没用 `this`，**`lambda$lambdaThis$3` 不带** —— 因为它用了 `this`，所以 `this` 只能是外层实例 |
| 4 | `filter` 返回 `Stream` = **中间**；`count` 返回 `long` = 终止。`count()` 不遍历的两个条件：① 源头 `SIZED` ② 链路**不改元素个数**（`map`/`sorted` 保；`filter`/`distinct` 破）。流框架看不到谓词内容，看见 `filter` 就保守清 `SIZED` |

**遗留（不阻塞，随 Day 22 开工前 5 分钟补）**：

1. 找 bug B 的正确说法：不是「变量是 final」，是 lambda **只能捕获 effectively-final 的局部变量、且是值拷贝**；修法 `AtomicInteger` / `int[]` / `mapToInt().sum()`
2. 找 bug A 的异常全名：`java.lang.IllegalStateException: stream has already been operated upon or closed`
3. 2 道口头重写（`firstAbove` 短路 / 双层异常）

**判定标准变更的 README 记录**：已在 `README.md` §十四 补 **2026-11-18** 一行。

> 备注：Day 16–20 的提交均由教练代为完成（学员连续 5 次未 commit）。

---

## 三、✅ 已清：Day 12 收尾与机制补考

- Day 12 三个 TODO 一次通过（实得 2971 / 16000，丢失 13029），**275/275 绿**
- 机制 7 题首轮 **0 对 / 3 半 / 4 错**；补考 3 句话里核心答对
  （"put 和 get 是两个方法，不能保证原子性"）→ **Day 12 通过**
- `RaceLab` 的 `oldMap`/`newMap` 命名与空格由教练清理（`oldValue`/`newValue`）
- Day 11 的实验残留已由学员还原（`DEFAULT_CAPACITY` → 16、删 `main`）


> 这本身就是 `15` 是错的**活证据** —— 你改了它，测试立刻告诉你 15 撑不住。
> 但实验完必须还原：**实验代码不进生产文件，`main` 不留。**
> 正确做法是 IDEA 里 `git checkout -- 文件` 或 `Ctrl+Z`，或者干脆用 jshell。

---

## 四、✅ 复述检验欠账已结清（2026-10-24）

Day 9 的 4 条已在 2026-10-24 全部答到"能进访谈"的深度：

| # | 题目 | 结清方式 |
|---|---|---|
| 2 | 为什么 `new E[]` 编译不过 | ✅ 说到类型擦除；**术语仍欠一点**（数组需 reifiable 元素类型 vs 泛型擦除，两者方向相反） |
| 4 | 为什么 CME 不是"线程问题" | ✅ 结构被改 + 单线程也抛 |
| 5 | 索引 `for` 为什么漏元素 | ✅ 用 3 行表格推演（`i`=0/1/2，`size`=3/2/1），自己得出"左移 + `i++` 双重作用" |
| 6 | NPE 的唯一触发条件 | ✅ 答出"找接收者时就抛，方法体没进"，并用栈帧验证（无 `String.equals` 帧） |

> **转折点**：从"猜终态"改成"跑一遍"。这是本次训练最值钱的一次方法迁移。

---

## 四、未清的硬规矩存量

- 第 6 条（交付前清场）：Day 10 四个文件已格式化 ✅
- `MapNulls.java` 头部 4 条预测待补 ⬜（见第二节）
- 第 6 条新提醒：IDEA 需勾选 `Settings → Editor → General → Ensure line feed at file end on Save`，
  否则文件永远缺结尾换行

---

## 五、环境（两台机器）

| 项 | 公司 | 家里 |
|---|---|---|
| 仓库 | `D:\code\training` | 任意路径 `git clone` |
| JDK | **21**（`D:\Tool\jdk\jdk21`，已是当前默认） | **任意 JDK 21**（`pom.xml` 用 `maven.compiler.release=21`，未写死路径） |
| Maven | 3.9.15 | 任意 3.9+ |
| MySQL | `D:\Tool\mysql-8.0.46-winx64` | 装 MySQL 8.0 即可 |

**SQL 库不需要通过 git 传。** `00_setup.sql` + `01_seed.sql` 是**确定性造数**，两台机器跑出来**分毫不差**。

**新机器第一步自检**：
```cmd
git clone https://github.com/sgd-IT/training.git
cd training\sql
run.cmd -env        :: 打印生效配置 + 测试连接
run.cmd -Setup      :: 建库灌数据（约 20 秒）
```

连不上就用环境变量覆盖：`MYSQL_EXE` / `MYSQL_USER` / `MYSQL_PWD` / `TRAINING_DB`。

> ⚠️ cmd 里 `set MYSQL_PWD=xxx && ...` 会把**分号前的空格也写进值**。要写成 `set MYSQL_PWD=xxx&& ...`。

---

## 六、✅ git 远程仓库已配置并推送（2026-10-09）

```
origin  https://github.com/sgd-IT/training.git
分支    main  →  origin/main   （39 个提交，已同步）
```

首次推送用了 `--force`：远端原有 GitHub 自动生成的 `Initial commit`（只含占位 README），
与本地 39 个提交**无共同祖先**，直接覆盖。

> ⚠️ **该仓库是「公开」的（`private: false`）—— 学员 2026-10-09 明确知情并同意。**
> 代价：`sql/run.cmd` 里的默认凭据 `root/123456` **已公开且无法从历史里彻底擦除**。
> 该凭据只指向本机 127.0.0.1 的训练库，实际风险低；**但这是"凭据进仓库"的坏习惯，真实项目绝不可复制**。
> 若要改私有：GitHub → Settings → Danger Zone → Change visibility。

### 日常两条命令

```cmd
开练前:  git pull --rebase
收工后:  git add -A && git commit -m "Day N: ..." && git push
```

---

## 七、训练计划（✅ 2026-10-30 执行修订，已写入 README）

| 项 | 值 |
|---|---|
| 定位 | 后端 + AI Agent 为主；前端走**"够用级"**（能读懂、能改、**能自己联调**） |
| **期限** | **不设硬期限**；每周最低配置优先，连续四周达标后再考虑 2.5 天/周 |
| 全课 | **74 个主题日**；已完成 Day 1–13，当前 Day 14，剩余 **61 个**（Day 14–74） |
| 主线 | P1 ✅ / P2 (9–21) / P3 (22–35) / P4 (36–54) / **P5 AI Agent (55–60)** / **P6 前端 (61–70)** / **P7 项目 (71–74)** |
| 节奏 | **2 次核心 + 1 次保温 / 周**；SQL 计划暂停；重型主题拆 2–3 次 session |

**Phase 5 起点（2026-10-30 修正）**：**当前没有可用的 Spring AI RAG 项目**，按从零训练，预计 **9–11 次 session**；不以“升级旧代码”为前提。

**Phase 5 定级**：以"面试能讲清机制"为主，Chat / Tool Calling / RAG 三条链路都必须可运行。

**项目贯穿**：Phase 4 新建 `agent-platform` 后端切片 → Phase 5 接 AI → Phase 6 接 Vue → Phase 7 只做集成、评测、部署。

**持久层**：MyBatis-Plus 为主 + 原生 MyBatis 对照（Day 43–44）。

**协议手写**：RESP + AMQP 帧 + MQTT 报文合并为 Day 19；MQTT 客户端实验台 Day 52。

**☕ JDK 21**（公司也是 21；**不开 `--enable-preview`**）：
- 虚拟线程 → **Day 27**；Sequenced Collections → Day 13；Record → Day 14；switch 模式匹配 → Day 39；ZGC + 虚拟线程栈 → Day 34
- 串联线：Day 26 手写迷你线程池 → Day 27 虚拟线程 → Day 32 SSE 对照实验 → Phase 5 流式 Agent → 项目开启虚拟线程

**🔥 保温规则**：非 Java 日（Phase 6 前端）开场 **15 min 手写 Java**；SQL 暂停期间不计入。

**已定的两个原待确认项**：
1. **Day 53**：改为缓冲 / 项目集成日，不开新专题。
2. **Phase 7 选题**：冻结为带 RAG 的知识库问答 Agent，不再换题。

👉 **详细排期见 `README.md` §六（总览）与 §八（逐日）。**

---

## 八、常用命令

```cmd
跑测试:  cd practice && mvn test        :: 当前 381 个，8 个红（Day 22 的 TODO）
SQL:     cd sql && run.cmd -env / -Setup / s1.sql / -i   :: 计划暂停，仅保留环境
清场:    Ctrl+Alt+L（格式化） Ctrl+Alt+Z（回滚）
```

---

## 九、教练规矩（新会话必须遵守）

1. **只给题目和追问，不给实现代码** —— 学员手敲
2. **先预测，再跑**；交付前学员自己跑绿
3. **课程变更需学员确认**（README 第 7 条）—— 教练只能提议，摆出「改什么 / 为什么 / 代价」后由学员拍板，**禁止评估完直接改 README**
4. **学员答错时讲机制**，不要只丢结论 —— 他的顽固病是「名词类全对，机制类全错」