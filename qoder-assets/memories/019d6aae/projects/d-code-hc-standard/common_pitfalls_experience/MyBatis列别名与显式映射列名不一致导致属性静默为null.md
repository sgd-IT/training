---
title: "MyBatis列别名与显式映射列名不一致导致属性静默为null"
usage_scenario:
    - "排查接口返回字段恒为null"
    - "编写或修改mapper SQL时"
    - "移动端作业人选证书报参数缺失"
keywords:
    - "SQL别名映射失配"
    - "syncId返回null"
    - "列标签不匹配"
    - "sync_id显式映射"
    - "MyBatis静默null"
source: "auto"
---

缺陷类型：MyBatis 查询列别名与显式 resultMapping 列名不一致，导致属性静默为 null（不报错）。根因：项目未开启驼峰转换（mapUnderscoreToCamelCase 被注释），resultMap 中 `<result property="syncId" column="sync_id"/>` 只认列标签 sync_id；当 SQL 写成 `sync_id as syncId` 时列标签变为 syncId，显式映射失配、自动映射不兜底，实体 syncId 恒为 null。修复模式：SQL 必须保持原列名（去掉 `as syncId`），与该 resultMap 其他查询（如 a.* 直查列）行为对齐。适用边界：BaseContractorPeopleMapper.xml 等所有喂给带 sync_id 显式映射的 resultMap 的 SQL，禁止对映射列起大小写/下划线变更型别名；排查同类"接口返回某字段恒为 null"问题时优先核对别名与映射列名一致性。
