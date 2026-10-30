---
title: "火警弹窗链配色 token 收敛口径与 Element 深色适配配方"
usage_scenario:
    - "调整火警弹窗或视频组件配色"
    - "深色 HUD 页面新增按钮链接表单样式"
    - "处理 Element 默认样式与深色主题冲突"
keywords:
    - "主题色板"
    - "深色弹窗描边"
    - "Element按钮覆盖"
    - "近亲蓝收敛"
    - "按主题色实现"
source: "auto"
---

火警两屏（monitoring-center / duty-screen）深色 HUD 主题 token（定义于 monitoring-center/index.vue 的 style 头部变量）：主色 #4fc3f7 系 rgba(79,195,247,·)（0.9 主 / 0.5 描边 / 0.15 淡描边）、次级文字 #8b9dc5、弱化 #4e6585、底色 #0d1f3c（$bg2）与 #0f2747（$bg3）、常规描边 #1e3a5f、危险 #ff4d4f。弹窗规范对齐 .dark-dialog：描边 1px solid rgba(79,195,247,0.15) + 8px 圆角。收敛口径（2026-10-16 已应用于 AlarmVideoDialog / AlarmPopupDialog / AlarmAssistPanel / VideoPlayer）：链接与按钮主色 #4fc3f7，hover 文字统一 #fff 并加深边框/填充；输入值文字 #fff、占位 #4e6585、辅助图标用 #8b9dc5 或主色；非 token 近亲蓝（#79baff / #a8d4ff / #cfe0f7 / #8dc5f1 / rgba(121,186,255,·)）不再使用。Element Plus 默认蓝（如播放器重试按钮 #409eff）用元素级 --el-button-* CSS 变量覆盖为淡填充配方：bg rgba(79,195,247,0.12) / border 0.5 / text #4fc3f7 / hover bg 0.22 + border 0.8 + text #fff。

## Notes
- 适用范围补充：用户要求 UI"按主题色来"时，一律以本页/本端既有主题 token 为准（PC 火警两屏取 #4fc3f7 系深色 HUD token；alarm-order 页局部 primary 为 #199b96；移动端主色 #1e39b1），禁止引入 token 之外的新品牌色。
