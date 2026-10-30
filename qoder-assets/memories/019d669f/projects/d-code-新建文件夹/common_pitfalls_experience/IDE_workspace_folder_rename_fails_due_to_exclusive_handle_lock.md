---
title: "IDE workspace folder rename fails due to exclusive handle lock"
usage_scenario:
    - "When renaming a folder that is currently opened as an IDE workspace"
    - "When troubleshooting 'The process cannot access the file because it is being used by another process' during folder operations"
keywords:
    - "IDE workspace"
    - "folder rename"
    - "file lock"
    - "exclusive handle"
source: "auto"
---

Bug class: File system operation failure due to process handle contention
Root cause: The IDE holds an exclusive file system handle on its opened workspace folder, preventing any external rename/move operation on that directory.
Fix pattern: Close the IDE completely before attempting to rename or move the workspace folder.
Reusable lesson: Don't attempt to rename a folder while it's open as an IDE workspace because the IDE locks it at the OS level and the operation will fail with 'in use' error; instead close the IDE first. Applies when renaming or moving a folder currently loaded as an IDE workspace; does not apply to folders not opened in any IDE or editor process.
