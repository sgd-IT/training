# 落地写法与参数推导

Vue 3 + uni-app / Element Plus 为准，React / 小程序换标签不换数值。

## 1. 微明度色阶

### 定义 4 级 surface

```scss
:root {
  /* 亮色：page 略灰当 0 级，卡片纯白当 1 级。
     1 级以上不再往白提升——白到顶了，浮层靠投影区分，不靠亮度 */
  --surface-0: #f7f8fa;
  --surface-1: #ffffff;
  --surface-2: #ffffff;
  --surface-3: #ffffff;

  --hairline: rgb(16 24 40 / 6%);
  --shadow-1: 0 1px 2px rgb(16 24 40 / 6%), 0 1px 3px rgb(16 24 40 / 4%);
  --shadow-2: 0 8px 24px -8px rgb(16 24 40 / 12%), 0 2px 4px -2px rgb(16 24 40 / 4%);
  --brand-rgb: 30 41 59;
}

html.dark {
  /* 暗色：别碰 #000，白字光晕最重。每级叠白 4%/7%/10%，靠亮度差分层 */
  --surface-0: #121417;
  --surface-1: rgb(30 33 37);
  --surface-2: rgb(38 42 46);
  --surface-3: rgb(46 50 54);
  --hairline: rgb(255 255 255 / 8%);
  --shadow-1: 0 1px 2px rgb(0 0 0 / 40%);
  --shadow-2: 0 8px 24px -8px rgb(0 0 0 / 56%);
}
```

上面 dark 的 rgb 是 `#121417` 分别叠 4%/7%/10% 白**预先算好**的结果，等价于 `color-mix(in oklab, #fff 4%, #121417)`。小程序旧 webview 不支持 `color-mix`，所以直接写死；PC Web 想用 `color-mix` 表达也可以，语义一致。

### hairline 与聚焦双环

```scss
.card {
  background: var(--surface-1);
  border-radius: 12px;
  /* 用 inset 阴影当 1px 边：不占盒模型、不会被 subpixel 吃掉半像素 */
  box-shadow: inset 0 0 0 1px var(--hairline), var(--shadow-1);
}

.field {
  background: var(--surface-0);
  border-radius: 10px;
  /* 平时只有 hairline，没有任何亮线 */
  box-shadow: inset 0 0 0 1px var(--hairline);
  transition: background-color 160ms ease, box-shadow 160ms ease;

  &:focus-within {
    /* 双环：内 1px 咬合 + 外 3px 晕开，同时字段自身升一级 surface */
    background: var(--surface-1);
    box-shadow:
      inset 0 0 0 1px rgb(var(--brand-rgb) / 40%),
      0 0 0 3px rgb(var(--brand-rgb) / 14%);
  }

  /* 键盘焦点要比鼠标焦点更明确，别让两种用户拿到同一个强度 */
  &:has(:focus-visible) {
    outline: 2px solid rgb(var(--brand-rgb) / 60%);
    outline-offset: 2px;
  }
}

@media (forced-colors: active) {
  /* 高对比模式下阴影全部被吃掉，必须退回系统实线 */
  .field:focus-within {
    outline: 2px solid;
  }
}
```

`:has()` 需要 Chrome 105+ / Safari 15.4+。小程序旧基础库不支持时，用 JS 在首帧 `Tab` keydown 时给容器加 `.is-kbd` 类，样式同上，别放弃键盘焦点。

## 2. 暗色字重补偿

```scss
:root {
  --fw-body: 400;
  --fw-strong: 600;
  --fw-title: 700;
}

html.dark {
  /* 白字在黑底上光晕外溢，同一 wght 视觉更粗，标题降一档找平 */
  --fw-strong: 560;
  --fw-title: 640;
  /* 正文 400 不动：12–14px 低对比 + 细笔画会糊成一团 */
  --fw-body: 400;
}

h1, h2, .title { font-weight: var(--fw-title); }
.strong       { font-weight: var(--fw-strong); }
```

前提是字体为可变字体（`Inter var`、`HarmonyOS Sans SC`、`MiSans VF` 等）。静态字体下 `font-weight: 560` 会被取整到最近的可用字重，看起来时好时坏——这种情况只能换 300 字重的字族，不要用 `-webkit-text-stroke` 硬凑。

## 3. 流式文字晕染

```vue
<template>
  <p class="stream">
    <!-- 读屏器读整段；逐字 span 全部 aria-hidden，否则会被一个字一个字念出来 -->
    <span class="sr-only">{{ text }}</span>
    <span aria-hidden="true">
      <span
        v-for="(seg, i) in segments"
        :key="i"
        class="ink"
        :style="{ '--i': i % 24 }"
      >{{ seg }}</span>
    </span>
  </p>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({ text: { type: String, default: '' } })

// 中文按字、英文按词切分。逐字 inline-block 会把英文单词从中间折断换行
const segments = computed(() => {
  if (typeof Intl !== 'undefined' && Intl.Segmenter) {
    return [...new Intl.Segmenter('zh', { granularity: 'word' }).segment(props.text)]
      .map((s) => s.segment)
  }
  // 小程序旧基础库没有 Segmenter：退化成按字符切，只影响英文换行位置
  return Array.from(props.text)
})
</script>

<style scoped>
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip-path: inset(50%);
}

.ink {
  display: inline-block;
  white-space: pre-wrap;
  /* --i 取模 24：超过 24 段后延迟不再累加，否则长段落要等到天荒地老 */
  animation: ink-in 420ms cubic-bezier(.22, .61, .36, 1) both;
  animation-delay: calc(var(--i) * 30ms);
}

@keyframes ink-in {
  from { filter: blur(6px); opacity: 0; transform: translateY(1px); }
  to   { filter: blur(0);   opacity: 1; transform: none; }
}

/* 低端安卓 webview 逐字 filter 会掉帧：砍掉模糊，只留位移渐显。
   .is-low-perf 由首屏 rAF 帧率采样后挂到容器上，别用屏宽猜 */
.is-low-perf .ink {
  animation-name: fade-in;
}

@keyframes fade-in {
  from { opacity: 0; transform: translateY(2px); }
  to   { opacity: 1; transform: none; }
}

@media (prefers-reduced-motion: reduce) {
  .ink { animation: none; }
}
```

行内图标：

```scss
.inline-icon {
  display: inline-block;
  width: 1em;
  height: 1em;
  /* 往下挪 1/8 字高，让图标中心和文字视觉中线对齐。
     必须用 em：px 写死在改了字号之后立刻歪 */
  vertical-align: -0.125em;
}
```

## 4. 数字与排版

```scss
.num {
  font-variant-numeric: tabular-nums;
  /* 老内核补特性开关，和上面叠加写不冲突 */
  font-feature-settings: 'tnum' 1;
  /* tnum 只管数字：% . + - 仍会变宽，容器留固定宽度并右对齐才真的不晃 */
  min-width: 6ch;
  text-align: right;
}
```

折叠区（思考框、详情展开）：

```html
<div class="collapse" :class="{ open }">
  <div class="collapse__inner">
    <slot />
  </div>
</div>
```

```scss
.collapse {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 280ms cubic-bezier(.4, 0, .2, 1);

  &.open { grid-template-rows: 1fr; }
}

.collapse__inner {
  /* 这两行缺一个就不生效，是这套写法最常被漏掉的地方 */
  overflow: hidden;
  min-height: 0;
}
```

`grid-template-rows` 的 fr 过渡要 Chrome 107+ / Safari 16+。小程序端拿不到可靠的能力检测，直接在编译期分方案：小程序改用 `max-height` + 定值过渡，或干脆条件渲染（内容短的时候动画收益不如一次渲染来得实在）。

排版保护：

```scss
.title { text-wrap: balance; }  /* 标题防孤字，中英文都吃 */
.para  { text-wrap: pretty; }   /* 只影响西文孤词，中文基本无感 */
```

中文场景真正管用的是控制容器宽度和 `letter-spacing`，别指望 `pretty`。

## 5. 动效性格

参数（k = stiffness，c = damping，m = mass）：

```ts
// 拖拽吸附、抽屉落位：过阻尼，零回弹
// 临界阻尼 = 2√(k·m) = 2√1200 ≈ 69，取 70 刚好压死在目标位，一次都不晃
const snapSpring = { type: 'spring', stiffness: 1200, damping: 70, mass: 1 } as const

// 点赞、复制成功：欠阻尼，越过目标再弹回来一次
// 临界阻尼 = 40，取 18 得 ζ≈0.45，一次明显回弹后收敛
const popSpring = { type: 'spring', stiffness: 400, damping: 18, mass: 1 } as const
```

Framer Motion、`@vueuse/motion`、`motion-vue` 参数同名，直接换。

不引库时的 CSS 近似：

```scss
.snap { transition: transform 120ms cubic-bezier(0, 0, .2, 1); }       /* 起步即快，末尾咬死 */
.pop  { transition: transform 320ms cubic-bezier(.34, 1.56, .64, 1); } /* 越过去一点再回来 */
```

拖拽期间必须摘掉过渡，否则手指还在动它自己往回弹：

```ts
function onDragStart() { el.value.classList.add('dragging') }
function onDragEnd()   { el.value.classList.remove('dragging') }
```

```scss
.dragging { transition: none !important; }
```

反馈气泡 2.5s 自动隐藏：

```ts
let hideTimer: ReturnType<typeof setTimeout> | undefined

function flash() {
  visible.value = true
  // 必须先清：连点两次时，第一次的定时器会把第二次的气泡提前关掉
  clearTimeout(hideTimer)
  hideTimer = setTimeout(() => { visible.value = false }, 2500)
}

onUnmounted(() => clearTimeout(hideTimer))
```

## 端侧适配备忘

| 特性 | PC Web | 微信小程序 | 处理 |
|---|---|---|---|
| `color-mix()` | ✅ Chrome 111+ | ⚠️ 旧 webview 不支持 | 预算 rgb 写死 |
| `:has()` | ✅ Chrome 105+ | ⚠️ 旧基础库不支持 | JS 加 `.is-kbd` 类 |
| `grid-template-rows` fr 过渡 | ✅ Chrome 107+ | ⚠️ 不可靠 | 改 `max-height` 或条件渲染 |
| 逐字 `filter: blur` | ✅ | ⚠️ 低端机掉帧 | 降级 opacity + translateY |
| `Intl.Segmenter` | ✅ | ⚠️ 旧基础库缺失 | 退化按字符切 |
| `text-wrap: balance/pretty` | ✅ 新版 | ❌ 忽略 | 不影响布局，渐进增强即可 |
| 可变字体 `font-weight: 360` | ✅ | ✅ 字族自带即可 | 静态字体换字族 |
| `tabular-nums` | ✅ | ✅ | 字体需带 tnum 特性 |

## 参数速查

| 参数 | 值 | 为什么是这个值 |
|---|---|---|
| surface 步进 | 1.5%–3% L | 低于 1% 分不出层，高于 5% 变成色块 |
| 暗色底色 | `#121417` 系 | 纯黑让白字光晕最重 |
| 暗色叠白 | 4% / 7% / 10% | 一级一档，等差递增 |
| 字重降档 | 标题 -40 wght | 正文小字不降，降了会糊 |
| 逐字错开 | 30ms | 更小糊成一次闪现，更大追不上吐出速度 |
| 逐字动画时长 | 420ms | 与 30ms 错开配合，约同时收敛 |
| chunk 上限 | 24 段 | 24 × 30ms = 720ms，读者不用等 |
| blur 起值 | 6px | 再大就是白屏，字先看不见了 |
| 聚焦内环 | 1px / 40% | 咬合感来自这一圈贴边的实色 |
| 聚焦外环 | 3px / 14% | 晕开，不抢内环 |
| 过阻尼 | k1200 / c70 | ζ≈1.01，零回弹 |
| 欠阻尼 | k400 / c18 | ζ≈0.45，一次回弹 |
| 反馈自动消失 | 2.5s | 够瞥一眼，又不占位 |
