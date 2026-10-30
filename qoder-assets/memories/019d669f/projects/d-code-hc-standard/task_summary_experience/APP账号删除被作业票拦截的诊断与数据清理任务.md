---
title: "APP账号删除被作业票拦截的诊断与数据清理任务"
usage_scenario:
    - "删除用户账号提示存在作业票被拦截时排查"
    - "清理某账号名下特殊作业票及子表数据"
    - "判断作业票是否已上报平台再决定删除方式"
keywords:
    - "删除用户拦截"
    - "curOperatorId"
    - "base_work_special子表清理"
    - "sync_status上报失败"
    - "MCP读写权限差异"
    - "作业票物理删除"
source: "auto"
---

## Task description
- Core requirement: 用户（sgd）提供 APP 账户 17329995095（昵称 sss，技术部门，创建于 2026-08-10 11:39:06），要求删除其名下作业票。
- Task background: 用户在管理端删除该账号时被系统拦截提示"该用户存在作业票，不允许删除"；需先定位拦截代码与其校验口径，再查清作业票数据规模与平台同步状态，最后按用户选定的方式清除数据并解除拦截。

## Execution process
1. Grep 提示语文本定位拦截点：SysUserServiceImpl.deleteUserByIds() 调 baseWorkSpecialMapper.selectCheckUserIdCount(userId)，计数 >0 即抛 ServiceException；对应 SQL 为 `base_work_special where deleteMark = 0 and curOperatorId = #{userId}`（curOperatorId 存 sys_user.user_id 数值，非手机号）。
2. 读两个 MCP 的 tools/mysql_query.json 确认写权限差异：hc-standard-test（开发库 192.168.250.160）支持 INSERT/UPDATE/DELETE；tgr-gz-vue（220.203.23.144）为 READ-ONLY，故任何删改只能落在开发库。
3. 两库并行查 sys_user（用 user_name='17329995095'）：开发库 user_id=876/昵称 sss/create_time 与用户描述完全吻合，测试库 user_id=877/昵称"测试"为同号不同账号 → 锁定开发库为目标环境。
4. 逐轮摸清数据全景：主票 1 条（outerId=ebc4ef19...、workNo=ZY-126081012520732、ticketsType=1、workStatus=0 未开始、deleteMark=0）；SHOW COLUMNS 发现该表用 sync_status/sync_result 记录上报结果，本票 sync_status='2'（上报失败，原因为作业人员信息格式错误）、systemFileIds 为空 → 园区平台侧无此票，删除不会造成本地/平台不一致。
5. 子表排查（关联键统一为 workId=outerId）：base_work_approve 6 条、base_work_step 16 条、base_work_video 1 条（videoPath 为 null、sync_status=0）、base_work_certificate / base_sign_record / base_work_sensor 均 0 条；账号侧 sys_user_role、sys_user_post 均 0 条，base_risk_task 无关联，base_contractor_people 无该手机号人员 → 纯 APP 账号。
6. 对照系统自身删除语义：BaseWorkSpecialServiceImpl 的删除是逻辑删除（set deleteMark=1）并级联清视频/证书子表；据此向用户提供"逻辑删除 vs 物理删除""仅删票 vs 票+账号"两组选项。
7. 用户选定"物理删除 + 票与账号一并处理"后执行：先删三张子表（6/16/1 行），再删主表（1 行），最后删 sys_user user_id=876（1 行）。
8. 一条 UNION/子查询 COUNT 做残留验证：user_residue / ticket_residue / approve_residue / step_residue / video_residue 全部为 0。

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysUserServiceImpl.java
- /ruoyi-system/src/main/resources/mapper/base/BaseWorkSpecialMapper.xml
- /ruoyi-system/src/main/resources/mapper/base/BaseWorkVideoMapper.xml
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java

## Notes
- 首次查询误用 `u.phonenumber` 报 Unknown column（本项目 sys_user 无该列），按手机号定位账号只能用 user_name。
- MCP 的 SQL 解析器拒绝 `SHOW TABLES LIKE ...`（报 Expected "#", "--", ";"...），必须改用 information_schema.tables/columns 查表名与列名。
- base_sign_record 用下划线列名 unique_key，而 base_work_special 系列用驼峰 outerId/workId/deleteMark，同库混用命名风格，写 WHERE 前须先确认列名。
- 高风险删库操作在动手前用 AskUserQuestion 给出方案与影响（是否影响平台数据、可否恢复）并取得明确选择，避免默认按"推荐项"执行。

## Task overview
已完成：定位到删账号被拦截的根因（base_work_special 中 deleteMark=0 且 curOperatorId=876 的 1 条动火作业票），确认该票从未成功上报园区平台后，按用户选择在开发库物理清除主表 1 条 + 审批 6 条 + 步骤 16 条 + 视频 1 条，并删除 sys_user 账号本身；删除后全量残留计数为 0，管理端刷新即不再显示该账号。测试库同号不同 ID 的账号未做任何改动。
