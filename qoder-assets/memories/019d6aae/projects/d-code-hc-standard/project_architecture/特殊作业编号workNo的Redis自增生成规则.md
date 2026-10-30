---
title: "特殊作业编号workNo的Redis自增生成规则"
usage_scenario:
    - "核查作业票编号来源与前缀含义"
    - "修改特殊作业编号生成逻辑时复用现有枚举前缀"
    - "评估Redis重置导致的编号重复风险"
keywords:
    - "workNo生成"
    - "TicketsType前缀"
    - "Redis原子自增"
    - "saveWorkSpecial"
    - "编号规则"
source: "auto"
---

特殊作业编号（workNo）生成逻辑：
- 前缀来源：复用 TicketsType 枚举中各票证前缀（TicketsType.getCodeNumber()，如 DH-、GC-、LD-），该枚举前缀取值与 base_ticket_safe_config.source_type→ticketsType 映射同源（1→11动火、2→14受限空间、3→18盲板抽堵、4→12高处、5→15吊装、6→13临时用电、7→16动土、8→17断路）
- 日期部分：DateUtils.dateTimeNow("yyyyMMdd")
- 序号部分：redisCache.redisTemplate.opsForValue().increment() 每日原子自增，键格式 work_special:no:{prefix}{dateStr}，并 expire(seqKey, 2, TimeUnit.DAYS)
- 组装规则：String.format("%s%s-%04d", prefix, dateStr, seq)
- 落地位置：BaseWorkSpecialServiceImpl.saveWorkSpecial（原规则为 ZY-{ticketsType}+时间戳+随机数）
- 已知上限：Redis 重置后当日序号从 0 重新开始，极端情况可能重复；若发生需切换 DB MAX+1 方案（代码已留注释标记）
