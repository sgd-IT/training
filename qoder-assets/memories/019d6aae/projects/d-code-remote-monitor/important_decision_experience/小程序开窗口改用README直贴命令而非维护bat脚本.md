---
title: "小程序开窗口改用README直贴命令而非维护bat脚本"
usage_scenario:
    - "为项目记录外部工具调用命令的形式选择"
    - "决定是否保留批处理包装脚本"
    - "更新mobile仓库README常用命令相关内容"
keywords:
    - "README 常用命令"
    - "bat 包装脚本"
    - "cli.bat open"
    - "方案取舍"
source: "auto"
---

Conclusion: anwei-fire-remote-monitor-mobile 打开微信开发者工具的调用方式改为「把完整 cli 命令直接写进 README 的『常用命令（复制到 PowerShell 执行）』小节，用时复制」，不再维护 open-wxdev.bat 包装脚本；README 中所有原先指向 bat 的地方（发行上传流程步骤、常见问题 800059 处置）同步改为指向该小节。
Rationale (trade-off): 文档承载命令零维护成本、可直接审阅与复制；bat 需要额外处理 GBK 编码（中文路径在 UTF-8 下乱码）、写入后还要读回校验，且文件本身会成为需持续照看的死资产。
Rejected alternatives: 保留并继续维护一键 bat（双击即用，对不熟悉命令行的使用者更省事）——被用户明确否决（"整个命令都要贴进去，不用bat了"）。
Applicable / expiry conditions: 在该仓库内以 README 命令为唯一权威入口，项目根目录不应再出现 .bat 包装文件（本轮已确认无任何 .bat 残留）；若后续工具安装路径或产物目录结构变化，需同步更新 README 该小节；若团队出现大量不便复制命令的使用者，可重新评估是否恢复脚本形式。
