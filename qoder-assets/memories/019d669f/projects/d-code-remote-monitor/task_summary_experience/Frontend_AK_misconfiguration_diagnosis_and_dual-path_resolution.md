---
title: "Frontend AK misconfiguration diagnosis and dual-path resolution"
usage_scenario:
    - "When diagnosing frontend SDK Referer validation failures in local development"
    - "When resolving browser vs server API key mismatches in frontend integrations"
keywords:
    - "Baidu Maps"
    - "Referer validation"
    - "vite host"
    - ".env precedence"
    - "browser AK"
source: "auto"
---

## Task description
- Core requirement: diagnose and resolve 'APP Referer校验失败' error in Baidu Maps SDK integration for local development
- Task background: user reports failure on localhost:3000; the issue stems from mismatch between AK type (browser vs server), Referer white list scope, vite host binding configuration, and .env file precedence rules

## Execution process
1. Verified vite.config.ts L29 has `host: "0.0.0.0"`, confirming dev server listens on all interfaces — enabling direct IP access as a fast bypass
2. Confirmed .env.local does not contain VITE_APP_BAIDU_CODE, so it falls back to .env L20's value `fFlLMopNOdMI6b4TEpukrb8IzTlQE24B`
3. Verified this AK's white list only includes `*192.168.250.144*, *192.168.250.130*, *fire-remote-admin.anxunwang.cn*`, excluding `localhost`
4. Preemptively added `VITE_APP_BAIDU_CODE` line to .env.local (value unchanged) to simplify future AK replacement
5. Prescribed two remediation routes: Route A (fast IP access using existing AK) and Route B (robust browser-type AK replacement with restart+hard-refresh)

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\vite.config.ts
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\.env
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\.env.local

## Notes
- None

## Task overview
Successfully diagnosed the root cause of the Baidu Maps Referer failure: localhost access violates the current AK's white list; confirmed vite host binding supports IP access as a zero-change workaround; pre-emptively modified .env.local to enable one-line AK replacement; provided two actionable resolution paths with clear prerequisites and trade-offs.
