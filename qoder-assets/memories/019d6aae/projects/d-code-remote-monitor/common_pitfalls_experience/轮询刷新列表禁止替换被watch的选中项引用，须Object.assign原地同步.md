---
title: "轮询刷新列表禁止替换被watch的选中项引用，须Object.assign原地同步"
usage_scenario:
    - "实现列表轮询刷新且页面有选中行联动副作用"
    - "排查视频流或请求被周期性重复触发"
    - "基于选中行状态做按钮显隐时同步最新数据"
keywords:
    - "轮询刷新"
    - "watch引用比对"
    - "Object.assign原地同步"
    - "重复拉流"
    - "选中项状态过期"
source: "auto"
---

Bug class: 轮询刷新列表时用新对象替换"选中项"引用，隐式触发 watch(选中项) 造成副作用重放（本项目实例：每次约 30 秒的 refreshAlarms 轮询都重拉一次视频流）。

Root cause: alarm-order/index.vue 中 selectedAlarm 是 ref 包对象，watch(selectedAlarm)（默认浅层、按引用比对）驱动 loadCamerasForAlarm→watch(selectedCamera)→loadCameraVideo 取流链路；refreshAlarms 里 alarmList.value = res.list.map(...) 产出的是全新对象集合，若再把选中项指向新列表里的同 id 对象（selectedAlarm.value = fresh），引用变化即命中 watch，每轮轮询都重启播放。反向问题同样存在：不更新选中项则其 status 停留在旧值（如确认火警后仍为 pending），依赖它做显隐的门禁判断全部失效。

Fix pattern: refreshAlarms 赋值列表后按 id 找回当前页对应项，用 Object.assign(selectedAlarm.value, fresh) 原地改字段——ref 引用不变故 watch 不触发，而 reactive 深层属性更新仍让模板与门禁判定拿到最新状态；选中项不在当前列表时保持旧对象不动。

Reusable lesson: Don't 把轮询刷新出的新列表对象直接赋给被 watch 的选中项引用，因为每次轮询都会重放 watch 副作用（重拉流/重复请求）；也 Don't 干脆不同步选中项，因为状态门禁会拿旧值误判。instead 用 Object.assign 原地同步字段并加注释标明"必须原地改、换引用会触发 watch 重拉流"。适用于本仓库 firewatch 下"列表轮询 + watch(选中行) 驱动副作用 + 按钮按行状态显隐"三者共存的页面（处置中心即此类）；不适用于选中项本就只读、或 watch 回调本身幂等且代价低的场景。
