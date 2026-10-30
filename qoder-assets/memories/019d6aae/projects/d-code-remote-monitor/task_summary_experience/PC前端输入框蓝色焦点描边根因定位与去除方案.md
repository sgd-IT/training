---
title: "PC前端输入框蓝色焦点描边根因定位与去除方案"
usage_scenario:
    - "排查输入框聚焦时出现的额外描边或光环"
    - "诊断全局样式兜底规则污染组件库控件"
    - "区分border与outline造成的视觉差异"
keywords:
    - "focus-visible全局兜底"
    - "outline-offset描边间隙"
    - "el-input__inner未清outline"
    - "输入框双层蓝框"
    - "浏览器拼写检查点线"
source: "auto"
---

## Task description
- Core requirement: 用户就截图中输入框外围出现的蓝色圆角粗框提问"为什么会出现"，需要定位该样式的真实来源并给出去除方案。
- Task background: PC 前端仓库 anwei-fire-remote-monitor-vue 使用 Element Plus；此前一轮已对 alarm-order 页面做过主色局部收敛，页面存在青绿主题色与 Element 默认蓝 #409eff 混用背景。截图特征为蓝框与输入框之间留有一圈白隙、文字下方带灰色点线。

## Execution process
1. 关键词排查：在 vue 仓库全量 grep spellcheck/autocorrect 零命中；grep outline:none 与 :focus-visible 定位到唯一命中文件 src/styles/index.scss（其余命中为 bpmn/流程设计器主题文件，与本问题无关）。
2. 根因确认：读取 index.scss 第 100-105 行，确认存在全站兜底规则 `:focus-visible { outline: 2px solid var(--el-color-primary); outline-offset: 2px; }`，而应用未在 src 下覆盖 --el-color-primary，取值为 Element 默认蓝 #409eff；框与输入框之间的白隙即 outline-offset 造成，border 不会留缝。
3. 叠加因素验证：grep node_modules/element-plus/theme-chalk/el-input.css 中 outline 关键字返回 0 匹配，证明 .el-input__inner 从未声明 outline:none，原生 input 的焦点环会透出并叠在 Element 自身 is-focus 边框之外，形成"双层蓝框"。
4. 机制判定：文本输入元素（input/textarea）的 :focus-visible 在 Chrome 中鼠标点击同样命中，因此这条"只服务键盘可达性"的兜底实际覆盖了全部输入框聚焦场景。
5. 移动端排除：grep 移动端仓库 anwei-fire-remote-monitor-mobile 的 :focus-visible / outline:2px 均为 0 命中，确认若现象出现在手机端则属宿主浏览器或小程序原生焦点样式，不在本仓代码内。
6. 方案输出（未执行）：推荐把文本元素从全局兜底排除 —— `:focus-visible:not(input):not(textarea):not(.el-input__inner)`，一处改动全站生效；单组件场景可退化为作用域内 `.el-input__inner:focus-visible { outline: none; }`。已向用户征询是否直接改全局规则，等待答复。

## Related files
- src/styles/index.scss

## Notes
- 文字下方的灰色点线是浏览器拼写检查（spellcheck）产物，与蓝框无关，两类视觉噪声需分开解释，避免合并归因。
- 判断"是 border 还是 outline"的可靠依据是描边与控件之间是否存在间隙：outline-offset 会留缝，border 永远紧贴内容盒。
- 排查此类全局样式污染时，除项目 src 目录外必须同时检查组件库自身的 CSS 是否已重置 outline，否则会把"组件库未清理"误判成"项目主动加框"。
- 本轮为纯诊断，未做任何文件写入，结论中的修复方案尚未落地，后续引用需先复核 index.scss 当前内容。

## Task overview
已完成根因定位：蓝框来自 src/styles/index.scss 的全站 :focus-visible outline 兜底规则（主色取 Element 默认蓝），叠加 element-plus 的 el-input.css 完全缺失 outline 声明导致原生焦点环透出，形成双层蓝框；文字点线另属浏览器拼写检查。给出两种去除方案并确认移动端仓库无同类规则。状态：诊断完成、修复待用户确认后实施。

## Notes
- 后续轮次用户以"消失吧"确认后修复已落地：src/styles/index.scss 的兜底规则改为 `:focus-visible:not(input):not(textarea)`，实际只需排除 input/textarea 两个原生标签即可，无需追加 `:not(.el-input__inner)`（el-select、el-date-picker、el-input-number 内部都是原生 input，已被一并排除）。
- 同文件 `@media (forced-colors: active)` 内的 `:focus-visible { outline: 2px solid }` 有意保留不排除——高对比模式下系统色描边是唯一可见焦点提示，普通模式无描边、高对比有描边是正确取舍。
- 验证方式：`node node_modules\sass\sass.js --no-source-map --load-path=node_modules src\styles\index.scss _temp_scss_check.css` 编译通过后 Select-String 反查产物确认选择器为 `:focus-visible:not(input):not(textarea)`，随后删除临时产物文件。
- Element 自身 `.el-input__wrapper.is-focus` 的贴边 1px 蓝框仍保留，属正常聚焦反馈，非多余描边；状态更新为已完成。
