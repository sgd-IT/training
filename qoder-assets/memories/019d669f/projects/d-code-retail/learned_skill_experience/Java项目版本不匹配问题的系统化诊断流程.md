---
title: "Java项目版本不匹配问题的系统化诊断流程"
usage_scenario:
    - "当调试Java编译错误相关的不支持的release版本时"
    - "当诊断项目构建配置与本地开发环境之间的版本差异时"
keywords:
    - "Java version"
    - "diagnostic flow"
    - "JDK installation"
    - "environment configuration"
    - "pom.xml"
    - "build.gradle"
source: "auto"
---

## Goal
Systematically diagnose Java version mismatch issues when compilation fails with 'unsupported release version' errors, applicable to any Java Maven/Gradle project where the build environment differs from the project's declared requirements.

## Steps
Step 1: grep_code - Extract the declared Java version from the project's build configuration (pom.xml or build.gradle), rationale: The build file is the source of truth for the intended target version, input: build config path, output: declared version number
Step 2: Bash - Verify the actual JDK version available on the system via `java -version` and `where java` / `which java`, rationale: Confirms what JDK is actually used at runtime and whether the declared version is installed, input: none, output: installed JDK version and path
Step 3: Phase check - Confirm the declared version matches an installed JDK; if not, the root cause is confirmed and the fix is installation + environment variable update; if they match, investigate IDE sync, Maven wrapper, or compiler plugin misconfiguration
Step 4: SearchReplace - Update environment variables (JAVA_HOME) and IDE configurations to point to the correct JDK, rationale: Ensures all tools (IDE, CLI, build scripts) use the same consistent JDK, input: correct JDK path, output: environment and IDE configured correctly

## Notes
- Assuming IDE sync alone will fix version mismatch errors causes wasted time; must first verify the actual JDK version installed on the system
