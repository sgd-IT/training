---
title: "构建失败的多维度诊断与修复：MapStruct误用、依赖版本过时警告及上游模块编译连锁故障"
usage_scenario:
    - "When diagnosing complex build failures involving multiple error types and module interdependencies"
    - "When troubleshooting MapStruct misconfiguration in MyBatis-Plus projects"
    - "When distinguishing between fatal compilation errors and non-fatal deprecation warnings in Java builds"
keywords:
    - "build failure"
    - "MapStruct"
    - "MyBatis-Plus"
    - "commons-lang3"
    - "deprecation warning"
    - "cascading error"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the build failure in the anwei-fireworks-retail project
- Task background: the project uses RuoYi-Vue-Plus 6.0.0-BETA with Spring Boot 4.1.0; build fails with multiple error types including MapStruct mapping errors, commons-lang3 deprecation warnings, and 'package not found' cascading failures; the root cause is multi-layered

## Execution process
1. Identified the primary build failure source: AlarmRecordMapper.java was incorrectly annotated with org.mapstruct.Mapper instead of org.apache.ibatis.annotations.Mapper, causing MapStruct to attempt generating mappings for all inherited BaseMapper methods (e.g., selectById, selectCount), which failed due to incompatible return types
2. Confirmed the secondary warning sources: commons-lang3 3.18+ deprecated all CharSequence-overloaded StringUtils methods, and spring-ai-mcp deprecated the McpSchema.TextContent(String) constructor — both are non-fatal warnings but clutter the build output
3. Diagnosed the cascading 'package not found' errors (e.g., org.dromara.system.*): these were not code defects but build-state artifacts caused by the upstream MapStruct failure interrupting the compilation flow, leaving dependent modules (ruoyi-system, ruoyi-admin) without compiled class outputs
4. Verified that all related files (SysUser.java, SysUserVo.java, LoginUser.java, UserService.java, LoginHelper.java, RetailDataScopeHelper.java, SysLoginService.java) had correct, syntactically valid changes and no missing imports or dependencies
5. Confirmed the existence and correctness of all referenced external classes (MessageService, PushPayloadDTO, PushSourceEnum, PushTypeEnum) and the proper dependency declarations in ruoyi-admin/pom.xml

## Related files
- /ruoyi-modules/ruoyi-fireworks/src/main/java/org/dromara/fireworks/mapper/AlarmRecordMapper.java
- /ruoyi-common/ruoyi-common-core/src/main/java/org/dromara/common/core/utils/StringUtils.java
- /ruoyi-modules/ruoyi-demo/src/main/java/org/dromara/demo/mcp/McpDemoClientHandlers.java
- /ruoyi-common/ruoyi-common-mcp/src/main/java/org/dromara/common/mcp/core/McpClientTemplate.java
- /ruoyi-common/ruoyi-common-mybatis/src/main/java/org/dromara/common/mybatis/core/query/LambdaQueryBuilder.java
- /ruoyi-modules/ruoyi-system/src/main/java/org/dromara/system/domain/SysUser.java
- /ruoyi-api/src/main/java/org/dromara/system/api/model/LoginUser.java
- /ruoyi-api/src/main/java/org/dromara/system/api/UserService.java
- /ruoyi-common/ruoyi-common-satoken/src/main/java/org/dromara/common/satoken/utils/LoginHelper.java
- /ruoyi-admin/src/main/java/org/dromara/web/service/SysLoginService.java

## Notes
- The 'org.dromara.system.* package not found' errors are build-state residues, not code issues; they disappear after a full rebuild once the primary MapStruct error is fixed
- Commons-lang3 deprecation warnings can be suppressed or addressed by downgrading to 3.17.x, but this is unnecessary as they do not affect build success
- The MapStruct error is a critical misconfiguration that must be corrected before any other build troubleshooting

## Corrections（2026-08-28 验证）
- "程序包不存在"连锁错误的根因并非单纯的构建残留：Qoder 的 Java 语言服务器（redhat.java/JDT，ECJ 编译器）后台自动构建会清空 target/classes（.class 数为 0、只剩 *__Javadoc.json、install 产物为 293 字节空 jar），导致下游模块连锁报"程序包不存在"。修复：工作区 .vscode/settings.json 设置 "java.autobuild.enabled": false 并终止 JDT 进程后，mvn clean install 全部 41 个模块 BUILD SUCCESS（ruoyi-system 260 个 class、jar 612KB 正常）。
- 排查要点：ruoyi-system 单独 mvn 编译能通过、但全量构建时其 target/classes 为空，即为 JDT 自动构建破坏产物；先查 java/javaw 进程中的 JDT（命令行含 org.eclipse.jdt.ls）再定位。
- Maven 本地仓库实际位于 D:\Tool\apache-maven-3.9.15\repo（settings.xml 指定），非默认 %USERPROFILE%\.m2\repository。
