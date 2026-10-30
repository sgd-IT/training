---
title: "外施队人员定位MQTT上报字段与离线补报改造"
usage_scenario:
    - "改造LocationTask的MQTT上报字段取值"
    - "排查外施队人员进不了园区定位表"
    - "判断定位卡号形态与推送成败关系"
keywords:
    - "MQTT定位上报改造"
    - "position_card_no形态分布"
    - "离线首次补报"
    - "location:reported标记"
    - "外施队手工录卡号"
source: "auto"
---

## Task description
- Core requirement：结合"园区人员定位基础信息表由 MQTT 上报建立"这一现状，打通外施队（承包商企业）人员进入园区定位表并使作业票 operatorId 可匹配的链路。
- Task background：上一轮已完成定位同步只回填卡号、status=3 录入等改造；本轮用户出示定位系统截图证明外施队卡只有名称+12位mac、手机号/身份证/工号全空，并要求核对线上库中卡号形态是否真的等同手机号。

## Execution process
1. MCP 查线上 base_contractor_people（del_flag='0'，共 136 人，全部 contractor_id=1001）按 position_card_no 形态分组统计：11位手机号 95 人、12位mac 37 人、空 4 人，承包商企业名下 0 人 —— 推翻"内部人员卡号即手机号"的旧假设。
2. 追查 mac 卡人员是否成功推过作业票：JOIN operatorId=outid 与 FIND_IN_SET 两种写法均返回空，说明这批 mac 卡人员未参与过作业票，卡号形态与推送成败无因果关系。
3. LocationTask.java（L68-L130）三处改动：报文 phone 改取本地 people.getPhone()、name 改取 people.getName()（避免园区档案存成"湖北盛廷朱红波"）、离线人员改为仅在从未建档时补报一次，用 RedisCache.hasKey/setCacheObject 键 location:reported:{outid} 标记（先 Grep RedisCache 确认存在 getCacheObject/setCacheObject/hasKey）。
4. ruoyi-ui/src/views/base/contractorPeople/index.vue（L318-L329）：取消注释恢复"定位卡号"输入框，供外施队手工填 C18B9DF56FD 这类 mac。
5. 编译验证 mvn -q -pl ruoyi-quartz -am compile -DskipTests，EXIT=0。
6. 向用户说明 MQTT 链路由本系统 locationTask.getLocation（每10秒）主动 publish 到园区 broker tcp://111.4.80.39:8096 的 topic person/location，园区为订阅方，不会自行长人。
7. 指出两处风险：PLATEMENT_PUSH_ENABLED=false 只拦 HTTP 不拦 MQTT，联调会真推园区；LocationTask.java#L56-L58 的 if (position == null) { return; } 应为 continue，单人数据异常即中断整轮上报。
8. 给出"补手机号 vs 手工录卡号"两条路径对照，并强调顺序不可反：定位系统补手机号 → 本系统先建档 → 同步才回填（同步现只回填不新建）。

## Related files
- /ruoyi-quartz/src/main/java/com/ruoyi/quartz/task/LocationTask.java
- /ruoyi-ui/src/views/base/contractorPeople/index.vue

## Notes
- "卡号是不是手机号"不能作为区分内部/外施队的依据（线上 37 个内部人员也是 mac 卡），真正的断点只有一个：定位系统侧外施队记录的 phone 为空，导致按 phone 匹配的回填逻辑失效。
- 首次部署时 Redis 无 location:reported:* 标记，所有离线在册人员会被补报一次 state=1；Redis 无持久化重启后会再补报一轮，量级 136 条可接受但需知悉。
- 本轮结论仍待用户提供 7 名外施队人员名单（姓名/身份证/手机号/所属企业/卡号）后录入验证；LocationTask 的 return→continue 修复用户尚未拍板，未实施。

## Task overview
部分完成（代码改动已落地并编译通过，业务闭环待名单）：查清线上卡号形态三分布（95手机号/37mac/4空）并证伪形态与身份、与推送成败的关联；将 MQTT 上报的 phone/name 收敛为本地档案取值、离线改为首次一次性补报（Redis 打标），并恢复 PC 端定位卡号手工录入，使外施队链路"录入→sync_id=outid→按卡号匹配→上报建档→operatorId 匹配"闭环；同时暴露 MQTT 无测试开关与循环早退两个隐患，return→continue 改动留待用户确认。
