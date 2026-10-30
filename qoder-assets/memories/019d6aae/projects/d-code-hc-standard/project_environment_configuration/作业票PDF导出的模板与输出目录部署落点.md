---
title: "作业票PDF导出的模板与输出目录部署落点"
usage_scenario:
    - "排查作业票导出报模板或路径找不到错误"
    - "核对docx模板应放置的目录或怀疑放错位置"
    - "新环境部署后端验证PDF链路前置准备"
keywords:
    - "uploadPath/template目录"
    - "uploadPath/print目录"
    - "work_special docx模板"
    - "模板放错位置排查"
    - "模板放置目录核对"
    - "TicketsType printTemplate映射"
    - "中文占位符命名"
    - "线上模板"
source: "auto"
---

hc-standard 本机后端（ruoyi.profile=D:/ruoyi/uploadPath，端口 28080）的作业票 PDF 导出链路依赖两个磁盘目录，缺失即报错：① docx 模板目录 D:\ruoyi\uploadPath\template\，需放置 8 类票型模板 work_special_{dh,dl,dt,dz,gc,ld,mb,sx}.docx；文件名取自 com.ruoyi.common.enums.TicketsType 枚举的 printTemplate 字段（硬编码常量），由 TicketsType.getTemplateByType(ticketsType) 按票型取出，服务层拼 RuoYiConfig.getProfile() + "/template/" + printTemplate。这 8 个 docx 不在 git 仓库内（全盘搜索零命中），是运行期从磁盘读而非 classpath 资源，新环境部署须单独拷贝。模板源文件位于 C:\Users\Administrator\Desktop\源洹\作业票模版\（该桌面目录会在会话间被移动，部署前须重新搜索确认）；模板缺失时渲染抛 FileNotFoundException。② PDF 输出目录 D:\ruoyi\uploadPath\print\，printWorkSpecialPdf 写盘不会自动建目录，目录不存在时报"系统找不到指定的路径"。两处均用 New-Item -ItemType Directory -Force 补齐后即可，无需重启后端。注意 .tmp/poc_real 下的 work_special_*.pdf 只是模板转出的空白预览产物，不能作为渲染输入。模板内占位符全部是中文命名（如 {{编号}}、{{气体分析}}、{{措施}}、{{现场会签}}），改模板时占位符名须与服务层 finalMap.put(...) 的键严格对齐。

## Notes
- 【"不在仓库内"的结论存疑】用户后续明确表述：线上正在使用的模板，本项目仓库内应该也有一份。与前文"全盘搜索零命中、不在 git 仓库内"的记录相互矛盾——可能是检索方式受限（文档类二进制/被 .gitignore 排除/目录未被扫描到）导致漏检，也可能是后续才把模板拷入仓库。下次改动或部署模板前，应先在项目仓库内重新检索 work_special_*.docx（含 resources、template、docs 等目录及被忽略路径），以实际检索结果为准，不要直接依赖"仓库内没有模板"这一旧结论。
