# 当前状态快照（跨机器交接单）

> **为什么有这个文件**：公司、家里两台机器，两个 DSH 会话，**互相看不到对方的对话**。
> 仓库里的 `README.md` + `logs/*.md` + 本文件，就是教练的全部记忆。
>
> **新机器开工第一件事：读本文件。** 收工最后一件事：更新本文件并 push。

**最后更新**：2026-10-09

---

## 一、进度

| 项 | 值 |
|---|---|
| 已完成 | Day 1 – 9（Day 9 收官 **208/208**） |
| **当前训练日** | **Day 10 · Map 专题 I（用法层）** —— 脚手架已建，进行中 |
| 下一训练日 | Day 11 · Map 专题 II（HashMap 底层原理） |
| 测试基线 | `Tests run: 235, Failures: 0, Errors: 26`（Day 1–9 的 208 全绿 + Day 10 的 27 个里 26 红） |
| **交付目标** | `mvn test` → **235 全绿** |

---

## 二、Day 10 待办（14 个 TODO）

| 题 | 文件 | 状态 |
|---|---|---|
| **Q1** | `WordCount.java` | 🟡 **三个方法已写完**（逻辑全对），**测试还没跑**；`countWithMerge([])` 的预测写错了 |
| **Q2** | `Grouping.java` | ⬜ 未开始 |
| **Q3** | `MapTraversal.java` | ⬜ 未开始 |
| **Q4** | `MapNulls.java` | ⬜ 未开始 |

**Q1 已知问题**：
- 预测 `countWithMerge([])` 写成 `a:0 b:0 c:0` —— 应为 **`{}`**（空列表里没有 a/b/c）
- 待 IDEA `Ctrl+Alt+L` 格式化；文件末尾缺换行

---

## 三、🔴 未还的复述检验欠账（最高优先级）

Day 9 复述检验 10 题只对 4 题。
2026-10-10 已重答一轮（**1 对 / 3 半对 / 2 错**），下面 4 题**答清之前不许开始写代码**：

| # | 题目 | 现状 |
|---|---|---|
| 2 | 为什么 `new E[]` 编译不过 | ❌ **循环论证**（答"因为不支持"） |
| 4 | 为什么 CME 不是"线程问题" | ⚠️ 答成"线程异常" |
| 5 | 索引 `for` 为什么漏元素 | ⚠️ 只报终态 `i=2, size=1`，没讲机制 |
| 6 | NPE 的唯一触发条件 | ❌ 答成"值为空"（**第 3 次错**） |

> **顽固病**：名词类全对，机制类全错。面试官问的恰恰是机制。

---

## 四、未清的硬规矩存量

- 第 6 条（交付前清场）：Day 10 脚手架已自查（无未用 import、无死代码）✅
- `WordCount.java` 待格式化 + 补末尾换行 ⬜

---

## 五、环境（两台机器）

| 项 | 公司 | 家里 |
|---|---|---|
| 仓库 | `D:\code\training` | 任意路径 `git clone` |
| JDK | `D:\Tool\jdk\jdk17` | 任意 JDK 17（`pom.xml` **未写死路径**） |
| Maven | 3.9.15 | 任意 3.9+ |
| MySQL | `D:\Tool\mysql-8.0.46-winx64` | 装 MySQL 8.0 即可 |

**SQL 库不需要通过 git 传。** `00_setup.sql` + `01_seed.sql` 是**确定性造数**（`(n*质数)%范围`，无 `RAND()`），两台机器跑出来**分毫不差**。

**新机器第一步自检**：
```cmd
cd sql
run.cmd -env        :: 打印生效配置 + 测试连接
run.cmd -Setup      :: 建库灌数据（约 20 秒）
```

连不上就用环境变量覆盖，不用改文件：

| 变量 | 默认值 |
|---|---|
| `MYSQL_EXE` | `D:\Tool\mysql-8.0.46-winx64\bin\mysql.exe`（找不到则回退 PATH 里的 `mysql`） |
| `MYSQL_USER` | `root` |
| `MYSQL_PWD` | `123456` |
| `TRAINING_DB` | `training` |

> ⚠️ cmd 里 `set MYSQL_PWD=xxx && ...` 会把**分号前的空格也写进值**。要写成 `set MYSQL_PWD=xxx&& ...`。

---

## 六、⚠️ git 远程仓库尚未配置

`git remote -v` 目前是**空的**。两台机器同步前必须先做：

```cmd
cd D:\code\training
git remote add origin <私有仓库地址>
git push -u origin main
```

**仓库必须是私有的** —— `sql\run.cmd` 里有默认凭据。

### 日常两条命令

```cmd
开练前:  git pull --rebase
收工后:  git add -A && git commit -m "Day 10: ..." && git push
```

训练是单线程的（一次只在一台机器上写），天然不会冲突。

---

## 七、训练计划（2026-10-09 提出，**待学员最终确认**）

| 项 | 值 |
|---|---|
| 定位 | 后端 + AI Agent 为主；前端走**"够用级"**（能读懂、能改、能自己联调） |
| 频率 | **2.5 训练日 / 周**（实测 1.4 —— 这是唯一的硬要求） |
| 软期限 | **2027-03-31** |
| 剩余 | 约 **55** 个训练日 |
| 新增 | 前端 HTML/CSS/JS/Vue3（**10 天**）+ Spring AI（**6 天**）+ 全栈缝合（**3 天**） |
| 换向 | Day 19 MQTT → **JSON + HTTP 客户端**；Day 31 RESP → **SSE 流式响应** |
| 砍掉 | AMQP 帧手写、MQTT M3/M4、JVM 2 天→1 天、手写迷你线程池并入 Day 25 |

详见 `README.md`（重排后）。

---

## 八、常用命令

```cmd
跑测试:  cd practice && mvn test        :: 期望 235, 0 failures, 0 errors
SQL:     cd sql && run.cmd -env / -Setup / s1.sql / -i
清场:    Ctrl+Alt+L（格式化） Ctrl+Alt+Z（回滚）
```