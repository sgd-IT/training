---
title: "GitHub单文件100MB大小限制"
usage_scenario: []
keywords:
    - "GitHub"
    - "文件大小限制"
    - "git rm --cached"
source: "auto"
---

GitHub对单个文件大小限制为100MB，超过此限制的文件（如JAR包）会导致git push被拒绝，需使用`git rm --cached <file>`从暂存区移除后重新提交。（来源：Bash）
