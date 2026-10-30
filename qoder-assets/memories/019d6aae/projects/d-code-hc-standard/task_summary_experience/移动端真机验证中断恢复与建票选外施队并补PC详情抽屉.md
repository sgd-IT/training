---
title: "移动端真机验证中断恢复与建票选外施队并补PC详情抽屉"
usage_scenario:
    - "在外施队企业下录入并选取作业人员时"
    - "排查选人接口因sync_id为空过滤不到人员"
    - "用 adb 在安卓模拟器上做移动端 UI 真机验证"
keywords:
    - "建票选外施队"
    - "外施队人员录入"
    - "sync_id非空过滤"
    - "status=3可选人员"
    - "作业单位company_type2"
    - "MuMu模拟器adb驱动"
source: "auto"
---

## Task description
- Core requirement：用户要求"移动端真机操作（需求2/5 的 APP 展示、建票选外施队）继续"，随后追问"中断作业要去哪里交互"，最终决定"中断记录查看界面放 PC 详情页"。
- Task background：hc-standard 特殊作业链路横跨三端——PC 后台 ruoyi-ui、Android 移动端 other1（包名 com.sckj.other1）、后端 ruoyi-admin/system；此前需求2（实际开工时间展示）、需求5（中断/恢复）、需求1（建票选外施队）已在测试库与接口层验证过，缺真机 UI 取证；PC 端原本只有列表页 + PDF 预览，无作业票详情视图，中断记录只落库不可见。

## Execution process
1. 环境盘点：adb 不在预设路径，通过占用 5037 端口的进程反查得 D:\Tool\sdk\platform-tools\adb.exe；设备为 MuMu 模拟器 127.0.0.1:7555；本机局域网 IP 恰为 APP 硬编码的 HttpInterface.IP = http://192.168.250.130:28080/，模拟器 ping 通且 /captchaImage 返回 200，无需改地址或换 10.0.2.2。
2. 后端启动失败排障：IDEA 的 devtools 在 mvn 编译途中热重启，出现 IBaseWorkSpecialService bean 找不到、Tomcat 直接 Stopping；核对 ruoyi-system/target/classes 有 475 个 class 且 BaseWorkSpecialServiceImpl.class 存在，判定为编译与热重启竞态而非缺编译，touch RuoYiApplication.class 触发重启后 Started in 7.25s、28080 恢复监听。
3. 需求5 中断/恢复实测：adb input tap 走完全流程——详情页右上角「中断作业」→ AlertDialog 填原因 → 「确定中断」按钮变「继续作业」→ 确认框点「是」恢复；DB 侧 workStatus 2↔5 双向流转、base_work_interrupt 三次记录的 interruptTime/resumeTime 成对回填（时长 40 秒 / 382 秒 / 65 秒）。
4. 需求2 实测：给 base_work_special.actualStartTime 写值后重进现场会签页顶部显示「实际开工时间：2026-10-23 09:30:00」；置 NULL 后该行整体隐藏，正反用例均通过。
5. 需求1 实测：建票表单「作业单位」选择器列出 company_type=2 的外施队企业，选中后统一社会信用代码自动回填只读；因该单位无人员，用 JDBC 插入 status=3、sync_id 非空的人员「王外施」（id=960）后，「作业部门」可选到其班组，监护人弹窗按「焊工」岗位筛选只剩该外施队人员并可勾选回填。
6. 需求8 实测：现场会签页「下载作业票」触发后端 print，产物落在 /sdcard/other/<作业编号>.pdf（不是 /sdcard/Download），pull 回本地用 pdfbox 渲染两页核对版式正常。
7. 缺陷一修复：点「是」后确认框永久卡住（返回键也关不掉）。定位 BaseActivity.showMessageDialog2 只给左按钮绑 dismiss、右按钮直接透传业务回调，而 MyDialogMessage 构造里 setCancelable(false)；改为右按钮包装先 dismiss 再执行回调，顺带修好「作废分析记录」同类卡框。
8. 缺陷二修复：中断票在列表仍显示「进行中」。JobSlipListAdapter 的 tvType switch 缺 case "5"，RecyclerView 复用沿用上一次绑定文本；补 case "5" → 「已中断」并加 default 清空分支防串状态。
9. PC 详情抽屉实现：BaseWorkSpecialController 新增 GET /base/workSpecial/interruptList/{outerId}（权限复用 base:workSpecial:list，不新增菜单权限）；workSpecial.js 新增 listInterrupt(outerId)；index.vue 操作列首位加「详情」按钮（列宽 160→210）并挂出 size 760px 的「作业票详情」抽屉，上半 el-descriptions 直接用行数据展示票面字段，下半「中断记录」表格列中断时间/恢复时间/时长/原因/操作人，未恢复显示橙色「中断中」且时长按当前时间实时算，空数据显示提示文案。
10. 验证与收尾：后端接口实测 code=200 返回 3 条；Browser 子代理逐项核验按钮顺序、抽屉字段、共 3 次、时长格式全部通过且控制台无报错；node .tmp/check-vue.js 模板编译 0 错误；测试期间临时关闭 sys_config 的 captchaEnabled，验证完恢复 true 并清 redis 缓存键。

## Related files
- /other1/app/src/main/java/com/sckj/other1/base/BaseActivity.java
- /other1/app/src/main/java/com/sckj/other1/adapter/JobSlipListAdapter.java
- /ruoyi-admin/src/main/java/com/ruoyi/web/controller/base/BaseWorkSpecialController.java
- /ruoyi-ui/src/api/base/workSpecial.js
- /ruoyi-ui/src/views/base/workSpecial/index.vue
- /.tmp/seed/QueryDb.java

## Notes
- 命令行 `mvn -pl ruoyi-admin compile` 会报 ruoyi-generator:jar:3.8.6 not found（少了 -am），但 IDEA 已自动编译使接口即时生效；打包须用 `mvn -pl ruoyi-admin -am clean package`。
- MySQL MCP 本会话不可用，全程改用 java 单文件源码模式 + mysql-connector-j 8.0.33 jar 直连测试库读写（含造外施队人员、写 actualStartTime、UPDATE sys_config）。
- 前端 dev server 热更新常留旧 bundle，新增按钮看不到需 Ctrl+Shift+R 强刷；Qoder 内置浏览器视图不可见时 Browser 子代理拿不到截图，只能靠 DOM 断言取证。
- 能力边界结论：中断/继续的操作入口只有移动端 APP（PC 端全局搜 interrupt 零命中），且按钮只看票状态不看角色，凡能打开该票的人都能中断，未按需求原文限定监护人；需求3 的双人监护强制校验前后端均未实现。
- 表 base_work_interrupt、列 actualStartTime、字典 b_workstatus 的 5=中断 目前只存在于测试库，上线须与 SQL 脚本同步执行。

## Task overview
已完成：需求1/2/5/8 在 MuMu 真机上全部取证通过；发现并修复 2 个 APP 通用缺陷（确认框右按钮漏 dismiss、列表状态 switch 缺分支），均需重新打包才生效（gradle assembleDebug 两次被取消，未完成打包）；PC 端新增作业票详情抽屉与中断记录接口，后端接口与浏览器 UI 双路验证通过，模板编译无错。遗留：APK 尚未重新构建安装以固化两处 APP 修复；中断记录查看只做在 PC，APP 侧无入口；双人监护强制校验未实现。

## Notes（2026-10-27 追加更正）
- 「MySQL MCP 本会话不可用」只对当时那一次会话成立，不代表常态：后续会话实测 MCP 服务 tgr-gz-vue 可用但**只连正式库 anwei1-0001**，hc-standard-test 已被 disabled（调用报 not found MCPHost severName）；而本地后端 master 连的是测试库 192.168.250.160。因此读写测试库数据仍须走 java 单文件源码模式 + mysql-connector-j 8.0.33 直连，且动手前必须先 SELECT @@hostname 确认落点，切勿把 MCP 结果当测试库实况。
- 本条正文第 5 步的「王外施」只存在于测试库；正式库三家承包商（1039/1040/1041）名下人员均为 0，据此在正式库复现需求1/44 前须先录入外施队人员。
- 「双人监护强制校验前后端均未实现」已过时：validateGuardian（后端只卡新建）+ APP checkGuardianRule 拦截均已实现并在真机取证通过（Toast「监护人至少 2 名：公司内部 1 人 + 外部施工队 1 人」，监护人栏可回填「胡军,王外施」）。
- 新增一条易踩的状态可见性事实：BaseWorkSpecialServiceImpl 详情接口对非管理员且非 curOperatorId 本人的请求会把 workStatus 掩码成 "1"，而列表接口不掩码；存量票 curOperatorId 多为 NULL，导致除管理员外谁都看不到真实状态与「中断作业」按钮，排查"按钮不见了"应先查此掩码而非怀疑连错库。
