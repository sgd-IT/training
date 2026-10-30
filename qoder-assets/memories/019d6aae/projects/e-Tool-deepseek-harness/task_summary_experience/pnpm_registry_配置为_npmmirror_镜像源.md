---
title: "pnpm registry 配置为 npmmirror 镜像源"
usage_scenario:
    - "When configuring pnpm for faster dependency installation in China"
    - "When troubleshooting slow or failed package downloads due to network issues"
keywords:
    - "pnpm"
    - "registry"
    - "npmmirror"
    - "mirror source"
source: "auto"
---

## Task description
- Core requirement: configure pnpm's registry to use the npmmirror.cn mirror source
- Task background: the project uses pnpm for dependency management; switching to a domestic mirror accelerates package installation, especially for large packages like claude-agent-sdk-win32-x64 and codex

## Execution process
1. Execute `pnpm config set registry https://registry.npmmirror.com` to set the user-level registry
2. Verify with `pnpm config get registry`; output confirms the value is now `https://registry.npmmirror.com`

## Related files
- ~/.pnpmrc (user-level config file, implicitly modified)

## Notes
- The configuration is written to the user-level .npmrc (shared with npm/yarn), not project-level .pnpmrc
- This change affects all pnpm commands globally for the current user
- Mirror source is compatible with pnpm's integrity hash verification — security is unaffected

## Task overview
Successfully configured pnpm to use the npmmirror.cn registry: command executed and verified; subsequent `pnpm install` will download packages from the domestic mirror, accelerating installation of large dependencies.
