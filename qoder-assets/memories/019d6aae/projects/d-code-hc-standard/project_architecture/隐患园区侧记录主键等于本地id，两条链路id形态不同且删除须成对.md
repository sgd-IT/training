---
title: "隐患园区侧记录主键等于本地id，两条链路id形态不同且删除须成对"
usage_scenario:
    - "请园区平台删除某条错误隐患时定位该传哪个id"
    - "排查本地已删而园区仍显示隐患的残留问题"
    - "判断存量隐患修数应重推还是重录"
keywords:
    - "check_info主键"
    - "UUID与数字id双形态"
    - "deleteCheck单调用方"
    - "平台删除重推成对"
    - "buildUploadEntity投影"
    - "sync_status判定推送"
source: "auto"
---

向园区平台推送隐患时，平台 check_info 表的主键由本项目客户端指定（saveOrUpdate 语义按传入 id 覆盖），因此"平台侧记录标识 = 本地 id"。但本项目有两条独立推送链路，落到平台是**同表不同 id 形态的两批记录**：

- 隐患上报链路：base_hidden_trouble_upload，主键为 IdUtils.fastSimpleUUID()（32 位 UUID），新增即推 businessState=0；其删除走 DELETE /base/hiddenTroubleUpload/{ids} → PlatementRiskCheckUnitApi.deleteCheck(ids) → 平台 DELETE /openapi/check_info/batch，body 为 array[string]，能定位到平台那条 UUID 记录。
- 隐患治理主表链路：base_hidden_trouble，主键为 Long 自增，推送时经 buildUploadEntity 用 String.valueOf(id) 作为平台 id（纯数字字符串）；其删除 DELETE /base/hiddenTrouble/{id} → BaseHiddenTroubleServiceImpl.pushDeleteToPlatform(id) → 同样调 deleteCheck 推平台删除。

【2026-10-27 校正】此前记录的"主表删除不调 deleteCheck、只删本地"已过时：当日已补上平台删除推送，实现在 BaseHiddenTroubleServiceImpl.pushDeleteToPlatform（只推 sync_status='1' 的记录，推失败只记 error 日志、不阻断本地删除）。deleteCheck 现有两处调用方：上报表删除 + 治理主表删除。

由此的操作约束：
1. 删除推送传本地 id 即可定位平台记录，无需额外映射表；但要先确认这条隐患来自哪张表（平台 id 是 UUID → 上报来源；是数字 → 治理主表来源）。
2. 推送删除前必须判 sync_status='1'（已成功推送过）才推，否则历史 '0'/'2'/NULL 脏数据会让平台返回错误。
3. 平台侧主键即本地 id，故"删除"与"重推"必须成对考虑：删了本地却没推平台，之后再用同一 id 补推会被平台判重复或状态错乱。
4. 两表在 SQL 层零 JOIN、零同步（BaseHiddenTroubleMapper.xml 的 selectHiddenTroubleList 只 from base_hidden_trouble），任一表新增都不会在另一表生成记录——这是"隐患上报与隐患信息两个菜单数据不一样"的物理原因。
5. 主表删除一旦执行，sync_result 就没有载体了（行已删），平台删除失败的留痕只能落应用日志（log.error），不会阻断本地删除。
6. 主表推送平台的字段是硬编码投影（dangerManageType 恒 1、dangerSrc 恒 1、handleSource 恒 "1"、hazardCategory 恒 "0"、经纬度固定 "1"、registrant/registTime 取自 findUserName/findTime、dangerName 取 areaName），比对园区与本地字段差异时须先扣除这层投影。
