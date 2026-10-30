---
title: "平台API字段容错性与前后端契约对齐策略"
usage_scenario:
    - "When diagnosing why a required platform field appears missing in the payload"
    - "When verifying frontend-backend contract alignment for multi-field composite inputs"
keywords:
    - "平台API容错"
    - "isContractorWork"
    - "typeLevel解析"
    - "前后端契约"
source: "auto"
---

Bug class: Platform API field tolerance and frontend-backend contract gap
Root cause: The platform's isContractorWork field is marked as required in the spec but accepts null values in practice; the Web form (TicketBb/index.vue) has no control for isContractorWork and does not split typeLevel into type/level, causing backend BaseTicketBb to receive null values for those fields.
Fix pattern: For isContractorWork, rely on database default value '0'; for type/level, add splitting logic in BaseTicketBbServiceImpl.insertBaseTicketBb to parse typeLevel string like "1-0" into separate type and level fields.
Reusable lesson: Don't assume required platform fields must be provided by all clients because the platform may be tolerant; instead verify behavior empirically. Don't assume Web forms implement all required field logic — validate the actual payload structure before pushing. Applies when integrating with third-party platforms that have loose validation; does not apply to strictly enforced internal APIs.

## Notes
- This memory describes platform API field tolerance for job reporting; the current task involved PlatementPractitionersPerson form field补全 and service-layer fallback, which is a different feature area and does not conflict with or supersede this memory.
