---
title: "Baidu Map GL 地图引擎集成"
usage_scenario:
    - "扩展 duty-screen 地图功能或图层控件时"
    - "排查大屏地图与3D场景渲染归属时"
keywords:
    - "BMapGL"
    - "地图引擎"
    - "Baidu Map"
    - "duty-screen"
    - "地图初始化位置"
source: "auto"
---

项目使用 Baidu Map GL (BMapGL) 作为地图渲染引擎，支持 BMAP_EARTH（地球模式）、BMAP_SATELLITE（卫星模式）和 BMAP_NORMAL（普通模式）三种地图类型；初始化代码位于 /src/views/eiot/firewatch/duty-screen/index.vue 的 script 部分。

## Notes
- "初始化代码位于 duty-screen/index.vue"这一表述需按当前结构理解：duty-screen 的大屏三维场景已改为通过 iframe 嵌入两套第三方交付产物（city=dongguan-map、park=smart-park，见 project_architecture「duty-screen两套3D场景交付包的渲染参数与quality档位在宿主侧的真实生效路径」），index.vue 内仅保留宿主页与其自有地图容器逻辑。排查地图/3D 渲染参数或 GPU 占用问题时，先确认要改的是宿主自有 BMapGL 容器还是 iframe 内的交付包，后者源码不在本仓库，只能改宿主层或向交付方提需求。
