---
title: "PC火警处置中心页面彩虹配色收敛为三色体系"
usage_scenario:
    - "再次改造alarm-order页面配色或状态标签时"
    - "Element Plus弹窗teleport导致主题变量失效时"
    - "前端页面做局部primary色覆盖与色彩变量清账时"
keywords:
    - "彩虹配色收敛"
    - "Element primary覆盖"
    - "teleport弹窗主题变量"
    - "alarm-order页面"
    - "去AI味"
source: "auto"
---

## Task description
- Core requirement: 用户要求"不要用彩虹配色"，对 PC 火警处置中心（alarm-order 页面）残留的红/琥珀/绿/蓝多色语义体系做全量收敛。
- Task background: 前一轮已完成 emoji 清除与部分去 AI 味改造；页面仍混用 Element 全局默认蓝 #409eff 与自定义 $amber/$emerald/$sky 等色彩变量，视觉显"彩虹"。目标色板为「主题青绿 #199b96 + 中性灰 + 仅火警/紧急红」三色体系。文件：anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/alarm-order/index.vue（约 3175 行）。

## Execution process
1. 根因定位：全局 grep 确认 src 下无任何 --el-color-primary 覆盖定义，应用 primary 即 Element 默认蓝 #409eff；#199b96 仅存在于 alarm-order 局部 SCSS。
2. 主色局部收敛：在 .alarm-center 根节点挂 CSS 变量块，将 --el-color-primary 全套 7 档 light/dark 变体覆盖为 #199b96，使 primary 按钮、复选/单选、输入聚焦、分页器、加载转圈、默认 el-tag 一次性变色。
3. teleport 弹窗处理：el-dialog/el-drawer 渲染到 body 拿不到页面级变量，给 13 个弹窗/抽屉加 class="alarm-center-dialog"，并新增非 scoped <style lang="scss"> 块挂同套变量；两处 ElMessageBox.confirm 通过 options.customClass 同名收敛。
4. 模板/脚本改色（19 组替换）：状态标签 success/warning→primary/info；风险等级 B/C 级 warning→info（A 级保留 danger）；时间线圆点 #409eff/#67c23a→#199b96（告警触发保留 #f56c6c）；"已发送短信"内联蓝→#199b96；历史状态列、事件标签等统一 info。
5. 样式清账：删除 $amber/$amber-soft/$emerald/$emerald-soft/$sky 五个变量（grep 确认零引用后移除），保留 $coral/$coral-soft 供火警语义。
6. prettier 修复：加 class 后 13 个单行标签超宽，按 prettier 期望的多行属性形式重排（隔离副本 diff 定位 hunk）。
7. 三重验证：vite dev server 请求模块返回 200（SCSS+模板编译通过）；vue-tsc --noEmit 仅剩 4 条改动前已存在的 TS6133；prettier 隔离校验改动区域零 hunk。累计 +195/-312。

## Related files
- src/views/eiot/firewatch/alarm-order/index.vue

## Notes
- 删除 SCSS 颜色变量前必须先 grep 全文件确认零引用，否则 vite 编译直接失败。
- SearchReplace 批量替换可能部分成功（本次 10/11），失败项需回读当前实际文本再重试，不能凭记忆构造 original_text。
- 给单行组件标签追加属性会触发行宽超限，prettier 要求整段重排为多行属性格式；应预留一步"按 prettier 输出格式修正"的收尾环节。
- 刻意保留的非主题色：误报确认弹窗的标准 el-alert（type=warning）、"敬请期待"共享 DevelopingTag、toast 提示——均为标准组件语义，不纳入本页收敛范围。

## Task overview
已完成：页面主色由 Element 默认蓝局部覆盖为主题青绿 #199b96（含弹窗 teleport 场景），琥珀/绿/蓝全部退场为中性或主题色，红色仅保留火警/紧急语义；5 个废弃色彩变量删除；vite/vue-tsc/prettier 三重验证通过。改动未提交 git，待用户浏览器核验后确认。

## Notes

- 后续排查补充：除本页局部主色外，src/styles/index.scss 还存在全站 :focus-visible { outline: 2px solid var(--el-color-primary); outline-offset: 2px } 兜底规则，且 element-plus 的 .el-input__inner 未声明 outline: none，导致输入框聚焦时出现组件库边框之外的第二层蓝色描边。本页"输入聚焦变色"仅指 primary 变量驱动的边框色，不含这层 outline，二者需区分处理。
