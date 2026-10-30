---
title: "SQL别名撞保留字与adb文本输入空格截断两类脚本陷阱"
usage_scenario:
    - "编写临时JDBC诊断SQL选择列别名时"
    - "用adb向APP输入框写入多词文本时"
    - "排查命令行工具部分生效的静默异常时"
keywords:
    - "MySQL保留字别名"
    - "adb input text截断"
    - "诊断SQL语法错误"
    - "移动端UI自动化输入"
source: "auto"
---

Bug class: 测试驱动脚本中两类静默/报错陷阱——SQL 列别名撞保留字、adb 文本输入含空格被截断。
Root cause: ① MySQL 中 unsigned 是保留字，在诊断 SQL 里写成 `SUM(...) AS unsigned` 直接报语法错误（ExitCode 1），中断整条查询链路；② `adb shell input text` 以空格作为分隔符，传 "work finished check ok" 只会写入第一个词 "work"，UI 上看似输入失败但非空校验仍通过，属静默部分生效。
Fix pattern: ① 诊断/统计 SQL 的列别名一律避开类型关键字（unsigned、key、order 等），用 not_sign、signed_cnt 之类带前后缀的名称；② adb 输入多词文本前先确认目标校验逻辑只要求"非空"，若是则故意只输一个词最稳；确需完整句子时把空格替换为下划线或用 %s 转义。
Reusable lesson: Don't use MySQL type keywords as column aliases in ad-hoc diagnostic queries because the whole query fails with a syntax error and misleads you into suspecting the data; instead pick non-reserved alias names. Applies to JDBC/mysql CLI 临时诊断 SQL 与 adb 驱动的移动端 UI 自动化输入；不适用于 ORM 生成的映射查询和物理键盘手工输入。
