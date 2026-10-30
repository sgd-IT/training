---
title: "BaseWorkSpecialValidator.java 中子对象校验器被注释"
usage_scenario:
    - "When adding or modifying validation logic for work tickets"
    - "When debugging why sensor/certificate/video/approval data is not validated"
keywords:
    - "BaseWorkSpecialValidator"
    - "validateSensors"
    - "sub-object validation"
    - "commented out"
source: "auto"
---

In BaseWorkSpecialValidator.java, the sub-object validators validateSensors, validateApproves, validateVideos, and validateCertificates are all disabled by comment (lines 331–349). This means sensor/certificate/video/approval data integrity checks are not enforced at save time.
