---
title: "anwei-fireworks-retail后端工程模块结构全景图"
usage_scenario:
    - "当需要快速了解一个新接手的RuoYi-Vue-Plus项目的整体模块划分时"
    - "当需要定位某个功能（如OSS上传、定时任务）在哪个模块中实现时"
    - "当需要理解不同模块（如admin、api、common）之间的依赖关系和职责边界时"
keywords:
    - "anwei-fireworks-retail"
    - "Maven multi-module"
    - "ruoyi-admin"
    - "ruoyi-api"
    - "ruoyi-common"
    - "ruoyi-extend"
    - "ruoyi-modules"
    - "RegulatorStatisticsServiceImpl"
    - "regulatory cockpit"
source: "auto"
---

## Task description
- Core requirement: provide a complete and accurate overview of the module structure of the entire backend project `anwei-fireworks-retail`
- Task background: the user is a frontend beginner and needs to understand the macro-level organization of the project to navigate code, locate files, and grasp the overall architecture; the project is a Maven multi-module project based on RuoYi-Vue-Plus

## Execution process
1. Scanned the top-level directory to identify the 5 primary modules: `ruoyi-admin`, `ruoyi-api`, `ruoyi-common`, `ruoyi-extend`, `ruoyi-modules`
2. Explored `ruoyi-admin`: confirmed it as the sole startup module (`DromaraApplication.java`), containing authentication controllers, login strategies, and environment configurations
3. Explored `ruoyi-api`: identified it as the cross-module contract layer, containing service interfaces (e.g., `UserService`) and DTOs for loose coupling between modules
4. Explored `ruoyi-common`: enumerated its 26 submodules, each representing a distinct infrastructure capability (e.g., `ruoyi-common-mybatis`, `ruoyi-common-redis`, `ruoyi-common-oss`)
5. Explored `ruoyi-extend`: identified its 3 independent services (`ruoyi-snailjob-server`, `ruoyi-monitor-admin`, `ruoyi-snailai-server`) for distributed scheduling, monitoring, and AI capabilities
6. Explored `ruoyi-modules`: listed its 7 submodules, with `ruoyi-fireworks` being the core business module, and others (`ruoyi-system`, `ruoyi-job`, etc.) being framework-provided components
7. Verified the relationship between modules: `ruoyi-admin` depends on `ruoyi-modules/ruoyi-fireworks` and other modules, while `ruoyi-fireworks` depends on `ruoyi-api` for contracts and `ruoyi-common` for infrastructure
8. Confirmed the role of `script/` as the operations and maintenance directory containing deployment scripts and SQL files

## Related files
- d:\code\retail\anwei-fireworks-retail\pom.xml
- d:\code\retail\anwei-fireworks-retail\ruoyi-admin\src\main\java\org\dromara\DromaraApplication.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-api\src\main\java\org\dromara\system\api\UserService.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-mybatis\pom.xml
- d:\code\retail\anwei-fireworks-retail\ruoyi-extend\ruoyi-snailjob-server\pom.xml
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\pom.xml
- d:\code\retail\anwei-fireworks-retail\script\bin\ry.bat

## Notes
- None

## Task overview
Successfully provided a complete and accurate module structure overview of the `anwei-fireworks-retail` backend project: confirmed it is a Maven multi-module project with 5 primary modules; detailed the purpose and contents of each module; clarified the dependency relationships between them; and explained how this macro-structure supports the micro-level package-based layered architecture (controller/service/mapper/domain) within individual modules like `ruoyi-fireworks`. The information is ready for the user to use as a navigation map.
