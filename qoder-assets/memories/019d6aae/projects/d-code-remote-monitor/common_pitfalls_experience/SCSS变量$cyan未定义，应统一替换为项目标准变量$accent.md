---
title: "SCSS变量$cyan未定义，应统一替换为项目标准变量$accent"
usage_scenario:
    - "Sass编译报Undefined variable且变量名疑似拼写错误"
    - "在Vue单文件组件中引用未定义Sass变量导致构建失败"
keywords:
    - "SCSS变量"
    - "$accent"
    - "$cyan"
    - "Undefined variable"
source: "auto"
---

Vue项目中SCSS变量`$cyan`未定义，导致`ai-analysis/index.vue`编译失败、页面白屏。项目中实际定义的标准色变量为`$accent`（见`src/styles/variables.scss`等全局样式文件）。修复方式：将`$cyan`全部替换为`$accent`。（来源：Read/SearchReplace）

Sass 报错 `Undefined variable: "$cyan"` 是因变量名错误。该文件中实际定义的青色变量为 `$accent`（值为 `rgba(79,195,247,0.9)`），需将 `$cyan` 替换为 `$accent`。（来源：Read）
