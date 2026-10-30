---
title: "ponytail@ponytail 插件用户级安装与全局注册"
usage_scenario:
    - "When installing a Qoder plugin for global use across all projects"
    - "When migrating a project-level plugin to user-level scope"
keywords:
    - "ponytail"
    - "plugin"
    - "user-level"
    - "Qoder"
    - "installed_plugins.json"
source: "auto"
---

## Task description
- Core requirement: Install the ponytail@ponytail plugin at the user level so it is globally available across all projects.
- Task background: The user explicitly asked for a "user-level" installation after an initial project-level install; the Qoder IDE stores user-level plugins in C:\Users\Administrator\.qoder\plugins\cache\ and registers them in installed_plugins.json and installed_plugins_v2.json; this requires file copying and JSON manifest updates.

## Execution process
1. Created the user-level plugin directory structure at C:\Users\Administrator\.qoder\plugins\cache\ponytail\ponytail\
2. Copied the entire project-level plugin contents (including .qoder-plugin/plugin.json, skills/ponytail/SKILL.md, and README.md) to the user-level cache directory
3. Updated installed_plugins.json to register ponytail@ponytail with its installPath and version
4. Updated installed_plugins_v2.json to register ponytail@ponytail with scope="user", enabled=true, and source="local"
5. Verified the registration by reading the updated installed_plugins_v2.json
6. Deleted the original project-level copy at d:\code\retail\ponytail\

## Related files
- C:\Users\Administrator\.qoder\plugins\cache\ponytail\ponytail\.qoder-plugin\plugin.json
- C:\Users\Administrator\.qoder\plugins\cache\ponytail\ponytail\skills\ponytail\SKILL.md
- C:\Users\Administrator\.qoder\plugins\cache\ponytail\ponytail\README.md
- C:\Users\Administrator\.qoder\plugins\installed_plugins.json
- C:\Users\Administrator\.qoder\plugins\installed_plugins_v2.json

## Notes
- None

## Task overview
Successfully completed the user-level installation of the ponytail@ponytail plugin: created the required directory structure, copied all plugin files to the global cache, registered the plugin in both manifest files with correct scope and status, verified the registration, and cleaned up the project-level copy. The plugin is now globally available for all projects.
