---
title: "remote-monitor工作区双IDE共存与Qoder自动构建关闭配置"
usage_scenario:
    - "When configuring Qoder for Java backend development in the remote-monitor workspace"
    - "When diagnosing target/classes pollution sources in a multi-IDE environment"
    - "When onboarding new developers to the dual-IDE workflow"
keywords:
    - "remote-monitor"
    - "Qoder"
    - "IDEA"
    - "java.autobuild.enabled"
    - "target/classes"
source: "auto"
---

The d:\code\remote-monitor workspace is simultaneously opened by IntelliJ IDEA 2025.1.3 (primary builder, used for compilation and debugging) and Qoder (redhat.java extension). Qoder's Java language service jdt.ls uses ECJ compiler and defaults to java.autobuild enabled, which writes poison classes into the shared target/classes directory and has caused backend startup crashes. Current configuration: d:\code\remote-monitor\.vscode\settings.json sets "java.autobuild.enabled": false (workspace-level, effective after language service reload), preventing jdt.ls from writing build artifacts while preserving code navigation and completion. Applicable boundary: this configuration applies only to the anwei-fire-remote-monitor Java backend module in this workspace; frontend modules are unaffected.
