---
title: "APP账号掩码根因核查与气体分析园区推送停用任务"
usage_scenario:
    - "排查APP作业票变只读无按钮"
    - "确认某操作是否会误推园区平台"
    - "恢复气体分析临时注释的推送"
keywords:
    - "APP账号掩码根因"
    - "curOperatorId修正877"
    - "气体分析推送停用"
    - "saveOrUpdateSensor三出口"
    - "user_type 01"
source: "auto"
---

## Task description
- Core requirement：三个连续追问。①用户指出"APP 账号不分管理员"，需核实 APP 账号体系与作业票详情权限掩码的真实关系，并修正此前造的测试票；②用户问"气体分析保存按钮不会推送吧"，需确认 APP 保存气体分析是否会产生园区平台外呼；③用户要求"先把推送注释"，需临时断开气体分析的园区上报并保持项目内先例一致。
- Task background：hc-standard（RuoYi）处于需求32/54测试期，约束是不得向园区平台真实推送；上一轮已在160测试库克隆两张动火票（c0de 前缀），负责人 curOperatorId 被设为 1（admin）。

## Execution process
1. 三层代码取证定性 APP 账号体系：SysLoginService.appPwdLogin 只放行 user_type='01'，admin 为 '00' 故登不上 APP；SysUserMapper.xml 的 selectUserVo 确实 left join sys_user_role/sys_role（角色会加载），但实测 sss(877)/余威(732)/高北秀(335) 等 01 账号在 sys_user_role 角色全空 → BaseWorkSpecialServiceImpl 的 isAdminUser()（超管 userId==1 或 role_key=='admin'）在 APP 会话恒 false → 不掩码唯一路径是"登录账号 == 票 curOperatorId"。
2. 据此判定原造数有致命缺陷（负责人=admin 必然被掩码成只读），经 AskUserQuestion 让用户确认实际 APP 登录号后，UPDATE base_work_special 两张票 curOperatorId=877，回查 workStatus 仍为 0/2、deleteMark='0'。
3. 顺带查证 APP 菜单接口 ApiBaseMapper.selectMenuList 只按 menuType 查 base_app_menu，无用户/角色过滤 → 任何 01 账号都能看到"动火作业"入口，账号选择不受限。
4. 气体分析外呼核查：Grep saveOrUpdateSensor 全仓得 3 个出口——BaseWorkSensorServiceImpl.insertBaseWorkSensor（已提交版本即有，落库后无条件调用）、updateBaseWorkSensor（需求32未提交改动新增，原版仅 return）、BaseWorkSpecialServiceImpl.rePushBatchToPlatform:454（整票批量补推工具，入口 PushDataController POST /push/workSpecial，需传日期范围、无前端入口、非定时任务）；最终均 POST http://111.4.80.39:8092/openapi/work/sensor/saveOrUpdateSensor，带 KS-APPKEY/KS-TIMESTAMP/KS-SIGN，删除不上报；PlatementBaseApi.sendPost 不校验返回值，平台报错也照常本地保存。
5. 按包保模块先例把前两处 api.saveOrUpdateSensor 调用块注释，统一标记 `// ===== 园区推送临时注释（测试期不推送园区平台；恢复时取消本段注释） =====`，保留方法体与 api 字段作恢复锚点；第三处判为手工工具保留不动。
6. mvn -DskipTests -q compile -pl ruoyi-system -am 通过（COMPILE_EXIT=0）；GetProblems 仅 4 条未使用告警（ArrayList/BaseWorkSpecial import、baseWorkSpecialMapper、api 字段），属预期。
7. 在《待上正式库变更清单.md》"部署提醒"章节登记该恢复项，与包保履职/包保责任人并列成为第三处待恢复。

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSensorServiceImpl.java
- /ruoyi-admin/src/main/resources/sql/待上正式库变更清单.md
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java
- /ruoyi-framework/src/main/java/com/ruoyi/framework/web/service/SysLoginService.java
- /ruoyi-admin/src/main/java/com/ruoyi/web/controller/push/PushDataController.java

## Notes
- 判断"某操作会不会误推平台"若只读单个 ServiceImpl 会漏：必须先按外呼方法名（saveOrUpdateSensor）全仓检索所有调用点，本轮初次结论就漏掉了 rePushBatchToPlatform 这一处。
- "临时注释即生效"的前提是重新打包部署到测试服务器（130），否则线上跑的旧包仍会推；这是纯后端改动，APP 端无需重装。
- 测试票负责人必须与 APP 实际登录账号一致，不能想当然用 admin；PC 管理员账号在 APP 上根本登不进去。
- 若要测需求54"管理员打开他人票"用例，APP 侧当前无管理员账号，需给指定 01 账号在 sys_user_role 配 role_key='admin'。

## Task overview
已完成：APP"不分管理员"的机制被代码+数据双向证实并落成结论；两张测试票负责人由 1 修正为 877（sss）且回查通过，掩码不再触发；气体分析园区外呼的 3 个出口全部查明，其中 APP 保存链路的 2 处已按先例临时注释、编译通过、恢复项登记入变更清单，第 3 处补推工具经判定保留。遗留：改动未提交 git，需重新打包部署方为零外呼。
