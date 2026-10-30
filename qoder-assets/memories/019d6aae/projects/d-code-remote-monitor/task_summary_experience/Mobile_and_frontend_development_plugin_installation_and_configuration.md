---
title: "Mobile and frontend development plugin installation and configuration"
usage_scenario:
    - "When installing or configuring Qoder-native plugins for mobile and frontend development"
    - "When ensuring two complementary skills activate simultaneously"
keywords:
    - "mobile-frontend-dev"
    - "plugin installation"
    - "Qoder native plugin"
    - "ui-ux-pro-max"
    - "uniapp"
source: "auto"
---

## Task description
- Core requirement: install two skills (ui-ux-pro-max and uniapp) as a single Qoder-native plugin named mobile-frontend-dev@local, configured to activate simultaneously during mobile and frontend development
- Task background: ui-ux-pro-max provides UI/UX design intelligence; uniapp provides uni-app framework documentation; both are needed together for cross-platform development; the plugin must be registered in Qoder's installed_plugins.json and installed_plugins_v2.json

## Execution process
1. Explored the structure of both skill directories to identify SKILL.md and reference files
2. Created the plugin directory c:\Users\Administrator\.qoder\plugins\cache\local\mobile-frontend-dev\
3. Copied ui-ux-pro-max/SKILL.md and uniapp/SKILL.md + references/ folder into the plugin's skills/ subdirectory
4. Updated both SKILL.md files to explicitly require mutual activation during mobile/frontend development
5. Generated plugin.json, README.md, and avatar.svg files and placed them in the plugin root and .qoder-plugin/ subdirectory
6. Registered the plugin in installed_plugins.json, installed_plugins_v2.json, and installed_plugins-v2.json by adding the mobile-frontend-dev@local entry with correct installPath and metadata
7. Verified the plugin structure and registration entries

## Related files
- c:\Users\Administrator\.qoder\plugins\cache\local\mobile-frontend-dev\plugin.json
- c:\Users\Administrator\.qoder\plugins\cache\local\mobile-frontend-dev\README.md
- c:\Users\Administrator\.qoder\plugins\cache\local\mobile-frontend-dev\assets\avatar.svg
- c:\Users\Administrator\.qoder\plugins\cache\local\mobile-frontend-dev\skills\ui-ux-pro-max\SKILL.md
- c:\Users\Administrator\.qoder\plugins\cache\local\mobile-frontend-dev\skills\uniapp\SKILL.md

## Notes
- PowerShell's Get-Content/Set-Content commands corrupted UTF-8 Chinese characters in uniapp/SKILL.md; resolved by using [System.IO.File]::ReadAllText and WriteAllText with UTF8Encoding($false)

## Task overview
Successfully installed the mobile-frontend-dev@local plugin: created a unified plugin directory containing both skills; updated their descriptions to enforce co-activation; generated all required manifest files; registered the plugin in all three Qoder plugin lists; verified the structure and content integrity.
