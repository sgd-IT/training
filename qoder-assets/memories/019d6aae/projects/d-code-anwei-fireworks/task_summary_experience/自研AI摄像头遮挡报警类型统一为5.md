---
title: "自研AI摄像头遮挡报警类型统一为5"
usage_scenario:
    - "对接外部平台时需统一报警类型编码的改造"
    - "排查前端报警名称显示为空【】的根因与修复"
    - "同类AI算法报警需映射到已有标准类型时的代码调整"
keywords:
    - "自研AI"
    - "广西平台"
    - "报警类型统一"
    - "摄像头遮挡"
source: "auto"
---

## 任务描述
统一自研AI摄像头遮挡报警类型，使其与广西平台规范一致（即 alarm_type=5 表示‘摄像头遮挡偏移’），解决前端因 type=6 无映射而显示【】的问题。

## 执行过程
```mermaid
graph TD
    A[问题:前端显示【】] --> B[Grep定位报警类型映射逻辑]
    B --> C[发现AlramInfo.java与Mapper中缺少type 6映射]
    C --> D[用户提出更优解：源头改为type 5]
    D --> E[Read确认GetCameraAlarmInfoController.java报警触发点]
    E --> F[Read确认SetGXUploadInfo.java广西平台映射逻辑]
    F --> G[SearchReplace修改：源头handleAlarmType(6→5)]
    G --> H[SearchReplace修改：删除mapToGxAlarmType并直取alarmType]
    H --> I[git diff验证变更精准无误]
```

## 任务总结
成功将自研AI产生的摄像头遮挡报警类型从6改为5：1）在GetCameraAlarmInfoController.java中将occluded报警的handleAlarmType参数由6改为5；2）在SetGXUploadInfo.java中删除冗余的mapToGxAlarmType方法，恢复直接使用record.getAlarmType()。实现本地与广西平台报警类型完全对齐，前端可正确显示【摄像头遮挡偏移】。
