---
name: special-work-test
description: Designs and executes test paths plus edge-case expansion suites for the hc-standard 特殊作业 (special work permit) module and its adjacent modules (人员证书, 装置开停车, 风险单元). Use when the user asks for 测试路径, 扩展测试, 用例设计, 验收路径, 回归验证, or wants to verify 作业票, 监护人, 气体检测, 中断恢复, PDF导出, 双人监护 features in hc-standard. Also applies when turning a 需求台账 into an actionable test checklist.
---

# 特殊作业测试路径与扩展测试设计

使用本 skill 时，输出开头标注【项目名·special-work-test】，项目名取当前工作区根目录名（如 hc-standard）。

## 核心纪律（先读这三条，否则会白测）

### 1. 需求台账的"未完成"不可信

台账是甲方填的，代码状态与台账可能严重背离。判断完成度必须**三层同时取证**，任一层有命中即视为已实现：

| 层 | 查什么 | 怎么查 |
|---|---|---|
| 后端 | 方法名是否存在 | `Grep` Service/Controller：`interruptWork` `resumeWork` `checkGasDetection` `validateGuardian` `addWorkSensor` `updateWorkSensor` `deleteWorkSensor` `exportPdf` `print` |
| APP | 按钮/页签文案 | `Grep` other1 的 Activity/Fragment/layout：中断作业、继续作业、实际开工时间、取样分析 |
| PC | 事件处理函数 | `Grep` ruoyi-ui 的 index.vue：`handlePreview` `handleExportPdf` `handleDetail` |

**只有三层都无命中，才是真未实现。** 本项目已踩过多次：以为 PC 无打印入口、以为 APP 无中断按钮、以为气体检测时间被后端覆盖，全部与实际相反。

### 2. 扩展用例要从代码卡点反推，不能凭想象

读实现时重点抓这 6 类卡点，每类对应一组扩展用例：

| 卡点类型 | 典型形态 | 反推出的用例 |
|---|---|---|
| 解析/去重逻辑 | `split("[,，;；]")`、`distinct()` | 分隔符变体、重复值、空白填充 |
| 阈值/区间判定 | `<=` vs `<`、`30 * 60 * 1000` | 临界值 ±ε、越界、负值、未来时间 |
| 枚举/白名单 | `switch(status)`、`if (code == "11")` | 全部枚举值各走一遍、未命中分支 |
| 查询过滤条件 | `where status=3 and sync_id is not null` | 被过滤的人能否通过别的路径进 |
| 状态机前置 | `if (!"2".equals(status)) throw` | 非法状态调用、重复调用、跨状态跳转 |
| 异常兜底 | `catch` 后降级、`try/catch` 不影响主流程 | 触发降级后数据是否一致 |

同时要抓**前后端口径不一致**——这类往往是真 bug：
- 后端按姓名反查归属、前端按 ID 归属 → 重名场景结论相反
- 后端不卡 `status`、前端列表卡 `status=3` → 同一批人两边可见性不同
- 平台枚举无某状态 → 本地扩展状态推送时必须归一

### 3. 台账/需求原文与实现的偏差要单独记

需求写"限定监护人操作"，实现只看票状态不看角色 —— 这是**偏差**，不是缺陷判定；需求写"自动开关物理装置"，系统根本没有下发通道 —— 这是**语义冲突**，需甲方澄清。凡与需求原文不符的点，单列"偏差与待澄清"一节，不要混进测试用例里。

### 4. 列表接口与详情接口可能返回不同状态（最容易误判为缺陷）

**已修复（2026-10-27）**：原先 `selectBaseWorkSpecialByOuterId` 有一段**权限掩码**，非管理员且非当前操作人时把 `workStatus` 强行改成 `"1"`：

```java
// 旧代码（已移除）
if (!(SecurityUtils.getUserId()).equals(baseWorkSpecial.getCurOperatorId()) && !isAdminUser()) {
    baseWorkSpecial.setWorkStatus("1");
}
```

后果：同一个 outerId，**列表接口返回真实状态、详情接口返回掩码后的 "1"**。而 APP 的状态按钮由详情接口驱动 → **APP 看不到按钮，看起来像功能坏了**。

现在改为：状态保持真实，另置 `viewOnly` 标记（`true` = 既不是开票人也不是监护人），APP 据此隐藏按钮并提示。**排查"按钮不见了"先查 `viewOnly` 与 `guardianUserIds`/`applyUserId`，不要怀疑环境。**

**操作权口径（已落地）**：可对本票执行中断/继续/开工/完工的人 = **开票人 + 监护人**（管理员放行）。
- `base_work_special.applyUserId` 开票人账号ID，建票时写入
- `base_work_special.guardianUserIds` 监护人账号ID串，建票时按 `jhr` 姓名反查 `sys_user` 得到
- 关联口径是**姓名**不是手机号（实测两边手机号不一致，姓名命中 140/155）
- 后端四处接口 `interruptWork`/`resumeWork`/`startWork`/`endWork` 均有 `checkOperatePermission` 拦截，提示形如「你不是本票的开票人或监护人，无法中断作业（本票监护人：刘建飞）」

> 遗留：外施队人员当前无 `sys_user` 账号，其监护人身份反查不到账号 ID，自然无法登录操作——这是账号体系现状限制，不是代码缺陷。

### 5. 权限校验必须排在业务校验之前（否则形同虚设）

`interruptWork` / `resumeWork` / `startWork` / `endWork` 四个接口的 `checkOperatePermission` 必须在方法**最前**，早于会签完成、安全措施签字、气体检测等业务校验。否则业务条件恰好满足时非授权人可直接操作，拦截形同虚设。

**典型症状**：非授权人调 `startWork` 返回「现场会签未完成」而不是「你不是本票的开票人或监护人」——说明权限校验被排在了业务校验之后。

## 环境准备（缺一项就会误判失败）

| 项 | 落点 | 缺失后果 |
|---|---|---|
| 8 个 docx 模板 | `D:\ruoyi\uploadPath\template\work_special_{dh,gc,ld,sx,dz,dt,dl,mb}.docx` | 导出 500 FileNotFound |
| 打印输出目录 | `D:\ruoyi\uploadPath\print\` | 导出 500（目录不存在） |
| 后端服务 | IDEA 启动，监听 28080 | 全链路不可用 |
| APP 后端地址 | `HttpInterface.IP` 硬编码 `http://192.168.250.130:28080/` | 改 IP 会同时改变签字分支 |
| 真机 | MuMu `127.0.0.1:7555`，adb 在 `D:\Tool\sdk\platform-tools\adb.exe` | — |

**两套库都要认清**（本项目最易翻车的点）：

| 通道 | 落点 | 说明 |
|---|---|---|
| MySQL MCP `tgr-gz-vue` | 正式库 `anwei1-0001`（220.203.23.144） | 只读；`hc-standard-test` 已 disabled，调用报 `not found MCPHost severName` |
| 本地后端 28080 | **测试库** 192.168.250.160 | 接口写入都落这里；用 `mysql.exe --host=192.168.250.160` 直连读写 |
| 正式库直连 | `mysql.exe --host=220.203.23.144` | 需显式指定，勿混用 |

同一 outerId 在两套库可能都存在（造数时常用固定 ID），MCP 查不到 ≠ 本地后端没有。**任何结论前先 `SELECT @@hostname`**。

**APP 取 token 走 PC 登录接口**：`POST /login`（带算术题验证码 `captchaImage` 返回的 `capStr` 形如 `7-5=?`）。`/api/base/appPwdLogin` 会强制校验 `user_type='01'`，PC 的 `/login` 不校验——用 APP 账号（如 `15671066199`）走 `/login` 也能拿到可用 token，比 admin 更好用（admin 试错多次会被锁 10 分钟）。

**adb 操作纪律**：用 `uiautomator dump` 取控件 bounds 中心坐标再点，别按截图估算（误差 80px 就点空）。Toast 只存活约 2 秒，点「确定」后**立即** `screencap` 再 `pull`，不要 `sleep` 之后再截。

**模拟器两个硬约束**（不是缺陷）：
- 无蓝牙硬件 → 现场签字必卡 iBeacon 校验，需用 `POST /api/special/updApprove` 补签绕过
- 无 PDF 阅读器 → APP 下载成功只走兜底 toast，判成功要看 `adb shell ls -l /sdcard/other/`

## 特殊作业 9 条需求速查

菜单路径按 `sys_menu` 实查，勿凭路由推断。

| 需求 | PC 入口 | APP 入口 | 关键断言点 |
|---|---|---|---|
| 43 外施队办票 | `企业管理→人员管理` /cbs/contractorPeople | 首页→票型→添加 | 作业单位可挂承包商企业；监护人弹窗跨企业 |
| 显示开始时间 | `作业管理→特殊作业` /ticket/workSpecial | 现场会签页签顶部 | PC 两列 + 抽屉；APP 仅开工后显示 |
| 44 双人监护 | — | 建票→监护人弹框 | 人≥2 且 company_type 同时含 1 和 2；**只卡新建** |
| 32 气体多次录入 | PC 无录入入口 | 取样分析页签→新增 | 录入即时判定；已验收态按钮消失 |
| 34 中断恢复 | 特殊作业→详情抽屉 | 详情页右上按钮 | workStatus 2↔5；中断中不可完工 |
| 35 开工前气体检测 | — | 详情页右上「开始作业」 | 30 分钟窗口；仅动火/受限空间生效 |
| 外施队人员证书 | `企业管理→人员证书` /cbs/contractorPeopleCert | 建票选人→证件图标 | 企业类型=承包商可筛；回显依赖 sync_id |
| 46 打印预览+PDF | 特殊作业→行内预览/导出 | 详情页右上「下载」 | 8 票型逐个点；PDF 落 /sdcard/other/ |
| 51 装置自动开停车 | `平台对接→装置开停车` /platement/deviceCtrl | — | **备案登记制非控制执行**，语义待澄清 |

> 需求 51 的“到点自动”实际落在**风险单元启停**（`base_risk_unit_log` + `autoSyncUnitStatus`），实测：插窗口内停机记录→单元自动 0→1；改过期→自动 1→0。入口是 `风险管理→风险单元`。

**监护人选人弹框的维度切换会丢勾选（已修复 2026-10-27）**：

左侧四个维度：`全部` / `本公司` / `承包商` / 岗位（如焊工）。

- 原实现：点「本公司」「承包商」走本地 `applyData()`，勾选保留；点「全部」或岗位维度走 `setSearchPeople()` → `setData()` 里 `mAllPeople.clear()` 重建，**已勾选的人被清空**
- 现实现：`mAllPeople` 改为按 `姓名|企业类型` 索引的**人员池**，`setData()` 改为**合并入池并复用池中对象**（保留 `show` 勾选态）；「全部」维度也改走本地过滤，不再重新请求
- 额外修复：列表项点击改为 `setShow(!isShow())`，可反复点击切换勾选（原来写死 `true`，勾错了没法取消）

实测：勾内部人 → 切「承包商」勾外施队人 → 确定，监护人栏正确回填「胡军,王外施」；只勾 1 人确定仍被「监护人至少 2 名」拦下。

**票型字典**（`TicketsType`）：本地 type→平台 code→模板
`1 动火→11→dh` / `2 受限→14→sx` / `3 盲板→18→mb` / `4 高处→12→gc` / `5 吊装→15→dz` / `6 临电→13→ld` / `7 动土→16→dt` / `8 断路→17→dl`

**状态字典**（`b_workstatus`）：`0 未开始` `1 已验收` `2 进行中` `3 未验收` `5 中断`（5 为本地扩展，推平台归一为 2）

**企业类型**（`b_c_company_type`）：`1 本公司` `2 承包商`

## 串行总流程（一条链验 6 个需求，最省时）

```
APP 建票（作业单位=外施队）
  → 监护人勾 1 内部 + 1 外施队          验证 44
  → 提交
  → 取样分析录 2 条气体（一条回填 31 分钟前） 验证 32
  → 点「开始作业」应被 30 分钟窗口拦下        验证 35
  → 补录合格气体 → 开工成功
  → 右上「中断作业」→ 填原因 → 确定           验证 34
  → 状态应显示「已中断」
  → 「继续作业」→ 状态回「进行中」
  → PC 特殊作业→详情（看中断记录+实际开工时间）
  → 预览 / 导出 PDF                         验证 46
```

## 扩展测试设计套路

拿到一个功能，输出扩展用例时按这个顺序铺，**每条都要指明"改什么 → 期望什么"**，不要写"测试边界情况"这种空话：

1. **先列实现里的所有分支**：读 Service 方法，把每个 `if/throw/switch` 分支变成一条用例
2. **再叠输入变形**：对每个外部输入，构造合法/边界/非法/空/超长/特殊字符
3. **再叠时序**：正常顺序、跳过前置、逆序、重复、并发
4. **再叠数据前置**：正常数据、缺字段、空值、被逻辑删除、状态不符、缺 sync_id
5. **最后叠绕过**：直接调接口绕过前端校验、改请求体字段、匿名访问、越权到他人数据

每条用例标 **优先级**：必跑（能出真问题）/ 建议跑 / 快速跑。

## 输出格式

```
## 扩展测试清单

### <功能名>
| # | 扩展点 | 怎么测 | 预期/风险 |
|---|---|---|---|
| 1.1 | 解析变形 | jhr 传中文逗号分隔 | 后端拆 4 种分隔符应通过 |

### 必跑 / 建议跑 / 快速跑
```

## 数据库查询

**统一走 MCP**（`mysql_query` 工具），不要用命令行 mysql 客户端反复试错。

当前可用服务名：`tgr-gz-vue`（只读，落点 `anwei1-0001` 正式库）。
`hc-standard-test` 在全局配置里已 `disabled`，调用报 `not found MCPHost severName`；项目 `mcps/` 缓存目录里仍列出的名字不可信，**以实际调用是否成功为准**。

> **落点必须先确认**：任何结论前先 `SELECT @@hostname`，确认连的是哪套库。MCP 返回的 datetime 带 Z 后缀，比库内真值偏 8 小时，判读一律用 `DATE_FORMAT(col,'%Y-%m-%d %H:%i:%s')` 取字符串原值。

常用验证 SQL 见 [reference.md](reference.md) 末尾「校验查询速查」。

## Additional Resources

- 9 条需求逐条完整测试路径 + 约 90 条扩展用例全量清单：见 [reference.md](reference.md)
