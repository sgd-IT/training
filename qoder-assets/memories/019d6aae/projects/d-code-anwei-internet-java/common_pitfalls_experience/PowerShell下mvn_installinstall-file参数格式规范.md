---
title: "PowerShell下mvn install:install-file参数格式规范"
usage_scenario: []
keywords:
    - "PowerShell"
    - "mvn install-file"
    - "参数解析"
    - "run_in_terminal"
---

在PowerShell中执行mvn install:install-file命令时，必须用单引号包裹每个-D参数（如'-Dfile=xxx'），否则因PowerShell解析异常导致ArgumentOutOfRangeException；同时，-Dmaven.repo.local参数不被install-file插件支持，会导致NoPluginFoundForPrefixException，应移除该参数，让Maven自动使用settings.xml中配置的本地仓库。（来源：run_in_terminal执行mvn install:install-file失败）
