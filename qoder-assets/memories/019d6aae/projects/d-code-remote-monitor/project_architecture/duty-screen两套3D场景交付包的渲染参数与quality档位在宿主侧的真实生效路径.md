---
title: "duty-screen两套3D场景交付包的渲染参数与quality档位在宿主侧的真实生效路径"
usage_scenario:
    - "排查或优化大屏3D场景GPU占用与帧率"
    - "修改scene-url.ts iframe接入参数前判断是否生效"
    - "需要不经交付方单方面降低park场景画质档位"
keywords:
    - "dongguan-map与smart-park交付包"
    - "iframe场景质量档位"
    - "dataset.v7State取证"
    - "sessionStorage崩溃恢复注入"
    - "第三方minified产物无源码"
    - "WebGL GPU占用排查"
source: "auto"
---

duty-screen（值守大屏）的 3D 场景是两套**第三方交付的 minified 打包产物**经 iframe 嵌入，源码不在本仓库，因此任何"改渲染参数"的诉求只能落在宿主层或向交付方提需求。

结构事实（2026 本轮经逐行读产物核实）：
- city = public/dongguan-map/releases/v002-property-r7/（React + react-three-fiber + styled-components，主逻辑在 assets/extends-*.js、场景在 Dongguan-*.js；产物残留 leva 调试库约 197KB，并有单文件 34MB 且文件头非标准 glTF 魔数的 model/glb/turbine.glb）。已内建：dpr 按档 clamp（high 档 [1,1.35]，非 high 为 1）、阴影仅 high 档开 percentage、自带 window.__DONGGUAN_METRICS__（含 GPU 型号、dpr、纹理内存、quality、FPS）可直接取证。未做 on-demand 帧循环（frameloop:'always'），antialias:true 全档开启。
- park = public/smart-park/releases/v007-r001/index.html，实际代码在根 public/assets/v7-*.js（Three.js + Meshopt + bloom 后处理）。已内建：visibilitychange→pause('DOCUMENT_HIDDEN') 与 blur→pause、帧预算监控、91 处 dispose、lod0/1/2 三档资产、contracts/visual-profile.json 定义 HIGH(lod0/dpr1.5/shadow2048/bloom on/motion on)、MEDIUM(lod1/dpr1.2/shadow1024/bloom off/motion on)、LOW(lod2/dpr1/shadow512/bloom off/motion off)。
- 宿主层无泄漏：SceneHost.vue 以 v-if 挂载 iframe，useSceneHost.ts 的 mountScene/destroyScene 在切场景时销毁重建，两个 WebGL 上下文不会并存。

反直觉要点（易导致空转改动）：**scene-url.ts 与 docs/scene-integration.md 里写的 `?embedded=1&quality=HIGH` 中的 quality 参数，v007-r001 交付包并不消费**。引导代码只从 location.search 读 checks（验证面板开关），初始档硬编码 HIGH，路由解析器只解析 #/ hash 且对非 devices 路由显式拒绝 query，UI 外壳也没有 quality 回调。自证方法：打开 park 页面后执行 document.body.dataset.v7State，传 LOW 仍显示 "quality":"HIGH"。

宿主单方面真正可用的降档通道：park 应用启动时会读取同源共享的 sessionStorage key `anwei.v007.context-reload-state.v1`（崩溃恢复状态），写入合法 PENDING 状态并把 state.quality 设为目标档即可生效；四个约束是一次消费制（每次进 park 需重新播种）、url 字段须与实际 iframe 地址完全一致、timeOrigin 须早于当前 iframe、createdAt 须在 60 秒内被消费；任一校验不过则静默回退 HIGH，不会白屏。
