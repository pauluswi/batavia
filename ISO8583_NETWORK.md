# Handling ISO 8583 Single TCP Connection in Cloud

## 🚩 The Challenge
Legacy banking switches and core systems often impose strict networking constraints for ISO 8583 connections:
1.  **Single Persistent Connection**: The switch expects exactly **one** long-lived TCP connection.
2.  **IP Whitelisting**: The connection must originate from a specific, static IP address.
3.  **Stateful Protocol**: ISO 8583 relies on sign-on/echo (0800) messages to maintain the link state.

This conflicts with modern Cloud/Container architectures (like AWS ECS), which are:
- **Ephemeral**: Containers are destroyed and recreated frequently.
- **Horizontally Scaled**: Multiple instances run simultaneously.
- **Dynamic IPs**: Containers get random private IPs.

If 10 instances of the Batavia Middleware try to connect to the switch simultaneously, the switch will likely reject 9 of them or ban the port.

---

## 🏗️ Recommended Solution: The "Connector" Pattern

To resolve this, we must decouple the **Stateless API Layer** (which scales) from the **Stateful TCP Link** (which is a singleton).

### Architecture Diagram

```
[ Mobile / Web ]
      |
      v
[ AWS ALB (Layer 7) ]
      |
      v
+-----------------------------------+           +-----------------------+
|  Batavia API Cluster (ECS)        |           |  Redis / MQ           |
|  (Scales 1...N)                   |           |                       |
|                                   |<--------->|  (Message Broker)     |
|  - Handles REST validation        |           |                       |
|  - Handles Idempotency            |           +-----------------------+
|  - Waits for Async Response       |                       ^
+-----------------------------------+                       |
                                                            v
                                            +-------------------------------+
                                            |  ISO 8583 Connector (ECS)     |
                                            |  (Singleton: 1 Instance)      |
                                            |                               |
                                            |  - Maintains TCP Socket       |
                                            |  - Handles Sign-On / Echo     |
                                            |  - Reconnects on failure      |
                                            +-------------------------------+
                                                            |
                                                            v
                                                    [ AWS NAT Gateway ]
                                                    (Static Elastic IP)
                                                            |
                                                            v
                                                    [ Legacy Switch ]
```

---

## 🛠️ Implementation Details

### 1. The Split
Instead of `ISO8583Service` sending TCP packets directly, it should write to a **Message Queue**.

- **Batavia API Service**:
    - Acts as the "Producer".
    - Converts JSON to ISO 8583 byte array.
    - Generates a correlation ID (STAN or RRN).
    - Pushes the request to a **Request Queue** (e.g., Redis List or Amazon SQS).
    - Subscribes to a **Response Channel** and waits (with timeout).

- **ISO Connector Service** (New Component):
    - Acts as the "Consumer".
    - A standalone Spring Boot application running `jpos` Q2 or a simple `ISOChannel`.
    - Establishes the **Single TCP Connection** to the switch.
    - Reads from the Request Queue and writes to the TCP socket.
    - Listens to the TCP socket for responses.
    - Matches the response to the request (via STAN/RRN).
    - Pushes the result to the **Response Channel**.

### 2. Infrastructure Configuration (AWS)

#### For the Connector Service (Singleton)
- **ECS Service Configuration**: Set `desiredCount: 1`.
- **Deployment Strategy**: Use `RollingUpdate`. When deploying a new version, the old container will terminate (closing the socket), and the new one will start and reconnect.
    - *Note*: This causes a brief downtime (seconds) for ISO transactions during deployment.
- **Resilience**: If the container crashes, ECS auto-restarts it. The application logic must handle "Sign-On" immediately upon startup.

#### For Static IP (Whitelisting)
- Place the ECS Tasks in **Private Subnets**.
- Route outbound traffic through a **NAT Gateway** located in a Public Subnet.
- Assign an **Elastic IP (EIP)** to the NAT Gateway.
- Give this EIP to the bank/switch for whitelisting.

---

## 🔄 Handling Synchronous REST vs. Asynchronous TCP

Since the Frontend expects a synchronous HTTP response, but the internal flow is async via queues:

1.  **Batavia API** receives `POST /transfer`.
2.  It publishes the ISO message to Redis/MQ.
3.  It creates a `CompletableFuture` or uses `RedisTemplate.convertAndSend`.
4.  It waits (e.g., `future.get(30, TimeUnit.SECONDS)`).
5.  **ISO Connector** receives the TCP response and publishes it back to Redis Pub/Sub using the Request ID as the key.
6.  **Batavia API** listener triggers the `CompletableFuture`.
7.  The REST response is returned.

---

## ⚠️ Alternative: Leader Election (If you want a single codebase)

If you don't want to manage two separate deployable artifacts (API vs Connector), you can use **Leader Election**:

1.  Deploy the same Batavia application to N instances.
2.  Use **Spring Integration** or **Kubernetes/AWS API** to elect a "Leader".
3.  Only the **Leader** instance opens the TCP connection.
4.  Non-leader instances forward their ISO traffic to the Leader (via HTTP or RMI).

*Pros*: Single codebase.
*Cons*: Complex logic to handle leader failure and failover. The "Connector Pattern" (Option 1) is generally cleaner and more robust for production.
