---
title: "报警弹窗抽为公共组件 AlarmPopupDialog 两屏共用"
usage_scenario:
    - "修改报警弹窗 UI 或逻辑"
    - "两屏弹窗联调与排障"
    - "新增弹窗能力或入口"
keywords:
    - "弹窗组件化"
    - "enqueue close 契约"
    - "dimOverlay"
    - "两屏共用"
    - "editingAlarm"
source: "user"
---

报警弹窗已抽为公共组件 `src/views/eiot/firewatch/components/AlarmPopupDialog.vue`，两屏（duty-screen、monitoring-center）改为直接注入 `<AlarmPopupDialog>` 调用，弹窗类修改只需改组件一处，不再双份维护。

## 组件契约
- props：laterText/primaryText（左/右按钮文案，duty=稍后处置/去处置，monitoring=标记误报/确认火警）；dimOverlay（duty 传 false 保持透明遮罩，monitoring 默认 0.4 变暗）。
- emits：later/primary/overlay（均带当前 alarm 对象），业务动作留各屏处理。
- ref 暴露：enqueue(alarm)（轮询与 WS 一级告警统一入队，重复提醒周期判定在组件内）；close()（动作完成后关闭并推进队列）；monitoring 保留 closePopup 薄包装。

## 屏上保留
WS 接收与通知匣（二三级）分流、业务动作（duty：deferAlertRecord+close+跳转；monitoring：editingAlarm 留存后打开确认火警/误报表单，提交成功后 close）。overlay 点击差异：duty=onHandleLater、monitoring=closePopup。

## 验证
ESLint 0 错误；vue-tsc 无新增（monitoring 仅 3 条既有 TS6133：getRefreshToken/cachedDeviceCategory/ds）；三个 SFC 的 scss 编译通过。
