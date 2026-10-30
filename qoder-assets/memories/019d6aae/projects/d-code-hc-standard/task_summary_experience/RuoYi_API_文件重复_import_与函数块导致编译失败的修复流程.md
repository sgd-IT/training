---
title: "RuoYi API 文件重复 import 与函数块导致编译失败的修复流程"
usage_scenario:
    - "RuoYi 项目中 API 文件报 Identifier 已声明或语法错误时的排查与修复"
    - "前端 API 文件因合并冲突出现重复函数/重复 import 的标准化清理步骤"
    - "验证 API 文件是否与后端 Controller 路径完全匹配"
keywords:
    - "RuoYi"
    - "API 文件"
    - "重复 import"
    - "语法错误"
source: "auto"
---

## 任务描述
修复 leaderDutySchedule.js 因重复 import 和重复函数声明导致的 Syntax Error 编译失败。

## 执行过程
```mermaid
graph TD
    A[报错:Identifier 'request' has already been declared] --> B[Read 文件定位重复 import 和函数块]
    B --> C[SearchReplace 删除底部重复函数块及多余 import]
    C --> D[发现删除后 syncBatch 函数缺少闭合 }]
    D --> E[Read 确认缺失位置]
    E --> F[SearchReplace 补全 } 完成修复]
```

## 任务总结
RuoYi 前端 API 文件易因 Git 合并冲突误引入重复 import 和同名函数块，导致编译失败；修复需三步：1）定位并删除冗余代码块；2）检查函数闭合完整性；3）确保所有 API 路径与后端 Controller 严格一致。
