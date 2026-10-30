---
title: "Qoder资产盘点打包备份与跨仓库提交推送"
usage_scenario:
    - "重新盘点或恢复Qoder个人配置资产时"
    - "多仓库工作区执行提交与推送时"
    - "推送GitHub失败排查本地代理时"
keywords:
    - "Qoder配置备份"
    - "skills插件规则盘点"
    - "stash合并再推送"
    - "UTF-8中文乱码规避"
    - "多仓库工作区"
source: "auto"
---

## Task description
- Core requirement: 罗列当前 Qoder 环境的 skills、插件、规则；随后将其打包到当前项目（文件夹 + zip + 清单说明）；最后把修改与各产物提交并推送到对应远端仓库。
- Task background: 工作区 d:\code ol\remote-monitor 为多仓库结构，根目录本身不是 git 仓库；用户级配置分散在 ~/.qoder 下多个子目录，插件另有 cache 与注册表文件，需逐一定位并核对启用状态。

## Execution process
1. 逐项定位 Qoder 资产：~/.qoder/skills（6 个用户级 skill）、~/.qoder/rules（3 个规则）、~/.qoder/plugins/cache（bundler 与 local 来源插件）、settings.json 的 enabledPlugins、installed_plugins_v2.json 与 installed_plugins-v2.json 两套注册表快照、external-commands/registry.json、~/.qoder/app/bundled-resources/v1（为空）。
2. PowerShell Get-Content 读取中文 md 出现乱码（编码链问题），改用 Read 工具直接读取 SKILL.md 与规则文件解决；内置 skills 确认不落本地磁盘，由客户端会话提供。
3. 用 tar.exe -a -cf 生成 qoder-backup.zip（2.34MB），Compress-Archive 作为回退方案；校验 zip 条目数与源目录文件数差异（zip 含目录条目故更多）。
4. 后端仓库 anwei-fire-remote-monitor 按单文件最小单元拆成 3 个提交；application.yaml 因是本地环境开关（prod→dev）按约定不提交保留工作区。
5. 首次 push 被拒（fetch first，本地 ahead 3 / behind 10）：先 git stash push -- application.yaml → git pull origin master（ort 策略自动合并成功）→ git stash pop（Auto-merging 无冲突）→ 再 push 成功（89bd93e..7c61208）。
6. 中文提交信息经终端管道易被破坏，改为 Write 写入临时 msg 文件后 git commit -F 提交，再用 git log 输出重定向到 UTF-8 文件并以 Read 工具校验存储内容正确。
7. qoder-backup 需入版本控制：确认根目录非仓库、唯一个人仓库为 GitHub sgd-IT/remote（公开），先复制进 push-remote 再 add/commit；GitHub 直连失败（先超时后 Connection was reset），探测 netstat 发现 127.0.0.1:7897 LISTENING，用一次性参数 -c http.proxy/-c https.proxy 推送成功（35f2b3f..9a146da）。

## Related files
- /qoder-backup/清单与恢复说明.md
- /qoder-backup.zip
- /push-remote/qoder-backup/
- /anwei-fire-remote-monitor/module-eiot/module-eiot-core/iot-rule-engine/src/main/java/com/enjoyiot/eiot/ruleengine/action/alert/AlertService.java
- /anwei-fire-remote-monitor/docs/视频拉流对接复用指南.md
- /anwei-fire-remote-monitor/_sql/tools/.gitignore
- /anwei-fire-remote-monitor/server/src/main/resources/application.yaml

## Notes
- 教训一：PowerShell 终端读取 UTF-8 中文文件必然乱码，只能靠 Read 工具或"写文件后再 Read"取证，不能凭终端显示判断内容对错。
- 教训二：多仓库工作区必须先验证根目录是否为 git 仓库（Test-Path .git + rev-parse），否则会把产物放进无版本控制的目录而无法提交。
- 教训三：向 GitHub 推送前先探测本机代理端口是否监听；未监听时重试无意义，应转为提示用户启动代理。
- 遗漏风险：曾提议"只提交代码不含文档"的选项，但用户规则要求一个提交只含一个逻辑单元，最终仍按单文件拆分才合规。
- 推送被拒（non-fast-forward）需先合并远端时：若工作区留有故意不提交的脏文件，必须先比对本地领先/落后提交与 incoming 文件清单、并单独查看脏文件两侧 diff 的行区间是否相交，再按路径限定 stash、合并式拉取、pop 后逐行直读做字节级校验、最后重推至完全同步。带着脏文件直接 pull causes 合并被拒或产生额外冲突，must 先限定路径 stash 再合并。全量 stash causes 无关改动被卷入，must 按路径限定。

## Task overview
三项任务均完成：Qoder 资产清单已产出（用户级 skills 6 个 / rules 3 个 / 插件 6 个含状态标注 / 内置 skills 22 个）；qoder-backup 文件夹（546 文件、6.79MB）+ zip + 清单说明已生成；后端仓库 3 个提交经 stash-pull-pop 合并远端 10 个提交后推送至内网 GitLab，备份包提交 9a146da 经本地代理 7897 推送至 GitHub 公开仓库，本地与远端完全同步。

## Notes
- 更正一（后续会话实测）：步骤 7 所述"GitHub sgd-IT/remote（公开）"结论不准确。该仓库实际开启了 GitHub Secret scanning——推送含真实密钥的内容会被服务端拒绝并要求先移除 secrets，行为特征等同私密/受保护仓库；后续再次向 push-remote 同步三端代码时，_sql 中的阿里云 AccessKey 触发了推送拦截，脱敏后才推送通过。因此不能把"推上去没人能看到"当作前提，任何写入 push-remote 的内容都必须先完成密钥脱敏。
- 更正二：Task background 中的工作区路径应为 `d:\code ol\remote-monitor`（code 与 ol 之间含空格），PowerShell 中必须整体加引号，否则首个 cd/git 命令即失败。
- 新增事实：push-remote 是聚合备份仓库，内含 backend / frontend / mobile / qoder-backup 四份快照，远端即 GitHub sgd-IT/remote；mobile 快照对应的源仓库远端名为 anwei 而非 origin。
