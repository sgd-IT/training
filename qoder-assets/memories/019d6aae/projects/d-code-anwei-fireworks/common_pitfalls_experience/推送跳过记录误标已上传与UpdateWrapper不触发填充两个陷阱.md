---
title: "推送跳过记录误标已上传与UpdateWrapper不触发填充两个陷阱"
usage_scenario:
    - "改造推送服务的状态标记与重推逻辑"
    - "评估用更新时间字段做增量判据的可行性"
    - "排查数据被标记成功却未真正上报的问题"
keywords:
    - "excludeSkipped"
    - "access_status误标已上传"
    - "UpdateWrapper不填充"
    - "MyMetaObjectHandler"
    - "update_date判据"
source: "auto"
---

Bug class: 推送链路里"本地校验跳过的记录"被误标为已上传，导致永久不再重试
Root cause: PushingSDDataService 各 saveXxx 方法把整个待推 list 交给 updateAccessStatus(置 access_status=2)，而 SetSDUploadInfo 组装阶段对图片为空、deviceId/storeroomId 匹配不到平台UUID的记录是 `continue` 跳过（并未真正发出请求），但这些记录仍在原 list 中，于是被一并标记为"已上传"，下轮 eq(access_status,0) 再也扫不到。同时批次结果里的"第N条"是按实际发出的数组下标定位的，若发出去的和统计的不一致就会错配。
Fix pattern: 引入 excludeSkipped(list, skipReasons.keySet()) —— 从待标记名单剔除被跳过的记录后再调用 updateAccessStatus；跳过记录保持 access_status=0 并把具体原因写入 sync_reason，让下一轮继续暴露直至人工修数据。
Reusable lesson: Don't 用原始待推列表去批量标记"已上传" because 组装阶段被 continue 跳过的记录会连带被标成功、数据从此不再重推；instead 先剔除跳过记录再标记，并保证标记顺序与真正发出的 payload 顺序一致。适用于 SD/GZ 等"先组装过滤、再批量推送、按状态位圈定待推范围"的推送服务；不适用于全量必发、无本地过滤分支的推送实现。

Bug class: MyBatis-Plus 逻辑删除/更新时间的填充判断错误
Root cause: MyMetaObjectHandler.updateFill 只在"带实体的 update"（updateById / update(entity, wrapper)）时被触发；项目大量使用 new UpdateWrapper<T>().eq(...).set(...) 形式的更新（如推送成功后置 access_status、批量同步编号），这类调用不会填充 update_date / gmt_modified。
Fix pattern: 判断某字段能否作为"业务修改时间"依据前，先确认写入路径是 updateById 还是纯 UpdateWrapper.set；需要真实反映修改时间的地方显式 .set("update_date", new Date())。附带好处：推送后置 access_status 走 UpdateWrapper 不会误刷 update_date，因此可用 update_date > 上次推送时间 做"修改重推"判据而不会把自己推一次误判成业务改过。
Reusable lesson: Don't 假定声明了 @TableField(fill = FieldFill.UPDATE) 的字段在任何更新下都会被填充 because 纯 UpdateWrapper.set 路径不触发 handler，依赖它做增量/重推判据会漏掉全部变更；instead 逐个确认写入路径，必要时显式 set 时间列。适用于本项目的 em_* 表更新逻辑设计；注意 Temperature/Humidity 的 gmtModified 未声明 fill，即使 handler 设值也进不了 SQL。
