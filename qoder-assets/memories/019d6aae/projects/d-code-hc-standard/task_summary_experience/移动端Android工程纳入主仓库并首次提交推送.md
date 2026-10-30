---
title: "移动端Android工程纳入主仓库并首次提交推送"
usage_scenario:
    - "把被gitignore忽略的子工程纳入现有仓库"
    - "首次导入无基线的整工程提交与脱敏排查"
    - "排查git index.lock孤儿锁与构建产物误入库"
keywords:
    - "other1工程入库"
    - "gitignore解除忽略"
    - "index.lock孤儿锁"
    - "dry-run排除验证"
    - "移动端首次提交"
source: "auto"
---

## Task description
- Core requirement: 用户要求"移动端提交"，随后指示"推送"，把 hc-standard 的 Android 移动端工程纳入版本管理并推到远端
- Task background: 移动端工程位于 d:\code\hc-standard\other1，此前被根 .gitignore 第 56 行 `/other1/` 整体忽略，自身无 .git，不在任何仓库内；另有旧副本 d:\code\安卓\安卓\other1（2026-03）与另一项目仓库 anwei-fire-remote-monitor-mobile（工作区干净），需先定位活跃工程再定落点。经确认选择"纳入 hc-standard 仓库"方案

## Execution process
1. 用 `git -C other1 rev-parse --show-toplevel` + `git check-ignore -v` + `git ls-files other1` 三条证据确认 other1 非独立仓库且被父仓库忽略；按 LastWriteTime 对比判定 d:\code\hc-standard\other1 为活跃工程（其余候选目录均非 git 仓库）
2. 编辑根 `.gitignore`：删除 `/other1/` 一行，解除对移动端工程的整体忽略
3. 编辑 `other1/.gitignore`：追加 `build/` 与 `.gradle/` 两条规则 —— 原文件只有 `/build`，只匹配工程根目录，**匹配不到 app/build**，不加会导致 Gradle 输出 3300+ 个产物文件入库
4. 量化范围：递归统计 3813 个文件，排除 .gradle/.idea/build/app\build 后 506 个 / 8.5MB；再用 `git add --dry-run other1` 得 504 条待加项，并用 Select-String 反查 jks/local.properties/app/build/.idea/.gradle 零命中作为排除生效的判据
5. 遇障修复：管道 `| Select-Object -First 10` 提前关闭使 git add 中断，遗留 `.git/index.lock` 孤儿锁，后续命令报 "Unable to create index.lock: File exists"；先用 `Get-Process git` 确认无进程持有 + 核对 lock 的 LastWriteTime 后再 DeleteFile 删除，重跑 dry-run 得 504
6. 提交前安全抽查（Grep 全 app/src）：硬编码密钥/凭据 0 处、System.out.println 与 mockSuccess/skipPush/if(Boolean.TRUE) 硬拦截分支 0 处、日志仅 BaseActivity/BaseFragment 的 Log.i 包装、TODO 2 处历史注释；HttpInterface.java 生效行为生产地址 http://220.203.23.144:28080/
7. 分两笔提交：`chore(gitignore)` 只含两处 .gitignore 变更（8a61a20）；`feat(mobile)` 含 503 个文件 +57236 行（d006875），提交信息写 UTF-8 文件后 `git commit -F` 传入，中文不作为命令行参数
8. 推送：先 `git log origin/master..HEAD` 列清单，再在沙箱外执行 `git -C d:\code\hc-standard -c safe.directory=D:/code/hc-standard push origin master`，结果 d30c43e..d006875 fast-forward，无 stash/rebase

## Related files
- /.gitignore
- /other1/.gitignore
- /other1/app/build.gradle
- /other1/app/src/main/java/com/sckj/other1/http/HttpInterface.java
- /other1/settings.gradle
- /other1/gradle/wrapper/gradle-wrapper.jar

## Notes
- 签名密钥 other1.jks 与本机 local.properties 靠 other1/.gitignore 的 `*.jks`/`local.properties` 规则天然排除，但代价是其他人拉库后无法直接出包，密钥需线下传递
- 首次导入无基线，无法按功能拆最小单元，只能整工程一笔入库；改动前的文件级 diff 检查在此场景退化为"密钥/调试残留/配置回切"三类抽查
- application-druid.yml 保持未提交状态（配置文件不入库约定），未被本次两笔提交夹带
- 移动端的 Java 源文件均为 tab 缩进，编辑时注意既有规范

## Task overview
已完成：hc-standard 移动端 Android 工程首次纳入版本管理并推送到远端 gongmao/hc-standard.git（master 与 origin/master 同步，共 503 文件 +57236 行，分 chore(gitignore) 与 feat(mobile) 两笔）。已排除入库：other1.jks、local.properties、.idea/、.gradle/、app/build/、build/。移动端自此有版本基线可正常 diff。
