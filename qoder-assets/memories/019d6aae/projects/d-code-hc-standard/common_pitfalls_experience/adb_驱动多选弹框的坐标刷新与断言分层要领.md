---
title: "adb 驱动多选弹框的坐标刷新与断言分层要领"
usage_scenario:
    - "adb驱动监护人弹框跨企业凑内外各一人时的操作要领"
    - "决定多选校验分支用UI还是接口取证"
keywords:
    - "adb盲点坐标失效判定"
    - "uiautomator dump取bounds"
    - "Toast须立即screencap"
    - "拦截断言优先token直调接口"
    - "交互缺陷归被测程序"
source: "auto"
---

Bug class：用 adb 在 hc-standard other1 的 DialogWorkPeopleMultiple 选人弹框内跨企业凑齐"内部1人+外施队1人"时点确定被"监护人至少 2 名"拦下，易误判为"列表自刷新导致盲点不可行"。（维度切换丢勾选的代码级根因与全量遍历修复方式见「多选弹窗按维度筛选后勾选与校验须遍历全量数据」，本条只讲 adb 取证要领。）

Fix pattern：
① 弹框内全程停留在默认"全部"维度上下滚动分别勾内部人与外施队人，完全不点维度项；次选在"承包商"勾完外施队人后只切"本公司"（两者均走本地 applyData，勾选保留）。
② 每步点击前重新 uiautomator dump 取当前 bounds 中心坐标，不做连续盲点。
③ 拦截类断言（人数不足/纯内部/纯外施队三分支）优先用 token 直调后端 Controller 穷举；UI 只取证"能否跨企业列到人 + 单条 Toast 文案 + 最终栏位回填"，Toast 不进 logcat，须在点击命令内立即 screencap 再 pull。

Reusable lesson：判断"adb 点不稳"要先区分是坐标失效还是应用侧状态被重建清空——后者属被测程序的交互缺陷，应作为缺陷上报而不是归咎于自动化手段。适用于含"本地过滤 vs 重新请求"两套分支的筛选型弹框；不适用于纯静态布局的单次点击。
