---
title: "MQTT依赖坐标：vertx-mqtt为主，Paho mqttv3仅BOM预留未引用"
usage_scenario:
    - "查找MQTT相关Maven/npm依赖名称与版本"
    - "评估是否引入Paho mqttv3客户端库"
    - "确认某依赖版本是否被BOM统一管理"
keywords:
    - "vertx-mqtt坐标"
    - "Paho mqttv3预留"
    - "dependencies BOM"
    - "netty-codec-mqtt"
    - "mqtt.js"
source: "auto"
---

anwei-fire-remote-monitor 的 MQTT 相关依赖分层坐标：
- 功能实现本体：io.vertx:vertx-mqtt，版本由 module-eiot/pom.xml 的 <vertx.version>4.5.13</vertx.version> 在 dependencyManagement 统一管理，各组件模块只写 groupId+artifactId 不写版本。
- 底层报文编解码：io.netty:netty-all 4.1.116.Final(含 netty-codec-mqtt，MqttVersion/MqttDecoder 所在)，版本在 dependencies/pom.xml 用 <netty.version> + netty-bom 管理。
- 项目自有组件模块：com.enjoy-iot:iot-component-mqtt(内置 broker)、com.enjoy-iot:iot-component-emqx(EMQX 客户端)。
- 前端：npm 包名就叫 mqtt(mqtt.js)，^4.3.7。
- 预留未用：dependencies/pom.xml 的 dependencyManagement 声明了 org.eclipse.paho:org.eclipse.paho.client.mqttv3:${mqtt.version}(=1.2.5)，名字带 mqttv3，但全仓库无任何模块引用它，仅 BOM 预留；本地 .m2 已有该 jar 可直接解析。任何模块引入时只需写 groupId+artifactId 免版本号。Paho mqttv3 1.2.5 支持 MQTT 3.1/3.1.1 不支持 v5(v5 需另一坐标 org.eclipse.paho.mqttv5.client，BOM 未管理)。
根 pom.xml 以 <scope>import</scope> 导入 com.enjoy-iot:dependencies BOM。
