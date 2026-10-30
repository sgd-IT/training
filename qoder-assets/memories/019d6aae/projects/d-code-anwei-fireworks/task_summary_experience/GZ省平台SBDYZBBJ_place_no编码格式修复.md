---
title: "GZ省平台SBDY/ZBBJ place_no编码格式修复"
usage_scenario: []
keywords:
    - "GZ省平台"
    - "place_no"
    - "编码格式"
    - "SBDY"
    - "ZBBJ"
---

## 任务描述
修复GZ省平台SBDY（设备定义）和ZBBJ（指标报警）推送中`place_no`字段编码格式错误问题，避免因编码长度不匹配导致'库房或仓库编码不存在'报错。

## 任务总结
- 根因：省平台实际校验逻辑与规范文字描述矛盾——虽写'库房编码'，但所有示例及真实校验均使用15位CKXX仓库编码（如`520281920001101`），而非18位KFXX库房编码（如`520281920001101001`）
- 修复方案：在`SetGZUploadInfo.java`中两处统一将`place_no`赋值逻辑从`getStoreroomNum()`或三元判断改为直接`getStoreNum()`
- 具体修改：1）`getDeviceInfoMap()`方法中设备`place_no`；2）`getTargetAlarmMap()`方法中报警`place_no`
- 效果：`place_no`输出15位仓库编码，与CKXX表和规范示例完全一致，SBDY成功后ZBDY/ZBSS/ZBBJ级联错误自动消除
