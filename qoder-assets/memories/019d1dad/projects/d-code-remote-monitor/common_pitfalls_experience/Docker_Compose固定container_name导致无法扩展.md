---
title: "Docker Compose固定container_name导致无法扩展"
usage_scenario:
    - "尝试对已部署的Docker Compose服务进行水平扩容时失败"
    - "配置K8s或Docker集群前检查部署文件的可扩展性"
keywords:
    - "Docker Compose"
    - "container_name"
    - "水平扩展"
    - "--scale"
source: "auto"
---

在 Docker Compose 中，如果服务定义中指定了固定的 `container_name`（例如 `container_name: anwei-remote-monitor`），则该服务无法通过 `docker compose up --scale server=5` 进行水平扩展。因为容器名称必须唯一，Docker 不允许启动多个同名容器。若需支持多实例部署，应移除 `container_name` 字段，让 Docker 自动生成唯一名称。（来源：Read docker-compose.yml）
