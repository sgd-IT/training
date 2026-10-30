---
title: "需求完成度本地只读实测与开发库batch_no补列执行任务"
usage_scenario:
    - "复查需求27多类型报备缺列阻塞的处理过程"
    - "组织测试期推送类与人工验证分工清单"
    - "在无平台权限时本地起后端做只读接口核查"
keywords:
    - "只读实测"
    - "batch_no补列执行"
    - "多库彻查"
    - "手动测试清单交付"
    - "本地后端验证"
source: "auto"
---

## Task description
- Core requirement：用户确认"1 已完成 / 2 可以 / 3 可以"后，要求核实需求实际落地情况，并把剩余需人工验证的测试项整理成交付清单；随后质疑"需求27 仍卡缺列"的结论，要求再查，并指示"直接在 mcptest 的那个库执行"补列。
- Task background：hc-standard（RuoYi）项目处于测试期，约束是不得向园区平台真实推送；需求27（一报备多类型拆单）依赖 base_ticket_bb.batch_no 列，此前已给出 ALTER SQL 交由用户执行；需求50/14/26/55 等需验证数据链路与页面可用性。

## Execution process
1. 搭建本地只读实测环境：用 `mvn -pl ruoyi-admin -am -DskipTests -q package` 打包得 tgr-admin.jar 后 `java -jar` 起后端（28080）；`spring-boot:run` 因插件前缀解析失败被弃用。
2. 自动登录绕过验证码：GET /captchaImage 取 uuid → `redis-cli -n 2 GET captcha_codes:<uuid>` 取码（返回值带双引号，须 Trim('"')）→ POST /login（admin）→ token 落盘临时文件供后续 GET 请求携带。
3. 只读核查结果：隐患列表 50 条 status=31 且 statusName 逐字节核对为"隐患闭环"，前端该列直接绑 statusName（需求50 通过）；证书列表已返回 phone（需求14 链路就位）；workSpecial 列表 19 条正常（需求26 页面侧）；alarm/checkRecord/versionUpdate/project 列表冒烟全部通过。
4. 定位需求27 阻塞：/base/TicketBb/list 返回 500，报错含 Unknown column 'batch_no' in 'field list'，与 information_schema 查询结果互相印证。
5. 面对用户质疑做三线彻查：160 开发库 base_ticket_bb 共 29 列且无 %batch% 列、该实例全 schema 扫描均无此列；220 测试库已有 varchar(64) 同名列；本机 localhost:3306 MySQL 根本不存在 tgr-gz-vue 库；IDEA 数据源仅配 160 与 220 —— 据此推断用户的 ALTER 极可能执行到了 220。
6. 确认"mcptest"指向：读用户级 MCP 配置定位 hc-standard-test = tgr-gz-vue@192.168.250.160（正是缺列的开发库），而名为 tgr-gz-vue 的 MCP 才指向 220。
7. 执行补列：把 ALTER 语句用 shell 写成 UTF-8 无 BOM 的 sql 文件并归档到 ruoyi-admin/src/main/resources/sql/add_ticket_bb_batch_no.sql，再用 mysql.exe `source` 在 160 执行（EXIT=0）。
8. 校验与复测：新列 ORDINAL_POSITION=24、varchar(64)、可空、COLUMN_COMMENT 的 HEX 与 220 完全一致；重启后端后 /base/TicketBb/list 由 500 变 200（total=12，表内 28 条旧数据 batchNo 为空属预期）。
9. 收尾：按启动日志中的 PID 停掉自己起的 java 进程并确认端口释放（避免夜间定时补推任务触发外呼）、删除临时 token 文件；输出按"用户自测推送类 / 交测试人员 / 企业侧复现"三块分组的手动测试清单，并列出已验完无需重测项。

## Related files
- /ruoyi-admin/src/main/resources/sql/add_ticket_bb_batch_no.sql
- /file/特殊作业票相关修改测试清单.md
- /ruoyi-ui/src/views/base/hiddenTrouble/index.vue

## Notes
- 用户口头说"已完成"不等于目标库已变更：跨环境结论必须靠运行时实证 + information_schema 元数据双向印证，并被用户质疑时应扩大排查范围（所有实例/所有 schema/本地服务/IDE 数据源配置）而不是重复同一查询。
- PowerShell 控制台输出中文会乱码，判断字符串内容改用打印字符 codepoint 或 HEX 比对（UTF-8 字节序列），比目测更可靠。
- Get-NetTCPConnection 定位监听进程的方式在该沙箱下未生效（误报无监听进程），改为从启动日志读取 PID 再 Stop-Process 并以 HTTP 探活复核。
- redis-cli 非交互模式返回的字符串值可能带引号，直接作为登录验证码会导致 token 长度为 0 的静默失败，必须先剥引号并打印 RESP code/msg 便于定位。

## Task overview
部分完成：开发库 batch_no 缺列问题已在用户指定库上执行修复并通过接口复测（500→200），需求27 的本地卡点解除；需求50/14/26/55 及多页面冒烟已完成只读实测且全程零推送；推送联调（26/27/29/32/21）、APP 端真机（32/54）与企业侧流程复现已分组成交付清单待人工执行；本轮改动仍在工作区未提交。

## Notes
- 【2026-09-22 更正·库别定性】本条中"160 = 开发库""220 = 测试库"的称呼不准确：用户后续明确 220.203.23.144 是正式生产库、192.168.250.160 才是当前测试库。因此第 5 步"220 已有 batch_no 同名列"的真实含义是**正式库上早已有该列**，当初把它当作"测试环境的对照基准"来给 160 补列并对齐列定义，方向虽可用但风险判断错位——对齐目标应是正式库结构（这一点恰好成立），而"哪个库可以自由写"的判断则必须反过来：只有 160 可随意读写，220 一律先经用户确认。
- 【2026-09-22 补充·写操作前的强制动作】凡准备对某实例执行 INSERT/UPDATE/DDL 前，先按 IP 地址回到环境映射确认库别（不可凭 MCP 连接名、库名或"今天有没有测试活动"这类间接信号推断），并优先选择测试库执行、把变更登记进《待上正式库变更清单》。
