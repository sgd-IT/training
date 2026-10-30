---
title: "git push 结果须以 sha 区间行与 status -sb 计数判定而非报错文本"
usage_scenario:
    - "PowerShell下git push报NativeCommandError判断是否真失败"
    - "核验中文提交信息入库字节是否损坏"
    - "确认git是否主动做了输出编码转换"
keywords:
    - "git push核验"
    - "NativeCommandError误报"
    - "i18n.logOutputEncoding"
    - "spawnSync原始Buffer"
    - "status -sb计数"
source: "auto"
---

Bug 类：Windows PowerShell 下 `git push` 伴随 NativeCommandError 报错，据此误判推送失败而反复重推。

判定方法：
- 推送成败不看 stderr 与是否报 NativeCommandError，只看两点硬证据：输出中的 `旧sha..新sha  分支 -> 分支` 行，以及随后 `git status -sb` 无 ahead/behind 计数。第三重佐证用 `git ls-remote <远端名> <分支>` 的 sha 对比本地 `git rev-parse HEAD`（本项目后端/vue 仓远端名为 origin，移动端仓为 anwei）。
- 排除"git 主动转码"这一分支假设：用 `git config i18n.logOutputEncoding` / `i18n.commitEncoding` 是否为空来判定；为空说明 git 未做编码转换，乱码只来自控制台代码页。
- 需要断言中文提交信息真实字节时，用不经 shell 的 spawnSync 取 stdout 原始 Buffer 并以 hex 打印（hex 纯 ASCII 不受代码页影响），或与 Buffer.from(期望中文,'utf8') 做相等比对；execSync 走 shell 管道会被按系统代码页二次解码，读出的中文照样乱码。
- 【补充实测】node 的 execFileSync 直接取 Buffer 再 `toString('utf8')` 打出的中文**在控制台仍显示为乱码**（因为污染发生在 stdout 渲染层而非取值层），此时不能凭肉眼判定内容错误；正确做法是打印 `b.toString('hex')` 自行按 UTF-8 三段式解码，或用 `Buffer.from(期望中文,'utf8').equals(b)` 得布尔结论。另注意 `Buffer.from(s,'utf8').equals(b)` 只能证明字节是合法 UTF-8，不能证明中文语义正确，二者不可混用。
- 【PowerShell 解析陷阱】在 PowerShell 里把 `@{u}` 作为 git 参数传入会被当成哈希表字面量导致命令报错；查待推送清单改用 `git log --oneline origin/master..HEAD` 或 `git status -sb` 首行的 ahead/behind 计数。
- 同一核验思路适用于构建结果：Maven 增量编译会输出 "Nothing to compile"，此时 BUILD SUCCESS 不代表改动被编译。核验办法是看目标 class 文件的 LastWriteTime 是否为本次时刻加 Length 是否合理（如 `$env:JAVA_HOME='D:\Tool\jdk\jdk21'; mvn -pl <模块> -am compile -DskipTests -q` 后核对 ServiceImpl/Controller 三个 class 的时间戳），必要时先 clean 再编译。

适用边界：用于 Windows 终端下 git/mvn 等非 ASCII 输出的结果核验；实时流式查看日志场合不适用。乱码展示层的完整处置链见「PowerShell控制台GBK解释UTF-8致git中文输出乱码需转存文件」。沙箱拦截 git push（spawnSync EPERM、需提权执行）及推送全流程编排见「Windows终端git提交推送的字节级核验编排流程」。
