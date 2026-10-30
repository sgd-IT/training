---
title: "三端发布前环境指向核查与平台对接菜单SQL上线"
usage_scenario:
    - "多端发布前核对各端服务器指向"
    - "正式库菜单结构调整SQL执行与验证"
    - "终端噪音导致执行结果被质疑时取证"
keywords:
    - "三端发布"
    - "IP指向核查"
    - "平台对接菜单迁移"
    - "sys_menu落点验证"
    - "待上正式库清单"
source: "auto"
---

## Task description
- Core requirement：用户启动"三端更新"（后端 ruoyi-admin / PC ruoyi-ui / APP other1）发布，依次要求①输出发布前核对与更新清单；②"检查3端的ip是否都指向220"；③授权修改 APP 的 IP；④执行待上正式库的 SQL；⑤质疑"你确定你执行成功sql了？？"要求证实。
- Task background：目标为线上库 220.203.23.144（anwei1-0001），测试库 192.168.250.160；《待上正式库变更清单.md》中唯一未执行项是 move_platform_menus_to_platement.sql（4 个平台对接类菜单仍挂在 parent_id=2000 基础管理下）；RuoYi 动态路由在登录时构建。

## Execution process
1. 发布前核对：查 220 的 base_contractor 列结构确认 del_flag（char(1) default 0）与 sync_status/sync_result 已就位，读变更清单锁定唯一待办 #2 菜单移动，交付三端顺序（后端 jar→PC dist→APP APK 最后发）+ 备份提醒 + 发布后回归点清单。
2. 三端 IP 核查（Read .env.production + Grep other1/后端配置）：后端 application-druid.yml 已被切为 220 生效、160 注释；PC 走 VUE_APP_BASE_API='/prod-api' 相对路径（真实落点在部署机 nginx，仓库无 nginx.conf）；APP 的 HttpInterface.java 第 20 行仍为 http://192.168.250.130:28080/（测试机），是唯一未切换项。
3. 引用面确认后再动手：Grep HttpInterface\.IP 证明全工程仅 RetrofitUtils.baseUrl 一处消费（FragmentJobSlipSign 的 contains("192.168.0.200") 特判无关），改一行即够。
4. 改 APP IP：SearchReplace 用不含行首空白的片段 String IP = "http://192.168.250.130:28080/"; → http://220.203.23.144:28080/（因 grep 与 Read 对缩进显示不一致，故意从 String 起匹配以保证唯一命中）。
5. 执行 SQL 前先预检：一条 bash 内合并 SELECT @@hostname + 按 component LIKE 拉 4 个菜单现状 + 定位 path='platement' AND menu_type='M' AND parent_id=0 得 2503，确认落点唯一。
6. 执行方式选择：4 条 UPDATE 拼成单条 -e 命令会超 500 字符限制，改用 mysql --default-character-set=utf8mb4 -e "source d:/.../move_platform_menus_to_platement.sql"，一次拿到脚本自带 BEFORE/ROOT/AFTER 自检输出。
7. 独立复验：再查 parent_id=2503 得 7 个菜单按 order_num 1~7 排列（重大危险源/包保管理/企业信息 + 新移入的服务记录2698=4、第三方单位2703=5、装置开停车2711=6、大检修2719=7）。
8. 回写台账：SearchReplace 更新《待上正式库变更清单.md》——#2 状态由"⬜ 待执行"改为"✅ 2026-09-28 已执行（parent_id 2000→2503，order_num=4~7，脚本自检通过）"，头部"最后更新"同步为 2026-09-28。
9. 应对质疑：用户看到满屏 System.ArgumentOutOfRangeException/哎呀 报错怀疑失败，遂做第四轮实测——用 `2>&1 | Select-String -NotMatch "Warning|PSReadLine|System.Argument|哎呀|..."` 过滤噪音取干净表格，并补查 visible/status 列确认 4 个菜单均为 visible=0（显示）、status=0（正常），定位为终端渲染层报错而非 MySQL 报错，并给出界面侧排查顺序（重新登录>确认是 220 环境>角色授权）与自查 SQL。

## Related files
- /other1/app/src/main/java/com/sckj/other1/http/HttpInterface.java
- /ruoyi-admin/src/main/resources/sql/待上正式库变更清单.md
- 220 线上库 sys_menu（4 行 parent_id/order_num 更新，经 source 脚本执行）

## Notes
- PowerShell PSReadLine 在该非交互 shell 下渲染长命令输出会反复抛 System.ArgumentOutOfRangeException 并把内容打散，极易被误读为"命令执行失败"；判断真假要看输出尾部的真实结果表，必要时用 Select-String -NotMatch 过滤已知噪音关键词重跑一次取证。
- 向生产库交付"执行成功"结论时，仅凭一次性输出不够：需再做一轮独立查询（换维度：parent_id/order_num 之外再查 visible/status），因为 visible=1 或 status=1 会让数据改动在界面上完全不可见，从而出现"库里改了、用户看不到"的对不上现象。
- 跨工具读取同一文件时行首缩进的显示可能不一致（grep 结果看似无缩进、Read 显示 4 空格），SearchReplace 的 original_text 应避开行首空白只取唯一片段，避免匹配失败。
- 多条 UPDATE 直接拼进 mysql -e 会撞命令长度上限，脚本文件已在仓库时用 source 一次执行更短且能复用脚本自带的 BEFORE/AFTER 自检。
- 本机 datasource 切到正式库后，开发机 IDEA 重启后端即直连 220，必须提醒停止对本机 28080 做测试写操作。

## Task overview
已完成：三端 IP 指向全部核实并对齐 220（后端已切、PC 判定为部署侧 nginx 事项、APP 源码改 1 行待构建 APK）；《待上正式库变更清单》最后一项待执行 SQL 已在 220 执行并四轮验证通过（4 菜单 parent_id 2000→2503、order_num 4~7、visible=0、status=0），清单状态同步登记为已执行；针对用户质疑给出了噪音来源解释与界面侧排查路径。遗留：APK 需重新构建（other1.jks 签名）并装机验证能出登录页验证码；发布后需在 220 服务器核对 nginx 的 /prod-api 反代；本批改动尚未 git 提交。
