---
title: "bash工具沙箱限制工作区外写入需提升权限"
usage_scenario:
    - "修改工作区外文件被拒绝时"
    - "Windows下执行PowerShell写操作失败"
    - "判断沙箱限制与权限问题"
keywords:
    - "bash沙箱"
    - "sandbox.exe"
    - "访问被拒绝"
    - "required_permissions"
    - "工作区外写入"
    - "UnauthorizedAccessException"
source: "auto"
---

## 使用场景
在 Qoder 的 bash 工具中执行 PowerShell/命令时，若需要修改或移动工作区目录之外的文件（例如 C:\Users\<user> 下的配置目录）。

## 使用方法
bash 工具在 Windows 上通过 sandbox.exe 包裹执行并限制写权限（仅允许写工作区目录）。对工作区外路径执行 Move/WriteAllBytes/覆盖写会报"对路径的访问被拒绝"（UnauthorizedAccessException），而新建文件可能仍被允许。确认失败确由沙箱引起后，在 bash 调用中设置 required_permissions='all' 重新执行即可成功。

## 注意事项
沙箱限制与文件系统 ACL、杀毒软件拦截的症状相似，需先通过报错类型（访问被拒绝 vs 共享冲突）和路径是否位于工作区内来判断，避免误判为权限或杀软问题而绕远路。读取操作不受该限制影响。
