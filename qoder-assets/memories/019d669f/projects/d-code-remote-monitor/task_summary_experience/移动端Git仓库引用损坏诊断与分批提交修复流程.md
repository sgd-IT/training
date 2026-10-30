---
title: "移动端Git仓库引用损坏诊断与分批提交修复流程"
usage_scenario:
    - "When repairing a Git repository corrupted by external processes like IDEs"
    - "When restructuring a monolithic initial commit into semantic, reviewable batches"
    - "When diagnosing and recovering dangling commits after a failed git commit"
keywords:
    - "git repository repair"
    - "broken ref"
    - "dangling commit"
    - "fsck recovery"
    - "batched commits"
source: "auto"
---

## Task description
- Core requirement: repair a corrupted mobile app Git repository and restructure its first commit into clean, semantic batched commits
- Task background: the fire-hot branch's .git/refs/heads/fire-hot file was corrupted into 4KB binary garbage (not a valid SHA), causing `git commit` to fail with "reference broken" and the repo to show "No commits yet" despite staged files; the user wanted to continue working on fire-hot, but it was unusable

## Execution process
1. Diagnosed the corruption by reading .git/HEAD and listing refs/heads/, confirming both HEAD and fire-hot were binary-corrupted
2. Ran `git fsck --lost-found` and recovered the dangling commit `a4c336b` (the intended first commit)
3. Attempted to delete the corrupted fire-hot ref, but it auto-restored due to an IDE (HBuilderX) holding a write handle
4. Bypassed the corruption by creating an orphan `_split` branch, resetting index, then adding and committing in four logical batches: (1) scaffolding & static assets, (2) uni_modules, (3) components & utils, (4) business pages
5. Added the missing `uni.scss` file as a fifth commit
6. Renamed `_split` to `master`, verified content equivalence with the original `a4c336b` via `git diff backup-init master --stat`
7. Restored `fire-hot` by directly overwriting its ref file with the SHA of the new `master` tip (`bc5f08a`) and updating HEAD to point to it
8. Merged all branches into `master` and pushed the clean history to the remote `anwei` repository

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\.git\HEAD
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\.git\refs\heads\fire-hot
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\.git\refs\heads\main
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\.git\refs\heads\backup-init
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\uni.scss

## Notes
- `.git/HEAD` must be written with LF line endings (not CRLF) when using PowerShell Set-Content; otherwise git may fail to parse it
- Deleting corrupted loose refs fails if an IDE holds a write handle — switching to a clean branch name and `update-ref` is the lowest-cost bypass
- `git fsck --lost-found` is the canonical way to recover dangling commits when `git commit` appears to fail but the object was created

## Task overview
Successfully repaired the git repository and completed the first commit: diagnosed and confirmed the binary corruption of `.git/HEAD` and `refs/heads/fire-hot`; recovered the dangling commit `a4c336b` via `fsck`; bypassed the un-deletable corrupted ref by switching to `master` and `update-ref`; restructured the history into five clean, semantic commits; restored `fire-hot` by direct ref file overwrite; merged and pushed the clean history to the remote `anwei` repository.
