---
title: "首页公告走自定义取数仅认notice_type=2且LIMIT1无排序"
usage_scenario:
    - "修改或发布PC首页公告内容"
    - "排查新增公告后首页不变化"
    - "改造首页公告取数逻辑"
keywords:
    - "sys_notice"
    - "getHomeNotice"
    - "ApiHomeMapper"
    - "notice_type=2"
    - "首页公告"
source: "auto"
---

hc-standard 的 PC 首页公告区不走 RuoYi 标准 sys_notice 列表，而是自定义链路：ruoyi-ui/src/views/index.vue 调 getHomeNotice() → ruoyi-admin 的 ApiHomeController → apiHomeService.selectHomeNotice() → ruoyi-system/src/main/resources/mapper/base/ApiHomeMapper.xml，SQL 为 `SELECT cast(notice_content AS CHAR) AS noticeContent, update_time AS updateTime FROM sys_notice WHERE notice_type = 2 AND status = 0 LIMIT 1`。

由此产生三条约束（实测确认）：
1. 只有 notice_type=2（字典"公告"）的记录才可能出现在首页；在"添加公告"表单里选"通知"（notice_type=1）保存成功但首页永远不变。
2. 该 SQL 无 ORDER BY 且 LIMIT 1，因此新增公告不保证顶掉当前显示的那条（取哪条由存储顺序决定），也不存在"覆盖"——旧记录只是不再被展示，数据本身不丢。
3. 要让首页内容确定更新，正确做法是直接编辑现有那条 type=2/status=0 的公告（现网仅 1 条：notice_id=12「首页公告」），或新增 type=2 公告后把旧的一条 status 置 1（关闭）。

status 取值沿用 sys_normal_disable 字典：0=正常、1=关闭。返回体还带 update_time，前端 index.vue 用 parseTime(this.updateTime) 展示更新时间，故编辑旧记录能同时刷新内容与时间戳。
