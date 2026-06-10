# 0001: Adoption of ISO 8583 Connector Pattern

## Status
Accepted

## Context
Legacy banking switches and core systems often require a single, persistent TCP connection for ISO 8583 communication, along with static IP whitelisting. This poses a significant challenge for cloud-native deployments (e.g., AWS ECS Fargate) which are designed for horizontal scalability, ephemerality, and dynamic IP addressing. Directly connecting multiple instances of the Batavia middleware to such a switch would lead to connection rejections, port exhaustion, or system instability.

The Batavia middleware aims to be highly scalable and resilient, but must integrate with these legacy constraints.

## Decision
To reconcile the scalability requirements of the middleware with the single-connection constraint of legacy ISO 8583 systems, we will adopt the **"Connector Pattern"**.

This pattern involves:
1.  **Decoupling**: Separating the stateless, horizontally scalable API layer of the Batavia middleware from a stateful, singleton ISO 8583 "Connector" service.
2.  **Asynchronous Communication**: Using a message broker (e.g., Redis Pub/Sub or Amazon SQS) to facilitate asynchronous communication between the scalable API layer and the singleton Connector.
3.  **Dedicated Connector Service**: A separate, lightweight service (the "Connector") will be responsible for:
    *   Maintaining the single, persistent TCP connection to the legacy ISO 8583 switch.
    *   Handling ISO 8583 specific protocols (e.g., sign-on/echo messages).
    *   Managing connection lifecycle (reconnection on failure).
    *   Routing all outbound ISO 8583 traffic from the API layer to the switch.
    *   Receiving responses from the switch and routing them back to the API layer via the message broker.
4.  **Static IP**: The Connector service will be deployed in a private subnet, with its outbound traffic routed through an AWS NAT Gateway assigned a static Elastic IP, which can then be whitelisted by the legacy switch.

## Consequences

### Positive
*   **Scalability**: The main Batavia API layer can scale horizontally (1 to N instances) without being constrained by the ISO 8583 connection limit.
*   **Resilience**: Isolates the complexity and fragility of the stateful TCP connection into a dedicated, manageable component. Failures in the API layer do not directly impact the ISO connection, and vice-versa.
*   **Maintainability**: Clear separation of concerns. The Connector service can be optimized and managed specifically for its networking role.
*   **Cloud-Native Alignment**: Enables the core middleware to fully leverage cloud benefits like Fargate's serverless compute and auto-scaling.
*   **Compliance**: Provides a clear, controlled egress point for static IP whitelisting.

### Negative
*   **Increased Complexity**: Introduces an additional service (the Connector) and a message broker, increasing the overall architectural complexity and operational overhead.
*   **Asynchronous Nature**: Transforms a potentially synchronous request (from the client's perspective) into an asynchronous internal flow, requiring careful handling of timeouts and response correlation.
*   **Deployment Downtime**: Deployments of the Connector service will cause a brief interruption (seconds) to ISO 8583 transaction processing as the single TCP connection is re-established.
*   **Additional Cost**: The message broker and NAT Gateway incur additional AWS costs.

## Alternatives Considered
*   **Leader Election**: Deploying the full Batavia application on multiple instances, with one instance elected as a "leader" to manage the ISO 8583 connection.
    *   *Rejected*: While reducing the number of deployable artifacts, it adds significant complexity to the application logic for leader election, failover, and inter-instance communication, which was deemed less robust and harder to manage than a dedicated Connector service.
*   **Direct Connection with IP Whitelisting per Instance**: Attempting to whitelist multiple dynamic IPs for each middleware instance.
    *   *Rejected*: Not feasible with legacy switches that expect a single source IP. Also, dynamic IPs of containers make whitelisting impractical.
