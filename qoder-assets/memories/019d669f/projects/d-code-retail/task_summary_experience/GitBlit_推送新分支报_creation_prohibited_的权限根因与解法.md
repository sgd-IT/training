---
title: "GitBlit 推送新分支报 creation prohibited 的权限根因与解法"
usage_scenario:
    - "Diagnosing Git push failures on GitBlit servers"
    - "Resolving 'creation prohibited' errors when creating new branches"
    - "Verifying remote repository branch existence before push"
keywords:
    - "GitBlit"
    - "creation prohibited"
    - "CREATE permission"
    - "JGit REJECTED_NOCREATE"
    - "new branch push"
source: "auto"
---

## Task description
- Core requirement: diagnose and resolve the "creation prohibited" error when pushing a new branch to a GitBlit remote repository
- Task background: the user attempted to push the local 'regulatory' branch to origin but was rejected with "! [remote rejected] regulatory -> regulatory (creation prohibited)"; this is a service-side permission issue, not a network or local code problem

## Execution process
1. Confirmed the remote URL format (/r/ path) and error message are characteristic of GitBlit, not GitLab or GitHub
2. Used 'git ls-remote --heads origin' to verify that only the 'master' branch exists remotely, confirming that 'regulatory' would be a new branch creation
3. Performed WebSearch and WebFetch to identify the exact GitBlit source code location (GitblitReceivePack.java) where 'setAllowCreates(user.canCreateRef(repository))' is called
4. Grep'd JGit source cache to confirm that 'creation prohibited' corresponds to REJECTED_NOCREATE and that JGit defaults to allowCreates=true, proving the restriction is server-enforced
5. Identified the root cause: the user's account lacks GitBlit's 'CREATE' permission for this repository, only having 'PUSH' permission
6. Derived two actionable solutions: (1) request admin to grant 'Create' permission, (2) have an admin create the branch first via 'git push origin master:regulatory'

## Related files
- d:\code\retail\anwei-fireworks-retail\.git\config

## Notes
- The 4 modified files (application-dev.yml, UserService.java, OverStorageDetectionService.java, SysUserServiceImpl.java) were not committed locally before the push attempt, so no code was lost; they remain in the working directory
- The 'docs/监管后端接口开发文档（零售店）.md' file remains untracked and should be decided upon for inclusion in the next commit

## Task overview
Successfully diagnosed the GitBlit 'creation prohibited' error as a server-side 'CREATE' permission issue and provided two verified solutions: granting the missing permission or having an admin pre-create the branch. The diagnosis was confirmed by cross-referencing GitBlit source code and JGit behavior.
