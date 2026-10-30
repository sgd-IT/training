---
title: "作业票平台推送operatorId取值错误导致作业人员编码错误的诊断与修复"
usage_scenario:
    - "排查平台返回作业人员编码错误时"
    - "修复作业票推送字段取值错误时"
    - "复盘历史代码引入缺陷或判读补推结果时"
keywords:
    - "作业人员编码错误"
    - "operatorId取值"
    - "定位卡号转换"
    - "字节码验证"
    - "推送修复"
source: "auto"
---

## Task description
- Core requirement: 诊断并修复作业票平台推送时'作业人员编码错误'的根本原因
- Task background: 用户执行删除补推接口（/push/deletedWorkSpecial）失败，57条已删票全部报错'作业人员编码错误'；历史成功票（LD-/DH-系列）均发UUID原值，而失败票也存UUID，矛盾点指向推送过程中的字段转换逻辑

## Execution process
1. 数据取证：通过MCP查询正式库（anwei1-0001），确认5条目标票operatorId均为UUID（e9f73325-…等），排除数据源问题；同步查出园区侧存在重复韩飞（19位数字编码），但这些编码在我们库无对应，属园区历史遗留
2. 代码溯源：git log定位到2026-09-09提交4310cc0引入convertOperatorIdToCardNo方法，并在saveWorkSpecial中调用；但该假设（平台通过定位卡号关联人员）未经实测验证
3. 字节码验证：反汇编线上部署包ruoyi-system-3.8.6.jar，确认rePushDeletedBatchToPlatform方法内确有getOperatorId→convertOperatorIdToCardNo→setOperatorId→startTicket调用链，证实补推发出的是卡号而非UUID
4. 对照实验：核查LD-20260914-0001（同为韩飞UUID）因operatorId被录成重复值（e9f892d1,…,e9f892d1,…）而失败，反向证明单值UUID可被平台接受；同时所有LD-/DH-系列新票持续成功，佐证不转换路径正确
5. 根本修复：在BaseWorkSpecialServiceImpl.java中移除saveWorkSpecial、deleteBaseWorkSpecialByOuterIds、rePushDeletedBatchToPlatform三处convertOperatorIdToCardNo调用，将operatorId恢复为sync_id原值；convertOperatorIdToCardNo方法标记@Deprecated停用；编译验证通过

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java
- /ruoyi-system/src/main/resources/mapper/base/BaseWorkSpecialMapper.xml
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementTicketNewApi.java

## Notes
- 修复后需重新部署，重跑/post/deletedWorkSpecial即可解决'作业人员编码错误'类失败；其余失败类型（workUnitType为空、报备不存在、操作证缺失、多人格式错误）属于各票自身的历史数据问题，与本次bug无关
- 园区侧'重复人员'现象源于其系统对多代编码（19位数字/UUID）的混合存储，非本项目数据问题；建议与园区对账其人员表定义及'作业人员编码'校验所依据的字段

## Task overview
已完成根本性修复：定位到'作业人员编码错误'的根源是推送前将operatorId从UUID转为定位卡号，经字节码与对照实验双重验证后，在三处推送路径中移除转换逻辑，operatorId改用base_contractor_people.sync_id原值；修复已编译通过，待部署验证。

## 补充记录（2026-10-09 交付）
- 已用 JDK17（D:\Tool\jdk\jdk17）执行 mvn -q -pl ruoyi-system -am package -DskipTests 构建成功，产物 ruoyi-system-3.8.6.jar（738KB）复制为工作区根 ruoyi-system-new.jar 供热替换（上线替换 BOOT-INF/lib/ruoyi-system-3.8.6.jar 后重启）；对新 jar 反汇编复核 convertOperatorIdToCardNo 仅剩方法定义、零调用点。
- 同期新增排查结论：平时"保存推送"也在失败，只是 sync_status 只记最后一次推送结果，开工/完工推送（发 UUID 原值）成功会覆盖它；删除推送是最后一道工序，失败无人覆盖所以必然暴露。

## 补充记录（后续批次复测）
- 又一次删除补推返回「总计57条, 成功0条, 失败57条」，其中仍含 17 条"作业人员编码错误"，其余为 workUnitType 必填缺失(约25条)、报备作业不存在(9条)、动火/临时用电缺操作证列表(3条)、作业人员信息格式错误(1条)。
- 该结果与本条修复结论不矛盾：新 jar 尚未部署生效时，"作业人员编码错误"仍会按旧转换逻辑出现；判读此类返回时应先看部署版本，再区分编码类失败（代码缺陷，部署后消失）与其他四类（各票历史数据问题，须逐条修数据）。
- 补充排错经验：接口 code=200 不代表推送成功，业务结果只能以 msg 中的成功/失败统计为准。

## 补充记录（2026-10-09 全项目调用点复核与交付口径）
- 全 workspace（ruoyi-common / ruoyi-system / ruoyi-quartz / ruoyi-admin）grep 复核：convertOperatorIdToCardNo 已无任何调用点，仅剩 @Deprecated 定义；getPositionCardNo 的剩余命中均在非推送链路（BaseContractorPeopleCertServiceImpl 组装证书实体、BaseContractorPeopleServiceImpl 本地落库、LocationTask 人员定位在线过滤、BaseWorkSpecialServiceImpl 列表展示），不构成"卡号进推送"路径。
- api.startTicket / api.uploadTicket 调用点全景确认仍在 BaseWorkSpecialServiceImpl 内的四条业务路径（保存/开工/完工/补推），无新增旁路出口。
- 交付状态必须分三层表述，避免"改完了"被理解为已生效：① 代码层完成（三处转换移除 + 方法停用 + 编译通过）；② 交付层完成（jar 已构建并通过字节码验收）；③ 生效层未完成——需由用户在服务器替换 BOOT-INF/lib 下 jar 并重启，之后重跑 POST /push/deletedWorkSpecial 核对目标票 sync_status 是否变 1。部署后若仍有个别票报证书缺失/字段为空/格式错误，属各票独立历史数据问题，与本次修复无关。

## Notes（2026-10-09 更正）
- 上述执行过程第 3 步"反汇编线上部署包"的表述不准确：实际反汇编对象是本地工作区的 ruoyi-system-3.8.6.jar 构建产物，并非从服务器取回的正在运行包。后续补推复测仍返回 17 条"作业人员编码错误"这一事实恰好说明服务器上运行的仍是引入转换逻辑之前的旧包——即"源码含转换 + 本地包含转换"与"线上包是否含转换"是三件不同的事，不能由本地包推导线上包状态。判读同类问题时，字节码验证只能证明构建产物，线上生效与否必须以部署动作本身与实推返回结果为准。本条其余各步结论不变。

## Notes（2026-10-10 复核）
- 本轮再次 grep 复核：convertOperatorIdToCardNo 全仓仍仅剩 @Deprecated 定义、零调用点，修复内容在工作区完好。
- git log 显示仓库最新提交仍为 2026-09-24，本修复涉及的约 22 个文件（+323/-82）始终处于**未提交**状态。因此判读"是否生效"除原有三层外，还须注意第 0 层：**源码改动尚未进入任何 commit**，若此后发生 git checkout/reset/clean 或他人拉取，修复会丢失。复用本条时先确认改动是否已提交、已打包、已换 jar，三者缺一不可。
