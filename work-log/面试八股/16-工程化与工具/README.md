# 16-工程化与工具
> 层级：16｜建立：2026-10-30｜状态：空
## 【本类范围】
Git（分支策略、rebase/merge、冲突、reset/revert/cherry-pick）、Maven（依赖冲突、多模块、profile、scope）、构建与打包（jar/docker）、日志规范与级别、配置与环境隔离、CI/CD、灰度发布与回滚、单元测试与接口测试、代码评审。
## 【高频考点】
- merge 与 rebase 的区别，为什么公共分支不要 rebase
- reset --soft/--mixed/--hard 与 revert 的适用场景
- 依赖冲突的定位（mvn dependency:tree）与排除
- Maven 多模块的依赖顺序与循环依赖
- 环境隔离：profile / 配置中心，敏感配置不入库
- 日志分级、traceId 串联、生产不打 debug
- 上线回滚方案与数据兼容（先兼容再加字段，两步删除）
## 【与项目经历的关联】
- 三仓（后端 / PC / 移动端）分批原子提交、Conventional Commits、hunk 拆分（04-简历素材池）。
- Maven 全局 settings 的 profile 把编译目标压到 17 引发的构建问题（build 配置记忆）。
- changelog 式 SQL 迁移 + 上线把关（先加列兼容、回填、再收口）。
- 构建产物中毒 class、增量编译“Nothing to compile”不可信（03-踩坑手册）。
## 【待补题目】
- [ ] merge / rebase / cherry-pick 的选型
- [ ] reset 三种模式与 revert
- [ ] 依赖冲突定位与解决
- [ ] 数据库变更与代码发布的兼容顺序
- [ ] 一次构建/打包故障的排查复盘
## 【题目记录】
新增按 `## <题面> · <YYYY-MM-DD>` 追加，字段用 `### 【答案】` `### 【追问】` `### 【关联】`。
