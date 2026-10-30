---
title: "特殊作业票8类PDF导出全量验证与空值缺陷修复"
usage_scenario:
    - "再次验证或排查作业票PDF导出失败问题"
    - "需要向测试库快速造票做链路验证时"
    - "回答特殊作业导出入口与票据版式核对方法"
keywords:
    - "作业票PDF导出验证"
    - "docx模板部署"
    - "poi-tl空值保护"
    - "编制时间NPE"
    - "JDBC造票"
    - "pdfbox渲染核对"
source: "auto"
---

## Task description
- Core requirement: 完成需求8（特殊作业票 PDF 导出）的最后验证——用户给出 .tmp/poc_real 目录（5 个 work_special_*.pdf），要求补齐"缺模板"并让全部票型可导出；后续追问导出入口位置、要求覆盖剩余票型。
- Task background: 导出链路（xdocreport + poi-tl + /print + /exportPdf）此前已实现，但本机因模板未部署一直报 FileNotFoundException；测试库仅有动火/受限空间/临时用电三类票，其余 5 类无数据。

## Execution process
1. 定位模板：全盘搜索确认 8 个 docx 模板在 C:\Users\Administrator\Desktop\源洹\作业票模版\，复制部署到 D:\ruoyi\uploadPath\template\（后端 RuoYiConfig.getProfile()/template）。
2. 首次 /print 实测 500：日志显示输出目录 D:\ruoyi\uploadPath\print 不存在 → New-Item 创建后动火票导出成功（code=200，121KB PDF）；exportPdf 校验 Content-Disposition attachment + 票号文件名 + %PDF 头通过。
3. 受限空间票 500：根因 BaseWorkSpecialServiceImpl.renderWorkSpecialDocx 中 Pictures.ofUrl(step.getPersonFile()) 在未签字时传 null 抛 NPE；改放空串又触发 poi-tl "Unsupported picture: ."——最终改为 personFile 为空时**不放"确认人"键**；同时对断路说明附件/动土范围附件/盲板相关附件三处图片加 StringUtils.isNotEmpty 保护。
4. 断路/盲板 500：DateUtils.parseDateToStr 对 bzsj/workStartTime 为 null 抛 "date must not be null" → 两处"编制时间"和一处"开始时间"加三元空值保护。
5. 补测缺失 5 类：MCP hc-standard-test 不可用 → 改用 JDBC（mysql-connector-j 8.0.33，java 单文件模式）以动火票 c0de0002 为母版复制造 5 张 TESTPDF* 票（含 base_work_step/base_work_approve 复制），注意 outerId 必须恰好 32 位（首轮拼成 33 位导致"baseWorkSpecial is null"）。
6. pdfbox 3.0.7（pdfbox+pdfbox-io+fontbox+commons-logging，JAVA_HOME=D:\Tool\jdk\jdk21 的 java，.jdks 下 java.exe 不可用）渲染各 PDF 首页 JPG 核对版式；复用已有 .tmp/RenderPdf.java（Write 新建同名内容会因缓存校验失败）。
7. 清理：JDBC 删除 TESTPDF* 全部数据（5 票 + 80 step + 30 approve），库恢复原状。

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java
- d:/code/hc-standard/.tmp/seed/SeedTypes.java（临时造数工具）
- d:/code/hc-standard/.tmp/seed/CleanSeed.java（临时清理工具）

## Notes
- poc_real 里的 PDF 是模板转出的空白预览产物（可见 {{编号}} 占位符），不能当渲染输入；导出只认 docx 模板——曾据此误判"模板已存在无需部署"。
- 环境目录（template/print）缺失与代码缺陷是两类问题：接口 500 先查 sys-error.log 异常头再定位，不要直接改代码。
- 造票 ID 长度须与列定义 varchar(32) 严格一致，多一位会导致按 outerId 查询恒空且报错信息模糊（baseWorkSpecial is null）。
- 给其他票型复制动火票的 step/approve 数据会使措施文案不完全对应票型，仅用于验证模板渲染，不能作为业务正确性结论。

## Task overview
已完成：需求8 8/8 票型（动火/高处/临电/受限/吊装/动土/断路/盲板）PDF 生成与下载全部实测通过，版式经渲染图核对正常；顺带修复 2 类健壮性缺陷（poi-tl 图片占位 null/空串、日期字段 null）。PC 端入口为 /ticket/workSpecial 行内「预览|导出」，顶部「导出」为 Excel 台账，两者需在回答入口问题时区分。测试票已清理。

## Notes（2026-10-27 追加更正）
- 第 5 步「MCP hc-standard-test 不可用」只是该服务被 disabled 的事实，不等于 MCP 整体不可用：后续会话实测 tgr-gz-vue 可正常 SELECT，但它连的是**正式库 anwei1-0001**，与本地后端所连测试库 192.168.250.160 不是一套数据。因此"造票/清票走 JDBC、看线上实况走 MCP"才是常态分工，引用本条时不要把 JDBC 当成唯一通道。
- 前置条件复核有效：D:\ruoyi\uploadPath\template 下 8 个 work_special_*.docx 齐备、print 目录存在，是导出不 500 的硬前提；本轮 APP 详情页「下载」再次实测产出 /sdcard/other/DH-20261024-9901.pdf（49KB），文件名即作业编号，链路与本条结论一致。
