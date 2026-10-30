---
title: "uni-app showModal对象属性访问规范"
usage_scenario:
    - "When implementing confirmation dialogs with uni.showModal in uni-app projects"
    - "When troubleshooting TypeError in uni-app modal calls"
keywords:
    - "uni.showModal"
    - "WeChat mini-program"
    - "uni-app"
    - "confirmation dialog"
source: "auto"
---

## Usage Scenario
Description: When using uni.showModal in uni-app projects to display confirmation dialogs (e.g., sign-in/sign-out)
Examples: When implementing user-confirmation flows in WeChat mini-programs or other uni-app platforms

## Usage Method
uni.showModal: Use object property access (res.confirm) instead of array destructuring ([err]) because the Promise resolves to an object {confirm: true/false, cancel: true/false}, not an array.

## Notes
- Using const [err] = await uni.showModal(...) causes 'Invalid attempt to destructure non-iterable instance' TypeError; must use const res = await uni.showModal(...) and check res.confirm
