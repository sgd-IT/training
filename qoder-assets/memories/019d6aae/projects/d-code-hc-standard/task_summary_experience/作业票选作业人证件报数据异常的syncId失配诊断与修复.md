---
title: "作业票选作业人证件报数据异常的syncId失配诊断与修复"
usage_scenario:
    - "排查移动端作业票选人/证件报数据异常时"
    - "定位接口字段恒为null的MyBatis映射问题时"
    - "线上换程序后前后端契约失配复盘时"
keywords:
    - "syncId缺失500"
    - "MyBatis列别名失配"
    - "作业人选证书数据异常"
    - "getWorkPeopleCertAll"
    - "ruoyi-system热替换"
source: "auto"
---

## Task description
- Core requirement: 排查 sys-error.log 中"创建作业票无法选择作业人、添加作业人员证件信息报数据异常"的根因并修复
- Task background: 移动端(临时用电等8类作业票)选作业人后弹"数据异常"；日志显示 /api/contractor/getWorkPeopleCertAll 反复报 MissingServletRequestParameterException: Required request parameter 'syncId' is not present

## Execution process
1. 日志取证：定位到 getWorkPeopleCertAll 缺 syncId 参数报错(08:42~09:06多次)，其余为 sys_job_log 慢SQL噪声
2. 客户端链路梳理：Fragment*/searchPeople 用 ContractorPeopleBean.getSyncId() 填入 MultiSelectBean.id，作为 @Query("syncId") 传给后端；Retrofit 对 null query 会静默丢参→后端500；selectZs 仅在接口 success 后才 show() 证书弹窗，故失败即表现为"选不上人/加不了证件"
3. "数据异常"文案溯源：BaseActivity.java 对服务端非200且非中文消息统一兜底为"数据异常"
4. 部署服务器只读复现(管理账号登录取token)：getWorkPeopleList 返回130人但 syncId 全为 null；而同 resultMap 的 getPeopleInfo(SQL用 a.* 原列名 sync_id)能正常返回 syncId → 锁定为列别名问题
5. DB核对：base_contractor_people 130人 sync_id 全部有值，排除数据缺失
6. git取证时间线：mapper 仅3次提交，别名 sync_id as syncId 自首次提交(2026-03-13)即存在；显式映射 <result property="syncId" column="sync_id"/> 于 2026-09-09(3b2a948)新增；生产数据9-28上午仍有成功作业票，用户确认此间"更换了线上程序"，新包首次带上该行映射引爆故障
7. 修复：BaseContractorPeopleMapper.xml 去掉 selectContractorPeopleList 与 selectTicketContractorPeopleAll 两处 sync_id 的 as syncId 别名，使列标签回归 sync_id 与显式映射一致
8. 用 JDK17(ms-17.0.19) mvn -pl ruoyi-system -am package 重建，校验新 jar 内 SQL 已无别名，复制为工作区根 ruoyi-system-new.jar 供热替换

## Related files
- /ruoyi-system/src/main/resources/mapper/base/BaseContractorPeopleMapper.xml
- /ruoyi-admin/src/main/java/com/ruoyi/web/controller/base/ApiContractorController.java
- /other1/app/src/main/java/com/sckj/other1/fragment/FragmentHotWork.java
- /other1/app/src/main/java/com/sckj/other1/base/BaseActivity.java

## Notes
- 项目未开启驼峰转换(mapUnderscoreToCamelCase被注释)，凡喂给带 sync_id 显式映射 resultMap 的 SQL 禁止对该列起别名，否则属性静默为null(不报错)
- 该缺陷与移动端版本无关，任何App版本都会中招；不能用"更新App"绕过，必须修服务端并重新部署 ruoyi-system jar
- 复现生产接口需先登录取Bearer token(/captchaImage数学验证码+/login)，未带token访问/api/**返回401

## Task overview
已完成并产出待部署产物：根因为 MyBatis 列别名(sync_id as syncId)与新加的显式映射(column=sync_id)失配导致 syncId 恒为null，进而客户端查证书丢参触发500、UI统一兜底"数据异常"。修复方案(去别名)已写入源码并重建 ruoyi-system-new.jar，前端/移动端无需改动，仅需替换线上 jar 并重启后端。
