---
title: "乱码Java备份文件的字节级恢复与报告服务注释改造"
usage_scenario:
    - "恢复被误判加密的乱码源码备份时"
    - "用git历史版本回填丢失字符时"
    - "补充周期报表注释口径时"
keywords:
    - "UTF-16LE BOM"
    - "GBK误解码双层乱码"
    - "git历史版本锚点回填"
    - "编码恢复脚本"
    - "月报服务注释口径"
    - "沙箱文件视图隔离"
source: "auto"
---

## Task description
- Core requirement：把 _temp/_old_service.java（FireMonthlyReportServiceImpl 的历史快照备份）从"看起来被加密"的乱码状态恢复成可读正常源码；随后按 code-comment-style 评审结论对仓库现行源码执行注释改动。
- Task background：该备份是月报服务的旧版本快照，Read 工具显示第一层乱码、终端显示第二层乱码，用户误判为"加密"；仓库同名文件已随提交 961bfcf 泛化为月/季/年共用模板（1266 行），备份仅 1063 行且中文全部不可读。

## Execution process
1. 定性损伤机制（非加密）：node 直读字节发现文件头 ff fe = UTF-16LE BOM（PowerShell 5.1 重定向默认产物），其内层文本又是 UTF-8 字节被按 GBK 误解码的结果，形成双层乱码；GBK 无法配对的奇数字节被 `?` 兜底，共 204 处丢 1 字节并吞掉约 19 个换行（1063 行 vs 原文 1082 行）。
2. 建立反查表：枚举 GBK 全部双字节组合构建"字符→原始字节"映射，把乱码文本逐字翻回字节流再按 UTF-8 解码，中文即恢复。关键修补：UTF-8 中的 0x80 字节被 GBK 单独解成欧元符 €，映射表缺该条目导致"一"等字被错解成"丢"，补 `€ → 0x80` 后丢字点从 338 处降到 204 处。
3. 补齐丢失字节：以 `git show a1c666d:<该文件路径>` 取 donor 文本做参照，先归一化行尾（备份 CRLF、git LF，否则锚点全不匹配），再把损坏点（FFFD 及紧邻的 `?`，必须一并计入损坏段，否则锚点永远落空）前后的中文上下文在 donor 中定位，用中间原文回填；200/204 处自动补齐，含被吞的换行。
4. 残余手工修正：数据中心"N 天"图表说明行与 setPeriodText 一行整行按 donor 回正；再逐行与 donor 比对，抓出 8 处因回填错位产生的重复片段（如 "0.0%");" 出现在应为破折号的分支、在线率文案多出引号）按相似度 ≥0.75 一律回退为 donor 原文。
5. 落盘与校验：node 一次性以无 BOM UTF-8 + CRLF 写回目标文件，原始乱码字节另存 _temp/_old_service.utf16le.bak；校验结果为 1082 行 / 56400 字节、Grep 乱码字符集与 U+FFFD 均 0 命中、与 a1c666d 版本逐行比对 0 行差异。
6. 注释执行：对仓库现行 FireMonthlyReportServiceImpl.java 只做注释改动 13 处（+14/−6），新增租户上下文必要性、Map 键名形态不一致预警、location 复用 name 说明、LIMIT 2000 抽样上限、details 格式两处硬编码互指、AI 两段切分降级说明、sanitizeAiText 清洗清单补全，并把 3 处过期的"本月/上月"措辞改为"本期/上期"。
7. 注释验证：git diff 逐 hunk 确认全是 `//` 与 `*` 行；`mvn -o -pl module-eiot/module-eiot-biz -am -DskipTests compile` → BUILD SUCCESS（41 模块、Compiling 584 source files）。未提交（工作区另有他人 WIP，等用户确认）。

## Related files
- /_temp/_old_service.java — 恢复后的目标文件（覆盖写入）
- /_temp/_old_service.utf16le.bak — 原始乱码字节副本
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireMonthlyReportServiceImpl.java — 注释改动落点

## Notes
- 沙箱视图隔离再次出现：node 写入的 _old_service_decoded.java 在终端侧 readdirSync 完全看不到，而 Read/SearchReplace 能看到并修改，跨通道引用中间产物会 ENOENT；因此最终把"解码—回填—写盘"合并进同一个 node 脚本一次完成，不再让工具与终端交替读写同一临时文件。
- Grep 与 Read 对同一行的缩进呈现可以不同（Grep 输出前导空格被裁），判断空白/缩进问题必须以 Read 为准，否则会误加修复。
- 试错代价集中在对齐策略上：先后失败于"按 ASCII 骨架精确匹配 donor 行"（损坏使骨架本身变化）、"按整段 clean 文本 indexOf"（备份与 donor 存在版本措辞差异）、以及漏把 `?` 并入损坏段，三轮才收敛到"小窗口前后文定位 + 相似度回退"。教训是编码恢复类任务应先跑一次最小实验确定损坏规则（BOM 层、误解码层、丢字节层各是哪一层），再设计对齐策略。
- 单模块 `-pl` 编译报大量"找不到符号"（AlertApiImpl/RemoteConsoleController/UdpEventNotifier）与本次改动无关，是 m2 兄弟模块 jar 过期所致，带 `-am` 即通过；管道过滤 mvn 输出会使返回码变 1，成败只看 BUILD SUCCESS 行。

## Task overview
已完成：_temp/_old_service.java 恢复为 UTF-8（无 BOM）、CRLF、1082 行、无一处乱码，内容与 git 提交 a1c666d 的同名文件逐行 0 差异，可判定为该提交的完整快照；仓库现行文件的 13 处注释改动全部落地并通过 Maven 编译。剩余事项：仓库注释改动尚未提交，需用户确认后再走提交流程；_temp 下的 .bak 副本可由用户自行清理。
