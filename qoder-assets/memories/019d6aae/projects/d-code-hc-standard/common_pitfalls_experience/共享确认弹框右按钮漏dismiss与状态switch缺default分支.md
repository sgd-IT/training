---
title: "共享确认弹框右按钮漏dismiss与状态switch缺default分支"
usage_scenario:
    - "排查APP确认框点击后不消失遮挡页面"
    - "新增业务状态后核对列表标签是否显示错乱"
    - "修改共享弹框封装或RecyclerView适配器时预防缺陷"
keywords:
    - "showMessageDialog2漏dismiss"
    - "setCancelable(false)卡框"
    - "JobSlipListAdapter缺case"
    - "RecyclerView复用串状态"
    - "新增状态值排查映射点"
source: "auto"
---

Bug class: 移动端通用组件的两类静默缺陷——①确认弹框点右侧按钮后不关闭并永久遮挡页面；②列表状态标签在新增状态值后显示成上一次绑定的旧状态。

Root cause: ① BaseActivity.showMessageDialog2 只给左按钮的监听里显式调 dismiss()，右按钮把业务回调原样透传给 MyDialogMessage.setOnRightOnclickListener，而 MyDialogMessage 内部 btn_right 点击仅执行 onRightClick() 不做关闭，且构造里设了 setCancelable(false)，于是返回键与点击空白都关不掉，弹框卡在页面上（"继续作业""作废分析记录"两处同时中招）。② JobSlipListAdapter 给 tvType 赋值的 switch 只覆盖 0/1/2/3/4，没有 case "5" 也没有 default，RecyclerView 复用 ViewHolder 时 TextView 保留上一行绑定过的文本，中断票就沿用别行的"进行中"。

Fix pattern: ① 在封装层为右按钮统一包装"先 dismiss 再执行原回调"，而不是逐个调用点各自补关闭（调用点里手动 dismiss 属幂等冗余，可保留）；② 状态映射 switch 必须补齐新分支并加 default 显式清空/置默认文案，杜绝依赖 View 初始文本。

Reusable lesson: Don't assume a dialog closes itself after its confirm callback runs, because a shared dialog helper that only binds dismiss to one button plus setCancelable(false) leaves the modal stuck permanently and blocks all further UI interaction; instead centralize the dismiss in the wrapper so every caller inherits it. Applies when adding or reusing a shared message/confirm dialog helper and when introducing any new status value consumed by list adapters or visibility switches in this Android module (other1/app); does not apply to system AlertDialog built per-screen with default cancelable behavior, nor to non-recycled one-shot views.
