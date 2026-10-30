# 落地写法示例

示例取自 hc-standard 风险单元停用场景，字段名可按项目替换。

## 前端（Vue + Element UI/Plus）

### F1 拆 rules，并给必填项出红星

主表单与停机弹窗字段不同，共用一份 rules 会让校验互相串味。

```vue
<el-form ref="logForm" :model="logForm" :rules="logRules" label-width="80px">
  <el-form-item label="停用时间" prop="stopTime">
    <el-date-picker v-model="logForm.stopTime" type="datetime"
      value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择停用时间" />
  </el-form-item>
  <el-form-item label="原因" prop="reason">
    <el-input v-model="logForm.reason" type="textarea"
      placeholder="请填写停用原因，如：装置检修" />
  </el-form-item>
</el-form>
```

```js
data() {
  return {
    logForm: {},
    logRules: {
      stopTime: [{ required: true, message: "请选择停用时间", trigger: "change" }],
      reason: [{ required: true, message: "请填写停用原因", trigger: "blur" }],
    },
  };
}
```

红星由 rules 的 `required: true` 自动生成，所以 `prop` 必须与 rules 的 key 完全一致，对不上规则不触发也不报错。

### F2 提交前先校验

```js
submitStop() {
  // 不校验就发请求，错误会由后端或外部平台抛，用户看到的是技术报文
  this.$refs.logForm.validate(valid => {
    if (!valid) return;
    this.logForm.hazardCode = this.stopRow.hazardCode;
    this.logForm.riskUnitId = this.stopRow.id;
    addRiskunitlog(this.logForm).then(() => {
      this.$modal.msgSuccess("记录添加成功");
      this.stopShow = false;
      this.getList();
    });
  });
}
```

### F3 提交中置灰，防重复点击

```vue
<el-button type="primary" :loading="submitting" @click="submitStop">确定</el-button>
```

## 后端（Java）

### B1 必填校验放方法最前面

```java
@Override
public int insertBaseRiskUnitLog(BaseRiskUnitLog baseRiskUnitLog) {
    // 放在最前面：原因没过校验就不该改单元状态、更不该推平台
    if (StringUtils.isBlank(baseRiskUnitLog.getReason())) {
        throw new ServiceException("请填写停用原因");
    }
    // ...原逻辑
}
```

文案不写 "reason 不能为空"，用户看到的是字段名不是人话。`ServiceException` 会被全局异常处理器转成 `AjaxResult.error(msg)`，前端直接弹这段中文。

### B2 外部失败码判定 + 报文转译

```java
JSONObject json = api.saveEnable(baseRiskUnitLog);
// 平台失败码不止 500，写死 == 500 会把 400 误标成"已同步"
if (json.getInt("code", -1) != 200) {
    baseRiskUnitLog.setSyncStatus("2");
    // 原始报文留给运维查，返回给用户的要是业务话术
    baseRiskUnitLog.setSyncResult(json.getStr("msg"));
    throw new ServiceException("停用记录已保存，但同步到园区平台失败，请稍后重试");
}
baseRiskUnitLog.setSyncStatus("1");
baseRiskUnitLog.setSyncResult(json.getStr("data"));
```

注意：如果该方法带 `@Transactional`，抛异常会连带回滚记录插入。想让"记录留下、同步失败"成立，要么不加事务，要么把推送移出事务、失败只改状态不回滚。

### B3 参数注解校验（可选，与 B1 二选一或并存）

```java
public AjaxResult add(@RequestBody @Valid BaseRiskUnitLog baseRiskUnitLog) {
    return toAjax(baseRiskUnitLogService.insertBaseRiskUnitLog(baseRiskUnitLog));
}
```

```java
// message 一定要写中文：不写就是 Spring 默认英文，会原样弹给用户
@NotBlank(message = "请填写停用原因")
private String reason;
```

注解只覆盖 `@RequestBody` 上的字段级约束，跨对象、跨表的业务规则仍要在 Service 里判。

### B4 禁止把技术异常直接抛给前端

```java
// 反例：全局处理器会取 e.getMessage()，SQL 原文直接弹到页面
throw new RuntimeException(e.getMessage());

// 正例：业务异常带中文话术，技术细节只进日志
log.error("同步园区平台失败, riskUnitId={}", baseRiskUnitLog.getRiskUnitId(), e);
throw new ServiceException("同步到园区平台失败，请稍后重试");
```

## 好坏文案对照

| 坏 | 好 |
|---|---|
| `reason 不能为空` | `请填写停用原因` |
| `「原因」不能为空` | `请填写停用原因，如：装置检修` |
| `Column 'reason' cannot be null` | `请填写停用原因` |
| `must not be null` | `请填写停用原因` |
| `停用时间不能为空` | `请选择停用时间` |
| `启用时间必须晚于停用时间` | `启用时间要晚于停用时间，请调整` |
| 平台报文整段弹出 | `停用记录已保存，但同步到园区平台失败，请稍后重试`（原文存 `sync_result`） |
| `null` | `操作失败，请稍后重试` |
| `保存失败` | `保存失败，请检查网络后重试` |
| `同步失败` | `已保存，但同步到园区平台失败，运维可在同步结果列查看原文` |
