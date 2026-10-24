# 当前状态快照（跨机器交接单）

> **为什么有这个文件**：公司、家里两台机器，两个 DSH 会话，**互相看不到对方的对话**。
> 仓库里的 `README.md` + `logs/*.md` + 本文件，就是教练的全部记忆。
>
> **新机器开工第一件事：读本文件。** 收工最后一件事：更新本文件并 push。

**最后更新**：2026-10-24（**Day 11 脚手架已交付**：264 个测试，243 绿 / 21 红）

---

## 一、进度

| 项 | 值 |
|---|---|
| 已完成 | Day 1 – 10（Day 10 收官 **235/235 全绿**） |
| **当前训练日** | **Day 11 · Map 专题 II（HashMap 底层原理）** —— 脚手架已发，**7 个 TODO 待实现** |
| 下一训练日 | Day 12 · Map 专题 III（HashMap vs ConcurrentHashMap vs Hashtable） |
| 测试基线 | `Tests run: 264, Failures: 0, Errors: 21`（243 绿 + 21 红） |
| **交付目标** | `mvn test` → **264 全绿** |

---

## 二、Day 11 待办（7 个 TODO）

| 题 | 文件 | TODO | 状态 |
|---|---|---|---|
| **Q1** ★★ | `MiniHashMap.java` | Q1-1 `indexFor` / Q1-2 `get` / Q1-3 `put` / Q1-4 `resize` | ⬜ 4 个未实现 |
| **Q2** ★ | `HashSpread.java` | Q2-1 `indexWithoutSpread` / Q2-2 `disturb` / Q2-3 `countUsedBuckets` | ⬜ 3 个未实现 |
| **Q3** | `HashMapFactsTest.java` | 无 TODO | ✅ **只读**，7 个天然绿 |

**Day 11 开场已答完的 6 题**（Day 10 带入，得分 4 对 / 1 半 / 1 错），明细在 `logs/2026-10-24-day11.md`。

**⚠️ 唯一的结构性错误**：`computeIfAbsent` 为什么能直接 `.add()` —— 学员答"因为 add 是用在 list 上的"。
正确是"**它把桶返回出来了**"。这条要在 Q1/Q2 做完时回收。

**同时补**：`MapNulls.java` 头部 4 条预测仍是 `____`（第 5 条硬规矩欠账），
要填的输入是：空 Map / `{a=1}` 上分别调 `increaseByMerge` 与 `increaseOnlyIfPresent`。

---

## 三、✅ 复述检验欠账已结清（2026-10-24）

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
收工后:  git add -A && git commit -m "Day 10: ..." && git push
```

---

## 七、训练计划（✅ 2026-10-09 二次修订，已写入 README）

| 项 | 值 |
|---|---|
| 定位 | 后端 + AI Agent 为主；前端走**"够用级"**（能读懂、能改、**能自己联调**） |
| **期限** | **不设硬期限** —— 参考节奏 **2.5 训练日/周**，有空就多推 |
| 全课 | **72 个训练日**；剩余 **62 个**（Day 11 – 72） |
| 主线 | P1 ✅ / P2 (9–21) / P3 (22–34) / P4 (35–52) / **P5 AI Agent (53–58)** / **P6 前端 (59–68)** / **P7 项目 (69–72)** |

**Phase 5 定级**：**以"面试能讲清机制"为主**（学员选择）—— 全程按机制考 + 闭卷。

**持久层**：**MyBatis-Plus 为主 + 原生 MyBatis 对照**（Day 42–43）。

**协议手写**：RESP + AMQP 帧 + MQTT 报文 **合并为 Day 19 一天**；MQTT 客户端实验台 Day 50。

**☕ JDK 21**（公司也是 21，新语法写出来直接能用；**不开 `--enable-preview`**）：
- **虚拟线程 → Day 26**（并入线程池选型，不加天）
- Sequenced Collections → Day 13；Record → Day 14；switch 模式匹配 → Day 38；ZGC + 虚拟线程栈 → Day 33
- ⭐ 串联线：Day 25 手写迷你线程池 → Day 26 虚拟线程 → **Day 31 SSE 对照实验** → Phase 5 流式 Agent → 项目 `spring.threads.virtual.enabled=true`

**🔥 保温规则**：非 Java 日（Phase 6 前端）开场必须 **15 min 手写 Java 或 SQL**。

**🟡 两个待确认项**：
1. **Day 51 空位**（1 天）：认证鉴权（JWT/Spring Security）· 日志与可观测性（traceId）· 定时任务
2. **Phase 7 项目选题**：当前是"带 RAG 的知识库问答 Agent"，可换

👉 **详细排期见 `README.md` §六（总览）与 §八（逐日）。**

---

## 八、常用命令

```cmd
跑测试:  cd practice && mvn test        :: 期望 235, 0 failures, 0 errors
SQL:     cd sql && run.cmd -env / -Setup / s1.sql / -i
清场:    Ctrl+Alt+L（格式化） Ctrl+Alt+Z（回滚）
```

---

## 九、教练规矩（新会话必须遵守）

1. **只给题目和追问，不给实现代码** —— 学员手敲
2. **先预测，再跑**；交付前学员自己跑绿
3. **课程变更需学员确认**（README 第 7 条）—— 教练只能提议，摆出「改什么 / 为什么 / 代价」后由学员拍板，**禁止评估完直接改 README**
4. **学员答错时讲机制**，不要只丢结论 —— 他的顽固病是「名词类全对，机制类全错」