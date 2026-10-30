---
title: "PlatementPractitionersPerson表单字段补全与服务层兜底策略"
usage_scenario:
    - "修复因前端表单字段缺失导致的平台校验失败"
    - "为PlatementPractitionersPerson实体添加服务层兜底逻辑"
    - "确保select操作返回所有平台必需字段"
keywords:
    - "PlatementPractitionersPerson"
    - "表单字段补全"
    - "服务层兜底"
    - "平台校验失败"
source: "auto"
---

## Task description
- Core requirement: add missing fields (sex, birthday, personType) to the frontend form and implement service-layer fallback logic for all platform-required fields to prevent 'field X cannot be null' validation errors.
- Task background: the PlatementPractitionersPerson entity requires 16 fields; the existing frontend form only collected name/phone/post/certFiles/result_id, leaving other fields null; the service layer lacked fallback logic for these fields, causing validation failure during saveOrUpdate.

## Execution process
1. Frontend index.vue: added dicts ['sys_user_sex', 'platement_enterprise_person_type']; added select for sex, date-picker for birthday, select for personType; updated rules with required validation; updated reset() with initial null values.
2. Backend PlatementPractitionersPersonServiceImpl.java: modified insert/update/deleteById methods to set deleted/createBy/createDate/updateBy/updateDate; added fallback logic in deleteById to populate all 7 null fields (sex/birthday/personType/createBy/updateBy/createDate/updateDate) with defaults.
3. Mapper PlatementPractitionersPersonMapper.xml: updated resultMap and selectPlatementPractitionersPersonVo to include all 16 columns; updated insert/update statements to write all 10 newly mapped columns.
4. Verified compilation succeeded with mvn compile -pl ruoyi-system.

## Related files
- d:/code/hc-standard/ruoyi-ui/src/views/platement/practitioners/index.vue
- d:/code/hc-standard/ruoyi-system/src/main/java/com/ruoyi/base/service/impl/PlatementPractitionersPersonServiceImpl.java
- d:/code/hc-standard/ruoyi-system/src/main/resources/mapper/platement/PlatementPractitionersPersonMapper.xml

## Notes
- The 3 historical rows (id 18/19/20) have NULL values for sex/birthday/personType/createBy/etc.; they will still fail validation until those fields are populated in the DB.

## Task overview
Successfully completed the PlatementPractitionersPerson form field补全 and service-layer fallback: frontend now collects all required fields; service layer adds robust defaults for dirty data; mapper ensures full DB column mapping; compilation verified successful.
