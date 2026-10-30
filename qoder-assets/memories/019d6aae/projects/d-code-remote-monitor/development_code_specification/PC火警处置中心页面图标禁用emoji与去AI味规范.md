---
title: "PC火警处置中心页面图标禁用emoji与去AI味规范"
usage_scenario:
    - "修改alarm-order页面UI或图标时"
    - "评审PC火警处置中心视觉一致性时"
    - "为火警处置中心新增按钮或面板配色时"
keywords:
    - "alarm-order图标规范"
    - "禁用emoji"
    - "去AI味"
    - "快捷控制图标统一"
    - "火警处置中心配色"
source: "user"
---

PC 火警处置中心（alarm-order 页面）的图标与配色规范（2026-10 用户明确要求"图标禁止用 emoji 且去 AI 味""不要用彩虹配色"后实施）：
- 图标一律用 Element Plus SVG 图标组件，禁止字面 emoji 与 emoji 观感元素（彩色发光/脉冲圆点徽标、多按钮不同色的彩色图标网格）。
- 禁止彩虹配色：全页收敛为「主题青绿 #199b96 + 中性灰 + 仅火警/紧急红色」三色体系；禁止脉冲/闪烁动画、彩虹渐变、逐条配色的进度条与死代码样式。
- Element 默认主色（蓝 #409eff）在本页局部覆盖为 #199b96，含 7 个 light/dark 变体；弹窗/抽屉被 teleport 到 body，需 class="alarm-center-dialog" 挂同名全局样式块补同套变量；ElMessageBox 用 customClass 同名收敛。
- 语义映射：风险等级仅 A 级红色、B/C 中性；状态标签待处理红、处理中/已确认走主题色、闭环（已处理/误报/关闭）中性；快捷控制图标统一灰青、悬停点亮主题色，仅"报警"保留红色；消音仅 active 态点亮主题色。
- 主题色系与全局（其他页面）的 Element primary 均不改动，仅本页作用域收敛。
