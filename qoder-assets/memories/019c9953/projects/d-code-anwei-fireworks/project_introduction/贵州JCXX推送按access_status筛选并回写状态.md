---
title: "贵州JCXX推送按access_status筛选并回写状态"
usage_scenario:
    - "改造JCXX推送筛选与回写规则时"
    - "验证JCXX是否按access_status执行"
    - "验收贵州推送access_status联动"
keywords:
    - "JCXX"
    - "贵州推送"
    - "access_status筛选"
    - "推送回写状态"
    - "企业基础信息"
source: "auto"
---

贵州企业基础信息（JCXX）推送功能原为全量推送，不依赖 access_status 字段筛选，推送后也不更新该字段；现调整为：推送前按 access_status=0 筛选企业（即仅推送已启用的企业），推送成功后将对应企业的 access_status 更新为 2。该逻辑已生效且符合用户当前要求。
