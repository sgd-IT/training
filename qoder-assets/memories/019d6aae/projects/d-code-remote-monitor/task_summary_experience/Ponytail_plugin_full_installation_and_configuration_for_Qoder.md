---
title: "Ponytail plugin full installation and configuration for Qoder"
usage_scenario:
    - "When installing the ponytail plugin system for Qoder"
    - "When configuring Qoder hooks for automatic mode activation and ruleset injection"
    - "When upgrading ponytail from project-level to user-level (global) configuration"
keywords:
    - "ponytail"
    - "Qoder"
    - "plugin"
    - "hooks"
    - "skills"
source: "auto"
---

## Task description
- Core requirement: install and configure the ponytail plugin system for Qoder using the full plugin method
- Task background: ponytail is a Qoder plugin/skill system that makes AI agents write less code by applying a 'laziest senior dev' decision ladder; full plugin mode requires copying rules, setting up hooks, and configuring the plugin manifest

## Execution process
1. Cloned the official ponytail repository from GitHub to obtain the source files
2. Created the project-level .qoder/rules/ directory and copied the ponytail.md rules file into it
3. Created the .qoder/settings.json file with the required UserPromptSubmit and PreToolUse hooks, pointing to the ponytail-mode-tracker.js and ponytail-subagent.js scripts
4. Verified all files were correctly placed and functional
5. Upgraded the configuration to user-level (global) by copying ponytail.md to C:\Users\Administrator\.qoder\rules\ and updating C:\Users\Administrator\.qoder\settings.json with the hooks
6. Cleaned up the project-level .qoder/ directory to avoid duplication

## Related files
- C:\Users\Administrator\.qoder\rules\ponytail.md
- C:\Users\Administrator\.qoder\settings.json
- d:\code\remote-monitor\ponytail\

## Notes
- The ponytail checkout was initially cloned to d:\code\remote-monitor\ponytail-temp, then copied to d:\code\remote-monitor\ponytail for permanence
- PowerShell's Get-Content/Set-Content commands corrupted UTF-8 Chinese characters in some files; resolved by using [System.IO.File]::ReadAllText and WriteAllText with UTF8Encoding($false)
- The final configuration is global, applying to all Qoder projects

## Task overview
Successfully completed the full installation and configuration of the ponytail plugin for Qoder: cloned the repository, set up the rules and hooks at both project and user levels, verified functionality, and cleaned up temporary files. Ponytail is now globally active with automatic mode activation and per-turn ruleset injection.
