---
title: "小程序权限放开业务诉求与user_type位掩码技术实现的权衡决策"
usage_scenario:
    - "When business proposes 'Mini Program permissions fully opened' and technical constraints exist"
    - "When assigning user_type values to new accounts to ensure both Mini Program access and platform isolation"
keywords:
    - "Mini Program permissions"
    - "user_type bit mask"
    - "login gate"
    - "platform isolation"
source: "auto"
---

## Usage Scenario
Description: When business requires all accounts to access Mini Program features, but the technical implementation relies on user_type bit mask for login gate and interface permission control.
Examples: When onboarding new accounts and deciding whether to assign user_type=3 (dual-end) or modify default values; when designing future platform-specific services.

## Usage Method
development_practice_specification: Business requirement 'all Mini Program permissions fully opened' must be implemented while preserving the user_type bit mask for platform account isolation; do not remove checkLoginPlatform or hasMobilePlatformAccess checks, as they are essential for future differentiated service strategies.

## Notes
- Removing all permission checks (e.g., deleting checkLoginPlatform) causes loss of platform account isolation and prevents future fine-grained operation and strategy development
