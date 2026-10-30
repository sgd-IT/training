---
title: "隐患主表Service内平台JSON返回须全限定名且取数走mapper避免NPE"
usage_scenario:
    - "在隐患主表Service里新增园区平台调用代码"
    - "处理deleteCheck/saveCheck返回值类型冲突"
    - "为删除或推送做前置取数判空时选mapper还是service"
keywords:
    - "fastjson2与hutool冲突"
    - "全限定名JSONObject"
    - "selectBaseHiddenTroubleById双同名"
    - "mapper取数避NPE"
    - "deleteCheck返回类型"
source: "auto"
---

在 BaseHiddenTroubleServiceImpl.java 里新增调用园区平台的代码有两条必须遵守的结构约束：

1. 该类 import 的是 com.alibaba.fastjson2.JSONObject，而 PlatementRiskCheckUnitApi 各方法（saveCheck / deleteCheck 等）返回的是 cn.hutool.json.JSONObject。局部变量必须写成全限定名 `cn.hutool.json.JSONObject json = api.deleteCheck(...)`（既有 uploadWithState 即此写法），直接写 JSONObject 会绑定到 fastjson2 类型而编译失败或取不到值。

2. 类内存在两个同名 selectBaseHiddenTroubleById：service 层版本（约 L107）会在取到的对象上继续 setStatusName 等加工，对不存在/字段缺失的记录可能抛 NPE；mapper 层版本 baseHiddenTroubleMapper.selectBaseHiddenTroubleById 是纯查询。凡是为推送/删除做前置判空取数的场景一律走 mapper 版本，不要用 this.selectBaseHiddenTroubleById。

补充：该表的 sync_status 判定只认 "1".equals(...)，NULL/'0'/'2' 都视为"平台无此记录"，因此按此口径写的条件化推送天然跳过脏历史行。
