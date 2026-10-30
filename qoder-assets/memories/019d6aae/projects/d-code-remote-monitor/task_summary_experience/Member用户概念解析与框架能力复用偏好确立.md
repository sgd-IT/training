---
title: "Member用户概念解析与框架能力复用偏好确立"
usage_scenario:
    - "理解项目中框架定义的用户类型概念时"
    - "为新功能开发制定技术选型规范时"
    - "向新成员介绍项目架构和开发规范时"
keywords:
    - "Member"
    - "UserTypeEnum"
    - "框架复用"
    - "用户行为偏好"
source: "auto"
---

## Task description
- Core requirement: clarify the meaning of 'Member' in the SMS sending API and establish a user-level preference for proactively reusing existing framework capabilities
- Task background: the project is based on the Enjoy-IoT/yudao framework which has a dual-user system (ADMIN/MEMBER); the SmsSendApi interface contains sendSingleSmsToMember, but the project lacks a member module; the user explicitly requested to persist the 'reuse framework capabilities first' rule as a user behavior memory

## Execution process
1. Identified Member as C-end user type (UserTypeEnum.MEMBER) and Admin as B-end user type (UserTypeEnum.ADMIN)
2. Verified the absence of the member module by searching for 'module.member' — no results found
3. Confirmed that mini-program users are actually stored in the system_users table (userType=ADMIN), making sendSingleSmsToMember effectively unused in this project
4. Created a new IMMUTABLE user_behavior memory titled '开发功能前优先复用框架已有能力' with content describing the reuse principle and citing SmsSendApi as an example

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\framework\common\src\main\java\com\enjoyiot\framework\common\enums\UserTypeEnum.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-system\module-system-biz\src\main\java\com\enjoyiot\module\system\service\sms\SmsSendServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-system\module-system-biz\src\main\java\com\enjoyiot\module\system\service\member\MemberServiceImpl.java

## Notes
- The Member user concept is a framework inheritance; in this specific project, it's not implemented and the method is effectively dead code
- The new user_behavior memory is saved as IMMUTABLE to ensure cross-session persistence of the user's explicit preference

## Task overview
Successfully clarified the Member user concept in the context of the yudao framework and the project's actual implementation; established and saved the user's explicit preference for prioritizing framework capability reuse as a permanent user_behavior memory.
