---
title: "git log --all -S 全分支符号存在性验证方法"
usage_scenario:
    - "When verifying if a symbol definition exists anywhere in the repository history"
    - "When attributing a 'cannot find symbol' error to incomplete commits"
keywords:
    - "git log"
    - "symbol search"
    - "git -S"
    - "cross-branch verification"
source: "auto"
---

## Usage Scenario
When diagnosing a compilation error caused by a missing method or symbol definition in a multi-branch, multi-module codebase; when verifying if a symbol (method, field, constant) exists anywhere in the repository history to explain its absence in the current branch.

## Usage Method
1. Use `git log --all -S<symbol>` to search for commits that add or remove the exact symbol string across all local and remote branches.
2. If found, use `git branch -a --contains <commit>` to locate which branches contain the commit — this confirms whether the symbol exists globally or only in specific branches.

## Notes
- Relying only on git log of the current file or current branch misses definitions on other branches or in other files; must use --all -S to confirm global absence.
