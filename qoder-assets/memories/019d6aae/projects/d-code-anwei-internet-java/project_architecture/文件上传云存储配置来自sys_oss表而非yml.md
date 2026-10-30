---
title: "文件上传云存储配置来自sys_oss表而非yml"
usage_scenario:
    - "排查漏铝音频上传失败或记录完全不入库"
    - "新环境部署校验文件上传能力"
    - "定位oss相关配置的存放位置"
keywords:
    - "OSSFactory"
    - "sys_oss表"
    - "uploadSuffix"
    - "音频后落库"
    - "无记录则不入库"
source: "auto"
---

anwei-internet-java 中所有文件上传（含漏铝音频、热成像图片）统一经 OSSFactory.build().uploadSuffix(bytes, suffix) 走云存储抽象，其具体实现与端点配置来自数据库 sys_oss 表（由 SpringContextUtils.getBean 取 ConfigService 读取），application.yml 里不存在任何 oss 配置项。由此产生一条隐式约束：现行"音频上传后才落库"链路下，若 sys_oss 无有效记录，uploadSuffix 抛异常 → setEventVideo 不执行 → 报警记录根本不会写入 mon_aluminum_leakage_alarm_record，列表页连"暂无音频"的空行都不出现。因此排查"某环境完全没有报警数据"时，在检查 monkey.leak 配置之外必须同时 SELECT * FROM sys_oss 确认该表有配置，不能只在 yml 里找 oss 键。
