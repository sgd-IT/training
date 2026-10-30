# 落地写法

位置定了之后，这些是最容易写错的地方。Vue 3 + uni-app + Element Plus 为准，React / Ant Design 换标签不换位置。

## 1. 移动端底部操作栏（安全区 + 不遮内容）

用 flex 而不是 `position: fixed`，键盘弹起时页面自然压缩，不会出现"按钮浮在键盘上"或"遮住输入框"。

```vue
<template>
  <view class="page">
    <scroll-view class="content" scroll-y>
      <!-- 列表或表单内容 -->
    </scroll-view>
    <view class="action-bar">
      <button class="btn-secondary" @click="cancel">取消</button>
      <button class="btn-primary" @click="submit">提交</button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.page {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
.content {
  flex: 1;
  overflow: hidden;
}
.action-bar {
  flex-shrink: 0;
  display: flex;
  gap: 24rpx;
  padding: 20rpx 32rpx;
  /* 别写死 padding-bottom：全面屏手势条会压住按钮，安全区必须接管这一层 */
  padding-bottom: calc(20rpx + constant(safe-area-inset-bottom));
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}
.btn-primary {
  /* 主按钮占双倍宽度：Fitts 定律里加大目标比缩短距离更见效 */
  flex: 2;
  height: 88rpx;
  line-height: 88rpx;
}
.btn-secondary {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
}
</style>
```

## 2. 键盘弹起时按钮跟着上移

`adjust-position` 各端表现不一致，底部操作栏直接用键盘高度顶上去更稳。只对 `position: fixed` 的输入条适用；页面整体可滚动时优先让容器 flex 收缩（见第 1 条），不用这一层。

```vue
<template>
  <view class="input-bar" :style="{ bottom: kbHeight + 'px' }">...</view>
</template>

<script setup>
const kbHeight = ref(0)
// 键盘高度变化是全局回调，页面卸载时记得关掉，否则离开页面还在改已销毁的组件状态
uni.onKeyboardHeightChange(({ height }) => { kbHeight.value = height })
onUnmounted(() => uni.offKeyboardHeightChange?.())
</script>
```

## 3. 弹窗按钮顺序（Element Plus）

取消在左、主操作在右；危险操作单独一个位置，不与保存并排。

```vue
<el-dialog v-model="visible" title="编辑设备" width="520px">
  <!-- 内容 -->
  <template #footer>
    <!-- 顺序即语义：确认在右，阅读终点天然被当作"下一步" -->
    <el-button @click="visible = false">取消</el-button>
    <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
  </template>
</el-dialog>
```

删除这种不可逆动作，别塞进同一个 footer：

```ts
await ElMessageBox.confirm(`删除后不可恢复，确认删除「${row.name}」？`, '删除确认', {
  confirmButtonText: '删除',
  confirmButtonClass: 'el-button--danger',
  cancelButtonText: '取消',
  type: 'warning'
})
```

确认按钮的文案写具体动作（"删除"），不要写"确定"——用户扫一眼就知道自己在确认什么。

## 4. 表格操作列（右侧固定，最多两个直出）

```vue
<el-table-column label="操作" width="140" fixed="right">
  <template #default="{ row }">
    <el-button link type="primary" @click="edit(row)">编辑</el-button>
    <!-- 第三个操作开始收进更多，行内按钮超过两个必然点错 -->
    <el-dropdown trigger="click">
      <el-button link type="primary">更多</el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="remove(row)">删除</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </template>
</el-table-column>
```

## 5. 批量操作栏（选中才出现）

```vue
<template>
  <!-- 空选状态整条不渲染，避免"看得见点不动"的按钮 -->
  <div v-if="selection.length" class="batch-bar">
    <span>已选 {{ selection.length }} 项</span>
    <el-button type="primary" @click="exportSelected">导出</el-button>
    <el-button link type="danger" @click="removeSelected">删除</el-button>
    <el-button link @click="clearSelection">取消选择</el-button>
  </div>
</template>

<style scoped>
.batch-bar {
  /* 吸底而不是吸顶：批量操作是主流程，要在拇指够得到的位置 */
  position: sticky;
  bottom: 0;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  background: #fff;
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.06);
}
</style>
```

## 6. 文字按钮也要够大

```scss
// 只让文字本身可点是最常见的误触来源，内边距把可点区域撑到 44pt 以上
.text-action {
  display: inline-flex;
  align-items: center;
  min-height: 88rpx;
  padding: 0 24rpx;
}
```

行内并排两个操作时，两者之间至少留 `24rpx`（约 12px）空隙，防止连点误触。

## 7. 空状态

```vue
<template>
  <view class="empty">
    <image src="/static/empty.svg" class="empty__icon" />
    <text class="empty__text">还没有绑定设备</text>
    <!-- 按钮在文案正下方、水平居中，并且是这一屏唯一可点物 -->
    <button class="empty__action" @click="goBind">去绑定</button>
  </view>
</template>
```

## 8. 自定义底部 TabBar 的安全区

原生 tabBar 平台会处理安全区，自定义的一定要自己加，否则全面屏上文字被手势条压掉：

```scss
.tabbar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  /* 给内容区留出同样的高度，否则页面最后一项被 tabbar 永久遮挡 */
  background: var(--tabbar-bg, #fff);
}
```

## 9. 焦点顺序

DOM 顺序必须和视觉顺序一致。常见错误是用 flex 的 `order` 或 `row-reverse` 把"下一步"排到右侧，但 DOM 里还在前面——读屏和键盘会先走到视觉上的右边。

```html
<!-- 错误：视觉上主按钮在右，DOM 里却在左，Tab 顺序与视觉相反 -->
<div class="footer">
  <button class="primary">保存</button>
  <button>取消</button>
</div>

<!-- 正确：调整视觉顺序就调整 DOM 顺序 -->
<div class="footer">
  <button>取消</button>
  <button class="primary">保存</button>
</div>
```
