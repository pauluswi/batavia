# 0001: Adoption of ISO 8583 Connector Pattern

## Status
Accepted

## Context
The Batavia middleware needs to integrate with legacy banking switches and core systems using the ISO 8583 protocol. These legacy systems typically impose strict networking constraints:
1.  **Single Persistent TCP Connection**: The switch expects exactly one long-lived TCP connection.
2.  **Static IP Whitelisting**: Connections must originate from a specific, static IP address.
3.  **Stateful Protocol**: ISO 8583 relies on sign-on/echo messages to maintain link state.

Modern cloud-native architectures, such as AWS ECS Fargate, are designed for horizontal scalability, ephemerality, and dynamic IP assignments. Directly connecting multiple instances of the Batavia middleware to a single ISO 8583 switch would lead to:
- Connection rejections or bans from the legacy switch.
- Inability to scale the middleware horizontally without violating the single connection constraint.
- Operational complexity in managing static IPs for dynamic containers.

## Decision
To reconcile the scalability requirements of the Batavia middleware with the constraints of legacy ISO 8583 systems, we adopt the **"Connector Pattern"**.

This pattern involves:
1.  **Decoupling**: Separating the stateless, horizontally scalable API layer (the main Batavia application) from a stateful, singleton "ISO 8583 Connector" service.
2.  **Asynchronous Communication**: The main Batavia application will communicate with the ISO 8583 Connector via a message broker (e.g., Redis Pub/Sub or AWS SQS). Requests will be published to a request queue, and responses will be consumed from a response channel, using correlation IDs for matching.
3.  **Singleton Connector**: The ISO 8583 Connector will be deployed as a single instance (e.g., on AWS ECS Fargate with `desiredCount: 1`).
4.  **Static IP**: The Connector's outbound traffic will be routed through a NAT Gateway with a static Elastic IP, which can be whitelisted by the legacy switch.
5.  **Connection Management**: The Connector will be responsible for establishing, maintaining, and re-establishing the single persistent TCP connection, including handling ISO 8583 specific link management messages (e.g., 0800 echo/sign-on).

## Consequences

### Positive
- **Scalability**: The main Batavia API can scale horizontally without impacting the ISO 8583 connection.
- **Resilience**: Isolates the complexity and fragility of the legacy connection into a dedicated, managed component. Failures in the Connector do not directly bring down the entire API.
- **Maintainability**: Clear separation of concerns. The Connector can be updated or managed independently.
- **Compliance**: Easier to manage static IP requirements for whitelisting.

### Negative
- **Increased Complexity**: Introduces an additional service (the Connector) and a message broker, increasing the overall system's architectural complexity.
- **Asynchronous Flow**: Transforms a synchronous client request into an asynchronous internal flow, requiring careful handling of timeouts and response correlation.
- **Deployment Overhead**: Requires deploying and managing two distinct services (API and Connector).
- **Brief Downtime on Connector Deployment**: Deploying a new version of the Connector will briefly interrupt ISO 8583 transactions as the single connection is re-established.

## Alternatives Considered
- **Leader Election**: Deploying the same codebase to multiple instances, with one instance elected as a "leader" to manage the TCP connection.
    - *Rejected because*: Adds significant complexity to the application logic for leader election, failover, and inter-instance communication, which is harder to manage and debug than a dedicated connector service.
- **Direct Connection with Static IPs (non-scalable)**: Deploying a single instance of the Batavia application with a static IP.
    - *Rejected because*: Violates horizontal scalability requirements and introduces a single point of failure for the entire middleware.
