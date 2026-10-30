---
title: "SXTFX视频报警数据规范"
usage_scenario: []
keywords:
    - "SXTFX"
    - "视频报警"
    - "alarm_info"
    - "VIDEO_CODE"
    - "PLACE_NO"
---

SXTFX（视频报警数据）功能规范：
- 数据源为 `alarm_info` 表；
- 核心字段包括：主键ID、企业code（COMPANY_CODE）、库房编码（PLACE_NO）、视频编码（VIDEO_CODE，20位，关联12.2视频设备信息表ID）、摄像头名称（CAMERA_NAME）、视频报警类型（ALARM_TYPE）、报警截图路径（ALARM_IMG）、告警时间（BEGIN_DATE）、消警时间（END_DATE）、数据采集时间（COLLECT_TIME）；
- 文件格式为JSON+ZIP压缩包，命名规则：`企业编码_SXTFX_yyyyMMddHHmmss.zip`；
- 上传频率：报警新增/更新即刻上传；销警后上传含非空`end_date`的记录表示解除，此后不再上传直至新报警。
