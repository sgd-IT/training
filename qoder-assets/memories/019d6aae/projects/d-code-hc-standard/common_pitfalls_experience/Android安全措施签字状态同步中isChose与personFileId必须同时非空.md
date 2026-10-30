---
title: "Android安全措施签字状态同步中isChose与personFileId必须同时非空"
usage_scenario:
    - "When implementing or debugging safety measure signing workflows in Android apps"
    - "When troubleshooting persistent '安全措施未落实签字完成' errors despite UI showing completion"
keywords:
    - "isChose"
    - "personFileId"
    - "FragmentJobSlipMeasure"
    - "安全措施签字"
    - "状态同步"
source: "auto"
---

Bug class: 安全措施签字状态校验失败
Root cause: 后端验证逻辑 selectWorkStepFinish 要求 isChose 和 personFileId 同时非空；但 Android UI 仅显示签名图片，未显式设置 isChose，导致 DB 更新时 isChose 字段保持 NULL，从而触发持续性校验失败。
Fix pattern: 在 FragmentJobSlipMeasure.java 的 save() 方法中统一补填 isChose="1"，覆盖新签字和历史数据两种场景。
Reusable lesson: Don't rely solely on UI signature display to infer isChose state because the backend validation requires both fields; instead always persist isChose="1" for signed items. Applies when implementing safety measure signing workflows where the backend validates dual-state (selection + signature); does not apply to simple binary yes/no selection without attached file upload.
