---
title: "包保责任人园区推送临时注释与跨库SQL变更清单建立"
usage_scenario:
    - "恢复包保责任人园区推送前核对改动范围"
    - "登记或核对上正式库的SQL变更"
    - "判断某库连接属于测试还是正式环境"
keywords:
    - "包保责任人推送停用"
    - "待上正式库变更清单"
    - "正式库误写回滚待定"
    - "tab缩进CRLF探测"
    - "sys_oper_log环境活动比对"
source: "auto"
---

## Task description
- Core requirement：两个连续任务。①用户要求"先把推送注释"——把包保责任人的园区平台上报全部临时注释掉（测试期不允许真实外呼），以便本地验证测试用例 21（改电话保存）；②用户要求确立 SQL 变更口径并"帮我记录好，后续我要上正式库修改"——涉及 SQL 的修改要直接查正式库现状，并维护一份待上正式库的变更清单。
- Task background：hc-standard（RuoYi）项目处于测试期，约束是不得向园区平台真实推送；包保责任人后端的园区外呼集中在 PlatementImplementPersonServiceImpl（新增先推后插、修改调 syncToPlatform）；此前会话已在开发库执行过 fix_implement_person_menu.sql 的两条菜单变更；正式库尚未拿到任何变更脚本的登记台账。

## Execution process
1. Grep 检索仓库内既有"停用上报/临时注释"写法无先例，改为自定统一标记：`// ===== 园区推送临时注释（测试期不推送园区平台；恢复时取消本段注释）=====`。
2. 用 node 逐行打印 JSON.stringify 探测目标文件真实字符，确认全文为 tab 缩进 + CRLF 换行（PowerShell 的 Get-Content -replace 在该 Node fallback shell 下输出为空，不可依赖），据此保证 SearchReplace 精确匹配。
3. PlatementImplementPersonServiceImpl.java 三处注释：新增方法内"先推平台成功再落库"整段注释并改为直接 insertPlatementImplementPerson 落本地；update 方法内 syncToPlatform(...) 调用注释；syncToPlatform 方法整体注释。每处带上述可恢复标记。
4. `mvn -DskipTests -q compile -pl ruoyi-system -am` 编译通过（EXIT=0）；ServiceException / JSONObject import 与 api 字段变为未使用，仅告警不影响编译。
5. 盘点 ruoyi-admin/src/main/resources/sql 目录全部脚本；读取 file/ddl_prod.txt、ddl_prod2.txt 确认其为 SHOW CREATE TABLE 导出记录（内含 220 地址），不能作为环境定性依据。
6. 查 sys_oper_log 做环境活动比对（220 当天 system1 有大量测试操作、160 当天 admin 有联调操作），据此误判 220 为测试库，并把 fix_implement_person_menu.sql 执行到 220，用 HEX(menu_name) 校验中文无乱码（2549=包保管理、2562 visible=0）。
7. 用户纠正"220才是正式库"：确认 220 的 root 凭据来自 application-druid.yml 中被注释的那组数据源（即助手确有写权限），向用户坦白已误写正式库并给出 A（回滚）/ B（保留）两种处理方案等待定夺。
8. 新建 ruoyi-admin/src/main/resources/sql/待上正式库变更清单.md，分三块：变更记录（每条注明各库执行状态）、存量脚本索引（含空文件标注）、部署提醒（重点标注包保责任人 3 处临时注释须在正式部署前恢复）。

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/PlatementImplementPersonServiceImpl.java
- /ruoyi-admin/src/main/resources/sql/待上正式库变更清单.md
- /ruoyi-admin/src/main/resources/sql/fix_implement_person_menu.sql

## Notes
- 环境定性不能凭 MCP 连接名或库名推断：两个实例库名都叫 tgr-gz-vue，正确锚点是 application-druid.yml 中生效数据源的 IP（160=测试库）与被注释数据源（220=正式库）；本次因把 220 当测试库而向正式库执行了写操作，属需避免的事故。
- "临时停用上报"在本项目沿用全量注释而非配置开关（与本仓既有决策一致），但必须在注释中标明恢复方式，并在变更清单里挂"部署前恢复"提醒，否则易随发版遗漏。
- 注释掉推送后页面"同步状态"列会长期显示"未同步"，属预期现象而非缺陷；测试期产生的数据在恢复推送后不会自动补推。
- PowerShell 在该非交互 shell 下对管道/变量处理不稳定（探测命令输出为空），需要看到原始字节时改用 node -e + JSON.stringify 逐行输出。

## Task overview
部分完成：包保责任人 3 处园区推送已全部注释并通过编译验证，测试期新增/修改/删除均为纯本地、不外呼；《待上正式库变更清单》已建立并登记菜单变更与脚本索引。遗留两项待用户指示：①220 正式库上被误执行的两条菜单 UPDATE 选择回滚（A）或保留（B），并同步修正清单中的库别标注；②正式部署前需取消包保推送的 3 处临时注释。本轮改动仍在工作区未提交。

## Notes
- 【2026-09-23 补充·遗留项①已闭环】Task overview 中"220 正式库误执行的菜单 UPDATE 待定回滚或保留"已有结论：用户选择保留，清单第 #1 条（包保菜单显示+改名）已标注为"正式库已生效"，不再需要回滚操作。
- 【2026-09-23 补充·清单已完成全量正式库复核】后续会话对 220 做了逐项只读核对并更新清单：batch_no 列、包保 sync_status/sync_result 列、专项检查扩展字段、第三方单位/装置管控/大检修两表与菜单权限均已确认存在于正式库；当前唯一待用户在 220 执行的变更为 move_platform_menus_to_platement.sql（4 个平台对接类菜单仍挂在 parent_id=2000 基础管理下）。遗留项②（包保 3 处 + 气体检测 2 处临时注释恢复）仍未处理，属非 SQL 部署提醒。
