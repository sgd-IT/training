---
title: "Maven compile failure due to JDK version mismatch"
usage_scenario:
    - "When building Java projects with Maven that require a specific JDK version"
    - "When encountering 'Unsupported major.minor version' or 'release version not supported' errors during Maven compilation"
keywords:
    - "Maven"
    - "JDK version"
    - "compile failure"
    - "JAVA_HOME"
source: "auto"
---

Bug class: Maven compile failure due to JDK version mismatch
Root cause: Maven's default JDK is not JDK 21, causing the compiler to fail on Java 21 language features.
Fix pattern: Set the `JAVA_HOME` environment variable to point to the JDK 21 installation directory before running the Maven command.
Reusable lesson: Don't run Maven commands without first verifying the `JAVA_HOME` environment variable matches the project's required JDK version, because it leads to cryptic compile failures; instead, always set `JAVA_HOME` explicitly to the correct JDK path. Applies when building Java projects with Maven that require a specific JDK version; does not apply to projects built with Gradle or other build tools.
