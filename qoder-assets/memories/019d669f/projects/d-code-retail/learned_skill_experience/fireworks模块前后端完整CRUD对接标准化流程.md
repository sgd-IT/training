---
title: "fireworks模块前后端完整CRUD对接标准化流程"
usage_scenario:
    - "When implementing full CRUD for a new fireworks module (e.g., risk warning, video monitoring)"
    - "When onboarding a new developer and documenting the standard integration process"
keywords:
    - "fireworks module"
    - "CRUD"
    - "data isolation"
    - "menu component path"
    - "API layer"
    - "RuoYi-Vue-Plus"
source: "auto"
---

## Goal
Safely implement full CRUD for any new fireworks module while ensuring backend data isolation, frontend API/page alignment with menu paths, and permission consistency; applicable to modules like retail, person, inventory, and future ones.

## Steps
Step 1: Verify authoritative menu component path and permissions from database - Confirm the exact component path (e.g., store/retailInfo/index) and required permissions (e.g., fireworks:retail:list) directly from the sys_menu table; rationale: prevents frontend/backend misalignment and 404s, input: user's menu information, output: verified component path and permission identifiers
Step 2: Fix backend data isolation gaps in ServiceImpl - Add retailId ownership validation to queryById, insert, update, and delete methods in the module's ServiceImpl; rationale: ensures security before exposing UI, input: controller path and business logic, output: all operations enforce data scope
Step 3: Create frontend API layer (types/index) under `@/api/fireworks/{module}` - Define types.ts for BO/VO and index.ts for list/get/add/update/del APIs; rationale: decouples backend logic from UI and enables type safety, input: BO/VO field definitions, output: typed API functions
Step 4: Create frontend page under `@/views/store/{componentPath}` matching menu - Implement the Vue page at the exact path specified in the database menu component; rationale: satisfies RuoYi-Vue-Plus dynamic menu routing, input: verified component path, output: working Vue page with CRUD functionality
Step 5: Phase check - Confirm all pages compile and pass type checking; when compilation/type errors occur, trace them to API type mismatches or component binding issues and fix before proceeding

*本次零售店 CRUD 对接即严格遵循此流程：Step 1 核查了 sys_menu 中 store/retailInfo/index 路径及 fireworks:retail:* 权限；Step 2 在 RetailInfoServiceImpl 中补全了 queryById/update/delete 的数据隔离逻辑。*

## Notes
- Skipping backend data isolation fixes causes privilege escalation vulnerabilities where users can manipulate other stores' data; must be completed before frontend integration
