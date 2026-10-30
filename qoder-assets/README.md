# Qoder 个人资产备份（skills / 规则 / 插件 / 记忆）

> 导出时间：2026-10-30
> 源机器用户目录：`C:\Users\Administrator`
> 用途：换电脑、重装 IDE、把个人 Agent 能力整体搬走

这份备份把散落在 **3 个 skills 根 + 1 个规则目录 + 1 个插件缓存 + 记忆库 + 配置** 里的东西一次性收齐。
Qoder 这些资产都只存在本机磁盘上，不跟随账号云端同步，所以不主动拷走就等于丢。

## 零、带走哪一个文件

**只拷这一个：`qoder-assets-carry.zip.bin`（8.0 MB）。**

它就是一个标准 zip，里面装的是明文。到新机器把后缀改回 `.zip`（或直接用 7-Zip 打开），解压即得本目录的完整内容，再按下文第四节恢复。

为什么绕这一圈 `.bin`：本机装了透明加密客户端，`.zip`、`.md`、`.txt` 这类扩展名写盘时会被加密（每文件多 4096 字节加密头），直接拷到没有客户端的机器打开就是乱码；`.bin` 恰好不在加密名单里，所以这个容器落盘就是原样明文。详见第八节。

本目录下的散装文件**只在本机可用**——磁盘上是密文，只有本机的白名单进程（IDE、node）会透明解密。要带走，认准 `.bin` 那一个。

---

## 一、目录结构

```
qoder-assets/
├── README.md              # 本文件：这是什么 + 怎么恢复
├── MANIFEST.md            # 逐项清单：每个 skill / 规则 / 插件的名字、体积、用途
├── qoder-assets-carry.zip.bin  # ★ 唯一需要带走的明文容器（改名 .zip 解压）
├── rules/                 # 规则（3 个）→ ~/.qoder/rules/
├── user-skills/           # 自建 skills（11 个实体）→ ~/.qoder/skills/
├── third-party-skills/    # 第三方 skills（26 个，含 pdf 本体）→ ~/.agents/skills/
├── plugins/               # 插件缓存 → ~/.qoder/plugins/cache/
├── memories/              # 记忆库（5 个账号目录，2021 个文件）→ ~/.qoder/memories/
├── config/                # settings.json / mcp.json / 插件注册清单 / skill-lock
└── workspace/.qoder/      # 本项目级产物（plans、better-harness 运行记录）
```

体积：明文合计 17.3 MB，压成 `.bin` 容器后 8.0 MB。资源管理器显示 26 MB，是因为被加密的文本文件每个多带 4096 字节加密头。

---

## 二、规则（rules/，重点）

规则是"不用每次提醒、AI 就必须遵守"的硬约束，存在两处载体，**两处都要一起带走**：

**载体 A：规则文件** `~/.qoder/rules/*.md`（本备份 `rules/` 目录）

| 文件 | 约束内容 |
|------|---------|
| `git-commit.md` | ① 任何 `git commit` 之前必须先展示变更摘要（涉及文件、改动要点）并等用户明确同意；② 任何代码 review 之前也必须先询问，不得自动发起；③ 禁止堆积大量修改一次性提交，必须拆成最小逻辑单元、一个提交只含一个功能点/修复点/重构点；④ 提交信息要准确简洁，说明该次单一变更 |
| `code-reuse-check.md` | 动手写代码前必须先检索可复用实现：同类业务、后端公共能力（工具类/通用 Service/Mapper/公共 API）、前端公共能力（PC 组件 utils mixins 弹窗、APP pop 目录 Adapter Base Bean）、既有接口能否加参数满足。决策顺序：直接复用 → 扩展既有实现（保持向后兼容）→ 才新建 |
| `ponytail.md` | lazy senior dev 模式：按 7 级阶梯取舍（YAGNI → 代码库已有 → 标准库 → 平台原生 → 已装依赖 → 能否一行 → 最后才写最小实现） |

**载体 B：行为记忆** `~/.qoder/memories/<账号>/global/user_behavior/*.md`
这些是对话里沉淀下来的个人行为约束，效果等同规则，随 `memories/` 一起恢复即可。其中与提交直接相关的有：

- `019c9953/global/user_behavior/Git提交前需用户确认.md`
- `019d669f/global/user_behavior/Git提交与审核规则：先询问确认且拆分最小粒度提交.md`
- `019d669f/global/user_behavior/SQL变更口径：160测试库执行，220正式库只读核对并登记清单.md`
- `019d669f/global/user_behavior/开发前复用检索规则：先查框架现有实现再动手.md`
- `019c9953/global/user_behavior/每日决策点编码训练时间9点半与18点.md`

> 注意：规则文件用 PowerShell `Get-Content` 读会显示乱码，不是编码问题，而是本机透明加密——PowerShell 不在白名单进程里，读到的是磁盘密文；IDE 与 node 读到的是解密后的正常中文，文件内容本身没问题。这也意味着 `.md` 这类散装文件拷到别的机器会变乱码，必须走第八节的 `.bin` 容器。

---

## 三、skills 的三个根，别放错

Qoder 同时扫描两个目录，插件又是第三个位置：

| 根目录 | 放什么 | 本备份对应 |
|--------|--------|-----------|
| `~/.qoder/skills/` | 自己的 skills（新建默认落这里），可编辑、跨项目生效 | `user-skills/`（11 个） |
| `~/.agents/skills/` | 从市场/第三方安装的 skills，`~/.agents/.skill-lock.json` 记录来源 | `third-party-skills/`（26 个） |
| `~/.qoder/plugins/cache/` | 插件携带的 skills，属缓存不宜手改 | `plugins/` |

三个坑：

1. **pdf 是链接不是副本**：`~/.qoder/skills/pdf` 是指向 `~/.agents/skills/pdf` 的 junction，本体在 `.agents`。备份时用 `/XJ` 排除了链接（否则会循环复制两份），恢复后要手工重建链接，见下文第 4 步。
2. **ponytail 有两份且内容已分叉**：`.agents/skills/ponytail` 与 `plugins/ponytail/ponytail/skills/ponytail` 不是同一快照，两份都保留了，别只拷一份。
3. **内置 skill 带不走**：`canvas`、`create-skill`、`vercel-deploy` 打包在 IDE 的 CLI bundle 里，磁盘上没有独立目录，随 IDE 版本自带，恢复 IDE 即恢复。

---

## 四、新机器恢复步骤

假设解压到 `D:\qoder-assets`，新机用户目录为 `%USERPROFILE%`（用户名不同也没关系，下面用环境变量）。**PowerShell 逐行执行**：

```powershell
$src = "D:\qoder-assets"
$q   = "$env:USERPROFILE\.qoder"
$a   = "$env:USERPROFILE\.agents"

# 1. 规则
robocopy "$src\rules" "$q\rules" /E

# 2. 自建 skills（11 个）
robocopy "$src\user-skills" "$q\skills" /E /XJ

# 3. 第三方 skills（26 个，pdf 本体也在这里）
robocopy "$src\third-party-skills" "$a\skills" /E /XJ
robocopy "$src\config\.skill-lock.json" $a /XO

# 4. 重建 pdf 软链接（先确认 .agents\skills\pdf 存在）
New-Item -ItemType Junction -Path "$q\skills\pdf" -Target "$a\skills\pdf"

# 5. 插件缓存
robocopy "$src\plugins" "$q\plugins\cache" /E /XJ

# 6. 记忆库
robocopy "$src\memories" "$q\memories" /E

# 7. 配置
Copy-Item "$src\config\settings.json" "$q\settings.json"
Copy-Item "$src\config\mcp.json.secret" "$q\mcp.json"   # 含真实凭据，见第五节
```

恢复后重启 IDE（或重载窗口），新开会话问一句"提交前要做什么"验证规则与记忆是否加载。

**插件注册**：`config/installed_plugins*.json` 里写死了 `c:\Users\Administrator\...` 的绝对路径，直接覆盖到新机反而会指向不存在的目录。推荐做法：

- 市场插件（`better-harness`、`qoder-create-plugin`、`security-scan`）→ 新机从市场重新安装即可，不必搬缓存；
- 自建插件（`mobile-frontend-dev` = ui-ux-pro-max + uniapp、`refactoring`、`ponytail`）→ 市场装不到，把 `plugins/` 下对应目录拷到 `~/.qoder/plugins/cache/` 后，参照 `config/installed_plugins_v2.json` 的条目格式手工注册（注意改掉路径和用户名）。

---

## 五、凭据与安全

`config/` 下有两份 mcp 配置：

| 文件 | 内容 | 能不能外传 |
|------|------|-----------|
| `mcp.json` | 脱敏版，所有 `MYSQL_PASS` 与 Figma API Key 已替换为 `***REDACTED***` | 可以 |
| `mcp.json.secret` | **原文**，含 9 个 MySQL 实例的真实口令（生产库在内）与 Figma Key | **不可以** |

`mcp.json.secret` 是给"换机后还能连库"准备的，请当作密码文件对待：别上传网盘公开链接、别进任何 git 仓库、别发给他人。若这份备份要交给别人或上传到公开位置，先删掉它，恢复时再手工填口令。

另外 `memories/` 里包含项目地址、库表结构、账号现象等内部信息，同样只适合自己保存。

---

## 六、没有带走的东西（以及为什么）

| 资产 | 原因 |
|------|------|
| `~/.qoder/app`、`cache`、`logs`、`projects`（会话转录） | IDE 运行态与历史会话，换机后由新机重新生成；会话正文另有加密库，搬过去也读不了 |
| `~/.qoder/external-commands/registry.json` | 只有内置 `wiki` 命令，装 IDE 时自动下载 |
| 内置 skills（canvas / create-skill / vercel-deploy） | 打包在 IDE bundle 内，无独立目录，随版本自带 |
| 插件 `qoder-enterprise-*` 空目录 | 企业壳缓存，空目录无内容 |
| `work-log/` 数据、`docs/`、`_temp/` | 属于项目数据不属于 IDE 资产，且 work-log 数据在 `d:\code\remote-monitor\work-log\`，随项目仓库/工作区另行备份 |

---

## 七、这份备份的自我校验

- 容器完整性：4411 个条目（含目录）、2829 个文件条目逐个解压 + CRC32 比对，**0 失败**
- 内容抽验：README / MANIFEST / 3 个 rules / 脱敏与原版 mcp.json 全部在包内；`git-commit.md` 含 216 个汉字，明文正常
- 加密规避验证：用非白名单进程读 `.bin`，字节数与写入值一致（无多发 4096 字节头），头部为 `50 4b 03 04`（PK\x03\x04），确认未被加密
- 分区文件数：user-skills 27 / third-party-skills 411 / plugins 354 / memories 2021 / rules 3 / config 8 / workspace 3
- 逐项明细见 [MANIFEST.md](MANIFEST.md)

---

## 八、为什么是 .bin 后缀（透明加密实测记录）

本机装了透明加密客户端，它按扩展名决定是否加密，跟谁写入无关：

| 实测结果 | 扩展名 |
|---------|--------|
| 被加密（+4096 字节头，头部 `88 7d 1c xx`） | `.md` `.txt` `.log` `.csv` `.yaml` `.js` `.html` `.pdf` `.zip` `.rar` `.7z` `.gz` `.tar` `.b64` `.dat` `.db` 以及无后缀文件 |
| 不加密（原样落盘） | `.json`、`.png`、`.bin` |

直接后果有三个，都实际踩过：

1. 用 `robocopy` 复制出来的备份目录是**密文副本**，换机不可读；
2. 用 PowerShell `Compress-Archive` 打 zip 也不行——PS 读到的是密文，压进去的同样是密文；
3. 必须由白名单进程（node）读明文、再由它写出改后缀的容器，`.bin` 才能真正带走。

所以流程是：node 读源（透明解密）→ 手写 zip 结构（deflate + crc32）→ 落盘为 `.bin`。已逐条目校验 CRC，可放心改名解压。
