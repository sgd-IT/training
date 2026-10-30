---
title: "告警列表details字段为前端必需不可裁剪"
usage_scenario:
    - "评估分页查询能否裁掉大字段"
    - "改动告警列表接口返回字段前"
    - "排查列表页设备名位置为空"
keywords:
    - "details字段依赖"
    - "alarm-record列表页"
    - "TEXT列瘦身否决"
    - "设备名位置解析"
    - "前端字段消费点"
source: "auto"
---

结构事实（本会话 Grep 前端核实）：eiot_alert_record.details（TEXT）在 PC 告警列表链路是**被消费的必需字段**，不能为缓解排序落盘/行宽而从分页 SELECT 或列表 VO 中剥离。

具体依赖点：anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/alarm-record/index.vue 中 `const details = item.details || ''` 用于**从 details 文本里解析设备名称和位置**，同文件还把 details 传给编辑弹窗；firewatch/monitoring-center/index.vue、remote-console/components/AlarmLogPanel.vue（:title="item.details"）、AlarmPopupDialog.vue 同样读取 details。剥离后列表页设备名与位置直接全空。

推论：涉及"大字段是否可从查询列表裁掉"的判断，必须先在 Vue 侧按字段名 Grep 消费点，不能只看后端 VO 是否有该字段；TEXT 列带来的 filesort 落盘风险只能通过让执行计划不退化来规避，不能靠字段瘦身。
