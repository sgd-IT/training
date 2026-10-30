---
title: "定位系统人员卡两种形态与MQTT上报取数口径"
usage_scenario:
    - "外施队建卡与定位上报联调"
    - "排查人员进不了园区定位表"
    - "修改LocationTask上报字段"
keywords:
    - "定位卡两种形态"
    - "外施队卡无手机号"
    - "person/location上报字段"
    - "outid与operatorId同源"
source: "auto"
---

定位系统（只有湖北源洹一棵树，承包商是其中一个部门分组）里人员卡有两种形态：① 内部员工卡——"设备卡号"就是手机号（如 18571056506），身份证/手机号/岗位齐全；② 外施队卡——只有名称（"湖北盛廷朱红波"这种"单位+姓名"）和设备卡号（mac 十六进制串，位数不固定，实测有 B70E812012D3 这类 12 位、也有 C18B9DF56FD 这类 11 位），手机号/身份证/工号/岗位全空。

因此 syncContractorPeople 按 phone 回填 position_card_no 对外施队完全无效，外施队卡号必须在 PC 端录入人员时手工填写（填的时候按定位系统显示原样填，不要补齐位数或转大小写）。MQTT（topic person/location）上报的 phone 与 name 也不能取定位系统返回值：phone 为空会被 LocationTask 校验直接跳过、name 会变成"单位+姓名"，两者都必须取本地 base_contractor_people 的 phone/name。personId 取 outid，线上 base_contractor_people 的 sync_id 恒等于 outid，与作业票 operatorId 同源（线上 UUID 形态推送成功 72 条、雪花形态 0 条）。

注意：卡号形态不能用来判断内/外身份——线上 1001 名下 136 名内部人员中也有 37 人卡号是 mac 形态（2026-10-23 实测分布：手机号 95 / mac 37 / 空 4）。LocationTask 的匹配是 position.mac 与 position_card_no 纯字符串相等，两种形态同路，无需任何形态判断或转换。
