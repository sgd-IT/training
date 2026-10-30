---
title: "work-log加密md文件禁用终端编码读写否则不可逆损坏"
usage_scenario:
    - "向跨项目work-log笔记追加踩坑条目时"
    - "文件读写后出现大面积乱码需恢复时"
    - "判断未知编码文件能否安全改写时"
keywords:
    - "加密存储md文件"
    - "编码往返有损"
    - "工作区缓存副本恢复"
    - "work-log笔记追加"
    - "整字节复制"
source: "auto"
---

Bug class: 跨项目笔记文件被非文本方式读写后内容整体乱码、无法逆向还原
Root cause: work-log 数据目录下的 md 文件在磁盘上是加密/受保护的二进制存储格式（文件头为非文本字节），只有 IDE 的 Read 工具能解密显示；用 PowerShell 以 GBK 或 UTF-8 解码后再编码写回，等于把密文当文本做有损转换，字节结构被破坏，且 UTF-8 与 GBK 往返不是双射，无法逐字节还原。此时严格 UTF-8 解码失败也不能证明原文件是 GBK。
Fix pattern: 从 IDE 的工作区缓存目录中定位同名文件的未损坏历史副本，核对行数与首尾内容一致后整字节复制覆盖回目标路径，再用 Read 工具验证中文正常显示；全程不做任何编码转换。
Reusable lesson: Don't 用终端命令（Add-Content / AppendAllText / WriteAllText）向 work-log 类笔记文件追加内容，因为一次编码误判就会毁掉整个文件且无版本库可回滚；应先确认文件是纯文本（Read 显示正常且首字节为可打印字符）再走 IDE 写入通道。Applies when 需要向工作区外的跨项目共用笔记（work-log 数据目录下的 md）追加条目而 IDE 写工具被限制在工作区内时；does not apply to 工作区内确认为纯文本的普通 markdown/代码文件。
