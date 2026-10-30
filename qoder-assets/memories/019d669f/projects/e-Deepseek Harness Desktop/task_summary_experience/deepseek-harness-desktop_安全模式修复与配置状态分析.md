---
title: "deepseek-harness-desktop 安全模式修复与配置状态分析"
usage_scenario:
    - "deepseek-harness-desktop 安全模式退出后状态排查"
    - "绿盾加密修复后残留问题诊断"
    - "桌面应用僵尸进程识别与清理"
keywords:
    - "deepseek-harness-desktop"
    - "安全模式"
    - "zombie process"
    - "profile migration"
    - "绿盾加密"
source: "auto"
---

## Task description
- Core requirement: continue fixing issues after green-shield encryption was mitigated, specifically addressing remaining problems like zombie processes, encrypted files, and safe-mode configuration state
- Task background: application exited; .store.dat shows active_profile="safe"; safe/pnpm-lock.yaml was re-encrypted after repair; desktop.log contains profile-switching context

## Execution process
1. Process check: confirmed deepseek-harness-desktop.exe is not running; only node.exe zombie process 65036 remains
2. Zombie cleanup: executed Stop-Process -Id 65036 -Force to terminate the orphaned process
3. Safe mode analysis: read .store.dat → confirmed "active_profile": "safe"; read safe/package.json and web/cordis.yml → confirmed plugin differences between profiles; grep desktop.log for 'profile|safe' → found INTERNAL_PLUGIN_PROFILE_MIGRATED log indicating profile migration occurred
4. Encrypted file scan: PS scan of .dsh (excluding node_modules/sessions) confirmed safe/pnpm-lock.yaml remains encrypted

## Related files
- C:\Users\Administrator\.dsh\profiles\safe\package.json
- C:\Users\Administrator\.dsh\profiles\web\cordis.yml
- C:\Users\Administrator\AppData\Roaming\io.github.hairyf.deepseek-harness-desktop\.store.dat
- C:\Users\Administrator\AppData\Roaming\io.github.hairyf.deepseek-harness-desktop\logs\desktop.log

## Notes
- None

## Task overview
Successfully completed the post-encryption-fix investigation: confirmed app exit and safe-mode persistence; terminated zombie process 65036; identified that safe/pnpm-lock.yaml remains encrypted and requires manual transplant; verified profile migration history in desktop.log; all findings provide a clear path for full restoration to normal mode.
