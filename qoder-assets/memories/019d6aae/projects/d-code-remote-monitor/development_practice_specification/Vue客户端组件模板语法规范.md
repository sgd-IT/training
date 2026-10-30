---
title: "Vue客户端组件模板语法规范"
usage_scenario:
    - "编写Vue客户端组件template时避免误加script/style标签"
    - "审查模板代码是否符合Vue SFC规范"
    - "修复因script/style标签导致的运行时警告"
keywords:
    - "Vue客户端组件"
    - "template"
    - "script标签"
    - "style标签"
    - "副作用"
source: "auto"
---

Vue客户端组件（client component）的template模板中禁止使用带有副作用的标签（如<script>和<style>），这些标签会被忽略；相关逻辑必须移至<script setup>或<style>块中。
