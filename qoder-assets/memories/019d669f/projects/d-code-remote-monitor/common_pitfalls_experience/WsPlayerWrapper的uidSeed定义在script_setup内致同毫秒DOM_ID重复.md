---
title: "WsPlayerWrapper的uidSeed定义在script setup内致同毫秒DOM ID重复"
usage_scenario:
    - "排查视频播放器不可见或部分渲染异常时"
    - "为多实例Vue组件生成唯一DOM ID时"
keywords:
    - "uidSeed"
    - "DOM ID碰撞"
    - "script setup作用域"
    - "全局计数器"
    - "同毫秒实例化"
source: "auto"
---

Bug 类型：前端组件实例 DOM ID 碰撞。
根因：uidSeed 声明在 <script setup> 内部，每个 WsPlayerWrapper 实例都会把它重新初始化为 0；同一毫秒创建的多个实例会生成完全相同的 id="wsplayer-${Date.now()}-${++uidSeed}"。
修复模式：把 uidSeed 移到模块作用域（<script setup> 之外），或使用全局计数器，确保跨所有实例唯一。
可复用经验：不要在 <script setup> 内声明实例计数器，它会随实例重置并导致重复 DOM ID；应放到模块作用域或使用全局计数器。适用于同一渲染周期内多次实例化的 Vue 组件生成唯一 DOM ID 的场景；不适用于静态 ID 或单实例组件。
