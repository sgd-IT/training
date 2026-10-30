---
title: "order_status 为遗留死列、handle_status 是唯一处置状态权威源"
usage_scenario:
    - "评估或实施告警处置状态字段合并去重"
    - "排查统计数与明细数不一致的分组SQL"
    - "新增处置动作时确定状态落库写法"
keywords:
    - "handle_status唯一权威"
    - "order_status遗留死列"
    - "resolveHandleStatusByAction映射"
    - "关闭工单无独立态"
    - "AlertOrderStatusResolver计算值"
    - "GROUP BY别名撞真实列"
source: "auto"
---

eiot_alert_record 表同时存在两个处置状态字段，但只有 handle_status 是活的（经生产库数据核对 + 全仓代码引用检索验证）：

- handle_status（tinyint）：全表 17250 条均有值、零 NULL。取值 0=待处置 / 1=稍后处置 / 2=处置中 / 3=已处置。写入点完整：AlertServiceImpl 的 confirm/defer/handle 三个动作直接写该列；FireAlarmOrderServiceImpl 处置动作通过 resolveHandleStatusByAction(action) 换算后写该列并追加 details 备注。
- order_status（varchar(32)，注释含 pending/handling/handled/closed/confirmed/real_fire/false_alarm 七值）：线上仅 286 条非空，且全部集中在 2026-04-22 ~ 04-29 这一周，此后从未被写入过；其余约 16964 条均为 NULL。其中 266 条 pending 与 handle_status=0 语义一致，另 20 条（18 confirmed + 1 closed + 1 handled）与 handle_status=0 相互冲突，属早期版本遗留脏数据。
- AlertRecordDO.orderStatus 与 API DTO AlertRecord.orderStatus 在全仓均无任何 getter/setter 引用，是纯死字段；所有 vo.setOrderStatus(...) 赋的都是 AlertOrderStatusResolver 由 handle_status/read_flg/details 计算出的展示值，不来自该 DB 列。因此前端消费的 orderStatus 是接口计算契约，删 DB 列不影响它。
- 反直觉点：resolveHandleStatusByAction 把「关闭工单」也映射成 3（已处置），handle_status 无法区分"已处置"与"已关闭"，closed 语义目前只能靠 details 里的中文串"关闭工单"判断；而线上 details 匹配"关闭工单"的记录为 0 条（"手动处理"359、"标记误报"43、"确认火警"34、"确认真实火警"0）。若业务要真正区分已关闭，需给 handle_status 增补第 5 态而非继续依赖字符串匹配。
- 结构陷阱：正因为表里有真实的 order_status 列，任何用 CASE 表达式派生虚拟分组维度（如按解析后的状态分组统计）时，SELECT 别名绝不能写作 order_status，否则 MySQL 的 GROUP BY 会优先绑定真实列，统计只覆盖该列非空的行。项目现用别名 status_group 规避，读取侧同步用 Map.get("status_group")。
