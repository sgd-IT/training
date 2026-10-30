---
title: "InboundMessage abstraction for uniform message ingestion"
usage_scenario:
    - "When extending alarm normalization to support new transport protocols"
    - "When designing a unified message ingestion layer for IoT platforms"
keywords:
    - "InboundMessage"
    - "message ingestion"
    - "transport protocol"
    - "alarm normalization"
source: "auto"
---

The project uses an InboundMessage abstraction to unify message ingestion from different transport protocols (UDP/TCP/HTTP). This abstraction includes fields for transport type, remote address, payload, device ID hint, receive time, and metadata. It serves as the foundation for alarm normalization and should be extended to support all alarm sources.
