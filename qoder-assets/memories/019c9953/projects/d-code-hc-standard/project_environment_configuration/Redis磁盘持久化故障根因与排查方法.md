---
title: "Redis磁盘持久化故障根因与排查方法"
usage_scenario:
    - "诊断Redis写操作失败时定位磁盘问题"
    - "部署或运维Redis服务时预设磁盘健康检查项"
    - "处理多系统连锁故障（MySQL慢SQL、连接池异常、Redis禁写）时统一归因"
keywords:
    - "Redis RDB"
    - "磁盘满"
    - "I/O异常"
    - "df -h"
    - "iostat"
---

Redis 配置了 RDB 持久化，但因服务器磁盘空间满或磁盘 I/O 异常导致写入失败，触发 Redis 禁用所有写命令的保护机制；需通过 `df -h` 和 `iostat -x 1` 检查磁盘使用率与 I/O 状态，并清理 dump.rdb、appendonly.aof、binlog、应用日志等占用空间。
