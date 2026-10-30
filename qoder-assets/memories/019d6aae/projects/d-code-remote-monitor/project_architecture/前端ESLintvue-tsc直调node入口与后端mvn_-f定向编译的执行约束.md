---
title: "前端ESLint/vue-tsc直调node入口与后端mvn -f定向编译的执行约束"
usage_scenario:
    - "前端Vue文件改动后需ESLint或vue-tsc权威校验"
    - "SFC样式块改动后需sass编译验证"
    - "IDE问题检测不可用需替代验证手段"
keywords:
    - "ESLint node直调"
    - "vue-tsc node直调"
    - "sass CLI load-path"
    - "顶层scss入口校验"
    - "GetProblems语言服务未就绪"
    - "mvn -f定向编译"
    - "Select-String产物反查"
source: "auto"
---

本工作区改动后的静态验证链路与三个环境约束（均实测）：
1. 前端仓库 anwei-fire-remote-monitor-vue 的命令行校验一律用 node 直调包内 bin 入口：ESLint 用 `node node_modules\eslint\bin\eslint.js <相对路径文件>`，类型检查用 `node node_modules\vue-tsc\bin\vue-tsc.js --noEmit`（package.json 脚本名为 ts:check），并在 Push-Location/Set-Location 进入项目目录后执行；直接跑 `npx eslint`/`npx vue-tsc` 会被 PowerShell 执行策略拦截（npx.ps1 禁止运行脚本，报 UnauthorizedAccess/PSSecurity），改用 cmd 包装则被沙箱以"终端为 PowerShell 须用 PowerShell 命令"拒绝。ElMessage 等 API 已由 unplugin-auto-import 全局自动导入（见 .eslintrc-auto-import.json），页面里无需显式 import。
2. Java IDE 语言服务可能长时间处于 "not ready or still initializing"，此时 GetProblems 返回"Unable to detect code problems"不可作为判据；后端权威验证以 `mvn -f anwei-fire-remote-monitor/pom.xml -pl <模块列表> -am -DskipTests compile` 为准，且必须检查日志是否出现 Recompiling/Compiling N source files 才算真编译。
3. 工作目录默认为 d:\code\remote-monitor（多仓库根，无 pom.xml），Maven 命令不带 -f 会报 reactor 找不到模块。
4. 编辑工具返回体与问题检测在语言服务滞后时会给出过期快照（引用已删除的行号或已修正的源码），不应据此判定改动破坏了文件或回滚修改，改由上述权威命令裁决。
5. vue-tsc 全项目输出含数百条存量类型错误（最近实测 426 条，TS6133 未使用变量、IPageResponse 导入等历史问题），非零退出码与错误总数都不能作为本次改动有问题的判据；判据 = 按改动文件路径过滤输出 + 与 logs/vue-tsc-notice*.txt 历史基线（同文件同错对比行号漂移）比对确认无新增。ESLint 定向跑改动文件时，无输出且退出码 0 即干净。

补充（实测）——SCSS 样式块校验：
6. 校验 .vue 内的 scss 语法用 `node -e` 直调 sass.compileString，先用正则 `<style[^>]*>([\s\S]*?)<\/style>` 提取全部 style 块逐个编译（一个 .vue 文件可能同时有 scoped 与非 scoped 两个块），并打印块数以便确认没有漏提。
7. compileString 必须传 `url: pathToFileURL(path.resolve(vueFile))`，否则块内既有的相对导入（如 `@import './styles/common.scss'`）解析不到基准路径而误报失败；loadPaths 对以 ./ 开头的相对导入不起作用，只有 url 决定相对解析基准。
8. compileString 成功时仍会打印大量 darken()/lighten()/@import 的 DEPRECATION WARNING，这是全项目既有基线，判据只看每块输出的 OK/ERR 标记。
9. 校验**顶层 scss 入口文件本身**（如 src/styles/index.scss，内含 @use/@import 第三方包）走 CLI 而非 compileString：`node node_modules\sass\sass.js --no-source-map --load-path=node_modules src\styles\index.scss _temp_out.css`。缺 `--load-path=node_modules` 会因解析不到 element-plus 等包内 scss 路径而直接报错退出码非零，容易被误判成"sass 没装/语法有问题"。
10. 编译产物反查选择器时用 `Get-Content <out.css> | Select-String "<pattern>"` 并顺带 `-Tail` 看尾部确认文件非空；管道形式比 `Select-String -Path` 更稳（后者在本仓曾出现明明有匹配却输出为空的情况）。产物存在且大小合理而 pattern 零命中时，先怀疑检索方式而非结论。校验完必须删除临时 css 产物避免污染提交。

适用边界：本仓库三端（后端 Java / vue 前端 / mobile）改动后的静态验证阶段。不适用：运行时联调、端到端 PDF 导出等功能性验证（仍需真实启动服务）。
