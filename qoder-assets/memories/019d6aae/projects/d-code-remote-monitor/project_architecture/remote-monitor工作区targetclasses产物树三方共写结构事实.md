---
title: "remote-monitor工作区target/classes产物树三方共写结构事实"
usage_scenario:
    - "When diagnosing intermittent Spring Boot startup failures in the remote-monitor workspace"
    - "When designing build isolation strategies for multi-IDE environments"
    - "When onboarding developers to avoid target/classes pollution"
keywords:
    - "remote-monitor"
    - "target/classes"
    - "IDEA"
    - "Maven"
    - "jdt.ls"
    - "anwei-fire-remote-monitor"
source: "auto"
---

The d:\code\remote-monitor workspace exhibits a structural fact: the target/classes compilation output directory is concurrently written by three independent processes — IntelliJ IDEA's jps builder (PID 42692), Maven CLI (mvn clean install), and Qoder's redhat.java language service jdt.ls (PID 51132). This tripartite co-write causes build instability: IDEA's incremental cache may miss overwritten classes, leading to startup crashes. The anwei-fire-remote-monitor backend module is the primary affected component. This structure is not declared by the user but was verified through process listing and timestamp correlation during diagnosis.
