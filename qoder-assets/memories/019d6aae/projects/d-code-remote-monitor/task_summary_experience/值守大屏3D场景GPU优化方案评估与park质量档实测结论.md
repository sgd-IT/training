---
title: "值守大屏3D场景GPU优化方案评估与park质量档实测结论"
usage_scenario:
    - "再次排查大屏3D场景GPU占用或降画质"
    - "修改scene-url或park/city交付包接入参数前"
    - "核对quality档位是否真正生效"
keywords:
    - "duty-screen 3D场景iframe"
    - "smart-park quality档位"
    - "dongguan-map交付包"
    - "GPU占用排查取证"
    - "崩溃恢复状态注入"
source: "auto"
---

## Task description
- Core requirement：用户贴出一份"前端大屏 3D 建模 GPU 占用高"的通用优化清单（限帧、降像素比、InstancedMesh、LOD、模型纹理压缩、dispose、减光源阴影），要求结合本项目实际评估；随后要求把 park 场景的 `?quality=HIGH` 改为 MEDIUM，并声明"我来改"（助手不动手）。
- Task background：值守大屏 duty-screen 通过 iframe 嵌入两套第三方交付的打包产物——city=dongguan-map v002-property-r7（React+R3F）、park=smart-park v007-r001（Three.js+Meshopt+bloom）；源码均不在本仓库，只有 public/ 下的 minified 产物。

## Execution process
1. 架构盘点：确认宿主 SceneHost.vue 用 v-if 挂载 iframe，useSceneHost.ts 的 mountScene/destroyScene 在切场景时销毁重建，两个 WebGL 上下文不会并存——宿主层无泄漏。
2. 逐项对照结论：park 已内建 visibilitychange→pause('DOCUMENT_HIDDEN')、blur→pause、帧预算监控、91 处 dispose、lod0/1/2 三档资产、Meshopt 压缩、按档位控制 dpr/shadow/bloom；city 是 R3F frameloop:'always'，dpr 已 clamp（high 档 [1,1.35]，非 high 为 1），阴影仅 high 档开启 percentage，antialias:true 全档开启；city 里存在 34MB 单文件 turbine.glb 且文件头不是标准 glTF 魔数，另残留 leva 调试库 197KB。
3. 关键实测发现：对 public/assets/v7-BXZjmu19.js（377KB/171 行）做覆盖式通读后确认——v007-r001 交付包**完全不读 URL 上的 quality 参数**。证据：引导逻辑只从 location.search 读 checks（验证面板开关）；初始档硬编码 HIGH；状态控制器 Ve 的初始状态只接受 initialState 或默认值；路由解析器只解析 #/ hash 且显式拒绝非 devices 路由的 query；UI 外壳点击只挂 data-scene/data-route/重试取消，无 quality 回调。故 scene-url.ts 与 docs/scene-integration.md 里的 quality=HIGH 属"文档先行、实现未跟上"的装饰参数。
4. 自测方法（2 分钟可复现）：浏览器打开 `/smart-park/releases/v007-r001/index.html?embedded=1&quality=LOW#/campus`，控制台执行 `document.body.dataset.v7State`，仍显示 `"quality":"HIGH"`；该 dataset 由应用在每次状态变更时主动输出，是现成取证抓手。
5. 给出三条路径：正路=向交付方提需求"引导时读取 location.search 的 quality 并过白名单校验作为初始档"；应急路=利用应用自带的崩溃恢复通道，同源 iframe 与宿主共享 sessionStorage，在挂载前写入 key `anwei.v007.context-reload-state.v1` 的合法 PENDING 状态并把 state.quality 设为 MEDIUM，非法值会被应用拒绝并静默回退 HIGH；维持现状不改。
6. 应急路具体写法（以代码编辑块交付用户自行落地，未执行修改）：useSceneHost.ts 新增 seedParkQuality()，由 VITE_PARK_QUALITY 环境变量控制并本地白名单校验 HIGH/MEDIUM/LOW；mountScene 中在 iframeMounted=true 之前、且仅 activeScene==='park' 时调用。四个约束：状态一次消费制（变 CONSUMED，故每次进 park 都要重新播种）；url 字段必须与实际 iframe 地址完全一致（用 new URL(sceneUrl('park'), origin).href 计算）；timeOrigin 取宿主 performance.timeOrigin - 1000（须早于当前 iframe）；createdAt 须在 60 秒内被消费。
7. MEDIUM 档预期效果：bloom 关闭、shadow 2048→1024、dpr 上限 1.5→1.2、LOD 切 lod1、动态扫描光效保留（关光效是 LOW 档行为）。

## Related files
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/duty-screen/components/useSceneHost.ts（给出改动建议，用户自行落地，本轮未修改）
- /anwei-fire-remote-monitor-vue/.env*（建议新增 VITE_PARK_QUALITY=MEDIUM，配置文件不入 git）

## Notes
- Grep/ripgrep 类检索工具对超长单行 minified 大文件（本次为 368KB 单行 chunk 及 38KB chunk）会静默返回 0 匹配，不能据此判定"字符串不存在"；必须换用逐行 Read 或 LSP documentSymbol 交叉验证，否则会得出错误结论。
- Read 工具的 100000 上限按字节计，含中文注释的行更易超限；v7 chunk 第 162 行约 21.5 万字符始终无法直读，只能靠相邻行 + 符号表推断覆盖，最终结论对该行残留不确定性已在回答中如实说明。
- Bash 工具在本轮中途变为不可用（tool not found），后续取证全部改用 Read/Grep/LSP/SearchCodebase 完成。
- 教训：给"改一行就能降 GPU"的建议前，必须先证明该参数真的被消费；否则改动是空转，用户会误以为已优化。

## Task overview
部分完成（分析闭环、改动未落盘）：确认了 duty-screen 两套 3D 交付包的渲染参数现状（多数通用优化项已内建），并推翻了"改 scene-url.ts 的 quality 即可降档"的原定第一刀——v007-r001 不消费 URL quality 参数；改为输出"向交付方提需求 + 宿主侧 sessionStorage 崩溃恢复通道注入"两条可行路径及配套自测方法。用户选择自行修改，故本轮无任何文件写入，scene-url.ts 中的 quality=HIGH 保持原样（保留无害，待交付方实现后自动生效）。
