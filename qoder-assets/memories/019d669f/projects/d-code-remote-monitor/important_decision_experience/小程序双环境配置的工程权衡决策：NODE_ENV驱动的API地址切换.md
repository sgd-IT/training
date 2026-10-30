---
title: "小程序双环境配置的工程权衡决策：NODE_ENV驱动的API地址切换"
usage_scenario:
    - "When designing dual-environment configurations for web/mobile applications"
    - "When choosing between config-file duplication and environment-variable-driven switching"
keywords:
    - "dual environment"
    - "NODE_ENV"
    - "API address"
    - "engineering trade-off"
    - "configuration strategy"
source: "auto"
---

Conclusion: Use NODE_ENV environment variable to drive the API address switch between development (HTTP local IP) and production (HTTPS domain).
Rationale (trade-off): Development convenience requires direct HTTP access to local IPs for rapid iteration, while production security requires HTTPS domain validation; a single configuration point controlled by NODE_ENV balances both needs without requiring separate config files or manual switches.
Rejected alternatives: Maintaining separate config files for dev/prod — dropped because it increases maintenance overhead and risk of config drift; using runtime detection (e.g., hostname) — dropped because it's less reliable and harder to test in CI/CD pipelines.
Applicable / expiry conditions: Holds as long as the build process supports NODE_ENV-based conditional logic; re-evaluate if the project migrates to a build system that does not support environment variables or if the requirement for local IP access during development is removed.
