---
title: "Git提交前多阶段审查与分批提交流程"
usage_scenario:
    - "执行Git提交前进行代码审查和清理"
    - "将大型变更拆分为多个功能相关的独立提交"
    - "确保提交内容符合项目规范和安全要求"
keywords:
    - "Git分批提交"
    - "pathspec-from-file"
    - "commit -F编码"
    - "调试代码清理"
    - "沙箱checkout失效"
source: "auto"
---

## Task description
- Core requirement: submit a Git commit for accumulated project changes, following the strict rule of "user confirmation before git commit"
- Task background: the project accumulated 82 modified files and 4 new files over ~7 weeks, covering multiple modules (contractor management, alarm types, special work tickets); the task involved identifying and cleaning up temporary debugging code (SignTest.java) and refactoring duplicated logic

## Execution process
1. Conducted a comprehensive ponytail-review to identify over-engineering: deleted SignTest.java (79 lines), converged 7 identical LuBoxiang API calls into one helper method, removed 7 unused fields from ContractorUnitDto, and extracted common validation logic in PlatementValidator
2. Applied all cleanup modifications and verified success with a full Maven compile
3. Split the 85 files into 9 functionally coherent commits: risk task batching, alarm type extension, special work ticket refinement, contractor platform integration, duty schedule synchronization, scheduled task setup, permission enhancement, and maintenance updates

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementBaseApi.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementRiskCheckUnitApi.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/entity/ContractorUnitDto.java
- /ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/DataSyncTask.java
- /ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementContractorApi.java
- /ruoyi-admin/src/main/resources/application-druid.yml
- /.gitignore
- /ruoyi-ui/README.md

## Notes
- In DataSyncTask.java, the comment and log message state "7 days" while the code actually calls deleteJobLogOlderThanDays(20); this inconsistency should be corrected.

## Notes
- 后续同类任务（约 78 个文件、拆成 8 个提交）补充三条可复用经验：
  1. 当同一个大文件（如 BaseWorkSpecialServiceImpl.java、ApiSpecialController.java、workSpecial/index.vue）同时含多条功能线改动时，git 按文件提交无法再细分；做法是按"主线归属"整体并入一个提交，并在清单里显式说明其余提交的接口层由该提交带出，避免用户误判粒度违规。
  2. `git checkout -- <file>` 在本工具沙箱的 Node fallback shell 下可能静默不生效（grep 仍能命中被删内容）；此时改为用 SearchReplace 手工精确删除新增片段，再以 `git diff --stat -- <file>` 无输出作为"已与 HEAD 一致"的判据。
  3. 多文件提交用 `git add --pathspec-from-file=.tmp/batch/NN.txt`（每行一个路径，git 2.53 支持）规避命令行长度与中文乱码；提交信息一律写 UTF-8 文件后 `git commit -F`，禁止把中文作为命令行参数传入。注意 `git show --stat | Select-Object -First N` 会因提前关闭管道使整条命令返回 ExitCode 1，但提交本身已成功，勿据此重复提交。
- pom.xml 是否入库并非绝对排除项：当新增依赖是编译前提（如 xdocreport 之于 PDF 导出）时，需主动向用户提示"不提交会导致他人拉取后编译失败"并由用户拍板。
