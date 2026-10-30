---
title: "CRM销售闭环客户调研单创建、报价关联与代码提交全流程"
usage_scenario:
    - "When creating a new customer research record to unblock the 'Freeze Quote' action in the CRM contract form"
    - "When associating an existing quote with a newly created research record"
    - "When committing and pushing frontend/backend code changes for a CRM sales loop feature"
keywords:
    - "CRM销售闭环"
    - "客户调研单"
    - "FIRE模板"
    - "报价关联"
    - "git最小单元提交"
source: "auto"
---

## Task description
- Core requirement: Create a completed customer research record for the 'Guangzhou Jinqiao Nursing Home' project to unblock the 'Freeze Quote' action on the contract form, then associate it with existing quotes (22 & 23), verify its frontend visibility, and commit all related code changes.
- Task background: The contract form requires a 'completed' research (status=2) to be selected before freezing. The project had no such research. The sales loop involves CRM modules (clue, customer, project, quote, contract, research) and requires coordinated frontend/backend changes.

## Execution process
1. Template mapping: Identified the correct template code 'FIRE' (智慧消防调研 V1) for the '智慧消防（远程值守）' industry by grepping researchTemplates.ts and confirming the industry-id=4 mapping.
2. Research creation: Inserted a new research record (id=6, research_no='DY20261012000006') into crm_customer_research with correct Chinese fields (customer_name, industry, conclusion), status=2, template_code='FIRE', and version_no=1.
3. Quote association: Updated crm_project_quote records for ids 22 and 23 to set research_id=6, research_no='DY20261012000006', research_version_no=1, template_code='FIRE', and template_version='智慧消防调研 V1'.
4. Frontend verification: Called /crm/customer-research/page?customerId=103&status=2 with mock auth to confirm the new research appears in the frontend dropdown.
5. Code change identification: Used git diff to identify all modified files: frontend (src/utils/dict.ts, StandaloneQuoteForm.vue, ProjectQuoteForm.vue, StandaloneContractForm.vue, SalesClueListPage.vue, .env.local) and backend (CrmContractServiceImpl.java, pom.xml).
6. Git commit & push: Committed each code file individually (smallest unit) with Conventional Commits messages, excluding configuration files (.env.local, pom.xml), then pushed both repositories to origin/master.

## Related files
- src/utils/dict.ts
- src/views/crm/contract/StandaloneContractForm.vue
- src/views/crm/project/StandaloneQuoteForm.vue
- src/views/crm/project/ProjectQuoteForm.vue
- src/views/crm/clue/SalesClueListPage.vue
- yudao-module-crm/yudao-module-crm-server/src/main/java/cn/iocoder/yudao/module/crm/service/contract/CrmContractServiceImpl.java

## Notes
- None

## Task overview
Successfully completed the CRM sales closed-loop research creation and code submission: a new 'FIRE' template research record was created and verified; quotes 22 and 23 were fully associated with it; the research is now visible in the frontend dropdown; all related code changes (5 frontend, 1 backend) have been committed individually and pushed to remote.
