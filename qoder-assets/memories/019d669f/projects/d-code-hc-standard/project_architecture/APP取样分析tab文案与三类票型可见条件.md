---
title: "APP取样分析tab文案与三类票型可见条件"
usage_scenario:
    - "用户反馈APP看不到气体录入入口"
    - "改造或新增作业票详情页顶部tab"
    - "按文案定位气体分析相关界面"
keywords:
    - "ActivityHotWork"
    - "tv2 visibility gone"
    - "sourceType 1 2 6"
    - "取样分析文案"
    - "FragmentJobSlipGas"
    - "setRight"
source: "auto"
---

APP 端（other1）特殊作业票详情页 ActivityHotWork 顶部标签栏由 tv1~tv5 组成，其中承载气体分析的第二个 tab 在布局 activity_hot_work.xml 中的文案是"取样分析"（不是"气体分析"），且 android:visibility 默认设为 gone。它是否出现完全由 ActivityHotWork.java 的 setRight() 决定：仅当 sourceType 等于 "1"（动火）、"2"（受限空间）、"6"（临时用电）三者之一时才 setVisibility(VISIBLE)，盲板(3)/高处(4)/吊装(5)/动土(7)/断路(8) 类型的票永远不显示该 tab。对应 Fragment 为 FragmentJobSlipGas。

由此产生两条排查约束：一是按业务术语"气体分析"在 APP 界面或布局文件里检索会找不到入口，须用"取样分析"或 tv2 / FragmentJobSlipGas 定位；二是用户反馈"看不到气体录入入口"时，应先确认所打开作业票的 ticketsType 是否属于 1/2/6，而不是先怀疑权限或数据。另外"取样分析"这个名称在 PC 端旧体系（base_ticket_analysis + ruoyi-ui 的 base/ticket/indexShow.vue）中也被使用，两侧表与列命名完全不同，靠页面文案判断数据归属会串台。
