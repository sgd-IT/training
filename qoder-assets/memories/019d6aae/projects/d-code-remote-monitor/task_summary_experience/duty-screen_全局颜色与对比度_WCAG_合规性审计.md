---
title: "duty-screen 全局颜色与对比度 WCAG 合规性审计"
usage_scenario:
    - "当需要对值守大屏（duty-screen）进行无障碍合规性审查时"
    - "当新增或重构HUD类大屏页面时参考颜色对比度最佳实践"
keywords:
    - "duty-screen"
    - "WCAG"
    - "对比度审计"
    - "全局颜色"
    - "无障碍"
source: "auto"
---

## Task description
- Core requirement: audit global colors and contrast for duty-screen, ensuring WCAG 2.1 compliance by checking root background and text colors.
- Task background: duty-screen is a standalone full-screen Vue page (remaining.ts); its .duty-screen root has no background, relying on transparent overlay over map; body remains white; several text elements (e.g., status badges, tooltips) fall below WCAG 4.5:1 threshold.

## Execution process
1. Read duty-screen/index.vue styles (SCSS + script) to extract color variables ($bg2, $t1, $glass-bg) and text color usages.
2. Grep for background/color patterns across SCSS and script to locate all text/background declarations.
3. Check router (remaining.ts) and global CSS (var.css) to confirm duty-screen is standalone and body has no dark fallback.
4. Compute WCAG contrast ratios for all critical text/background pairs (e.g., #ff4d4f on rgba(255,77,79,0.2) = 3.8:1).
5. Propose 14+ targeted edits: set .duty-screen background to $bg2, increase $glass-bg opacity to 0.96, adjust red/orange/blue text hues to meet AA, and update script fallback colors.

## Related files
- /src/views/eiot/firewatch/duty-screen/index.vue
- /src/router/modules/remaining.ts
- /src/styles/var.css

## Notes
- None

## Task overview
Completed a full WCAG 2.1 contrast audit of duty-screen: identified root transparency and near-transparent dialog background as critical risks; computed exact contrast ratios for all text elements; proposed and validated 14+ code edits to bring all text/background pairs above 4.5:1; ensured visual consistency with project’s #0d1f3c dark theme.
