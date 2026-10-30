---
title: "Git 提交信息措辞偏好：监管功能使用\"监管统计\"而非\"驾驶舱\""
usage_scenario:
    - "编写 git 提交信息时"
    - "监管模块功能命名时"
    - "生成对外可见文本时"
keywords:
    - "git commit message"
    - "监管统计"
    - "驾驶舱措辞"
    - "regulator提交规范"
source: "user"
---

用户在 git 提交信息中明确要求不出现"驾驶舱"字样，监管模块的提交统一表述为"监管统计"（如 feat(regulator): 新增监管统计接口、新增监管统计页面）。该约束仅针对提交信息措辞，代码文件路径（如 src/views/regulator/cockpit/）与功能命名不受影响。
