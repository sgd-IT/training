---
title: "IDE editor buffer overrides git worktree during rebase"
usage_scenario:
    - "When rebase or checkout modifies config files"
    - "When Read/SearchReplace tools show stale content"
    - "When multiple developers modify the same config file"
keywords:
    - "IDE buffer"
    - "git rebase"
    - "application.yaml"
    - "config loss"
source: "auto"
---

Bug class: IDE editor buffer overrides git worktree during rebase or checkout, causing silent loss of merged-in configuration.
Root cause: git rebase/checkout modifies the working tree file, but the IDE's unsaved buffer remains in memory; a subsequent save overwrites the disk with the stale buffer, erasing the remote's changes.
Fix pattern: before handling conflicts, ask the user to close or reload the file in the IDE; verify content with git grep -c -a <rev> -- <path> and git cat-file -s <rev>:<path>; modify only via git apply --cached zero-context patch.
Reusable lesson: Don't edit files opened in the IDE during rebase; use git apply --cached to modify the index and let git write the worktree. Applies when rebase/checkout affects config files like application.yaml; does not apply to files not open in the IDE.
