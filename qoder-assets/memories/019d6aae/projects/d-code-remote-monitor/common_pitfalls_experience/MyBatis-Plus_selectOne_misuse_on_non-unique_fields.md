---
title: "MyBatis-Plus selectOne misuse on non-unique fields"
usage_scenario:
    - "Debugging TooManyResultsException in MyBatis-Plus applications"
    - "Choosing the appropriate MyBatis-Plus query method for non-unique fields"
keywords:
    - "MyBatis-Plus"
    - "selectOne"
    - "TooManyResultsException"
    - "selectList"
    - "findFirst"
source: "auto"
---

Bug class: MyBatis-Plus query method misuse
Root cause: selectOne throws TooManyResultsException when multiple matching records exist, but the business logic can tolerate returning the first match.
Fix pattern: Replace selectOne with selectList(...).stream().findFirst().orElse(null).
Reusable lesson: Don't use selectOne for queries where database uniqueness is not enforced because it throws TooManyResultsException on duplicates; instead use selectList + findFirst to gracefully handle duplicates. Applies when querying by a field that may have duplicate values in the database; does not apply when strict uniqueness is guaranteed and enforced at the database level.
