---
title: "外施队人员作业票与定位上报人员ID匹配链路诊断与改造计划"
usage_scenario:
    - "排查作业票推送报作业人员编码错误的根因"
    - "设计外施队人员进入园区定位表的改造方案"
    - "评估新增HTTP定位上报接口替代MQTT通道"
keywords:
    - "外施队作业票上传"
    - "园区人员定位基础信息表"
    - "operatorId与personId同源"
    - "syncContractorPeople覆盖contractor_id"
    - "userLocation addBatch"
    - "作业人员编码错误"
source: "auto"
---

## Task description
- Core requirement：用户贴出园区平台【推荐】HTTP 接口 /openapi/userLocation/addBatch（人员实时定位数据）文档，最终收敛为确认"外施队（承包商企业）人员在作业票上传时必须匹配到园区人员定位基础信息表的人员ID，否则推送失败"，并要求先商讨计划、不改代码。
- Task background：现有定位上报走 MQTT（LocationTask 拉外包定位系统 getTagPos → broker 111.4.80.39:8096 topic person/location），OpenApiConstant 中无 userLocation 常量；作业票 operatorId 由 APP 选人链路提供；库内人员全部集中在 contractor_id=1001，外施队归属与 ID 口径均存疑。

## Execution process
1. 通道现状取证：Grep userLocation/Location 全仓零命中，确认 HTTP 定位上报尚未接入；读 LocationTask.java 确认 personId = people.getOutId()（UUID），且仅上报 isOnline=true 记录；定时任务 sys_job 中 locationTask.getLocation 每 10 秒启用，practitioners.sync 状态为停用且 cron 是每月 1 日。
2. ID 双轨取证：MCP 查 base_contractor_people 得 1001 名下 152 人（149 有 sync_id、130 有卡、sync_id≠outid 达 149 条），sync_id 为 19 位雪花而 outid 恒为 UUID；1039 宜昌华直仅 1 人（王外施 id=960，outid=sync_id=RD000…1，无定位卡）。
3. 成功/失败对照：base_work_special 按 operatorId 形态分组统计得雪花成功 60 条(2025-12~2026-09)、雪花失败 1 条（宋鹏 8-20"作业人员编码错误"）、UUID 成功 2 条（石磊 3-12）；结合 git blame 确认 LocationTask 的 personId=outId 自 2026-03-13 起生效，推出园区表人员ID取决于该人首次被上报时所用标识，而非单一字段正确。
4. 发现断点一（破坏企业归属）：updateBaseContractorPeopleByPhone 的 SET 含 contractor_id=#{contractorId}，而 syncContractorPeople 硬编码 1001L + status=3 + id_card_no 写成 worktype，where 仅按 phone 无 del_flag；clearPositionCard 会把全表卡号清空再回填，外施队人员同步一次即被挪到源洹名下。
5. 发现断点二（人员选不出）：整条同步链路从不写 sync_id，而 selectContractorPeopleList 过滤 status=3 and sync_id is not null，新录入外施队人员根本选不出来。
6. 发现断点三（口径不同源）：定位上报用 outid、作业票用 sync_id，APP 8 个 Fragment 均 bean.setId(getSyncId())，故新人员必然匹配失败。
7. 依据用户出示的定位系统组织树修正方向：湖北源洹树下已有独立"承包商"分组并绑定 7 张卡（湖北盛廷×2、陆晟机械×4，当前 0 在线），推翻了此前"外施队不在定位系统中此路不通"的判断；同时查得本地 base_contractor 仅有 1001/1038测试/1039华直/1040test(已删)，盛廷与陆晟两家企业档案缺失，若现在跑同步会因手机号未命中而 insert 到 1001 名下。
8. 输出四步计划：① 同步逻辑改为只回填 position_card_no、不新建不改 contractor_id/status/id_card_no 并补 del_flag 条件；② 先建承包商企业档案→PC 录入人员→同步回填卡号；③ operatorId 口径给出 A 逐人兼容(推荐)/B 全统一 outid/C 全统一 sync_id 三选项待拍板；④ 推送前本地校验 operatorId 是否已进园区表，并把 operatorInfo 多人分隔符由逗号改为文档要求的英文分号。

## Related files
- /ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/LocationTask.java
- /ruoyi-system/src/main/resources/mapper/base/BaseContractorPeopleMapper.xml
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseContractorPeopleServiceImpl.java
- /ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/platement/TaskPractitionersPerson.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/OpenApiConstant.java
- /ruoyi-ui/src/views/base/contractorPeople/index.vue

## Notes
- 初判"雪花 operatorId 必失败"被 60 条成功记录证伪，教训：判定 ID 口径不能只看字段名，必须查"该人首次进入平台表时用的是哪个标识"，历史两代编码并存会使同一字段出现两种合法值。
- PlatementBaseApi.java 的 PLATEMENT_PUSH_ENABLED=false 是 2026-10-22 的本地改动（git log -S 无命中即未提交），联调时所有 HTTP 推送返回均为 mock 成功，sync_result 为空无法区分真假成功，须先核对生效值。
- 用户明确"先商讨计划""不懂的对我进行反问"，因此本轮全程只做取证与方案，未产生任何代码/配置改动，Related files 均为读取对象。

## Task overview
部分完成（分析类任务，无代码改动）：查清定位上报与作业票推送的人员标识双轨事实（personId=outid、operatorId=sync_id），用成功/失败对照样本证明园区表人员ID按首次上报标识分两代并存；识别出三个必须修的断点（同步覆盖 contractor_id、选人 SQL 过滤 sync_id、operatorId 与 personId 不同源）及 operatorInfo 分隔符隐患；据用户提供的定位系统承包商分组实证修正了路径判断，产出四步改造计划并列出待用户拍板的 operatorId 口径 A/B/C 等决策点，等待用户确认后拆具体改动清单。
