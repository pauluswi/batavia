# Batavia Middleware Architecture

This document provides a detailed look into the architecture of the Batavia middleware service, covering logical components, runtime transaction flows, and physical deployment models.

---

## 1. Logical Architecture

This diagram shows the primary components and their relationships within the middleware. It illustrates the separation of concerns between the API layer, the core business logic, and the protocol-specific adapters.

```
+-----------------------------------------------------------------+
|                                                                 |
|  [ Channels / Clients (Web, Mobile, Partner API) ]              |
|                                                                 |
+-----------------------------------------------------------------+
                         |
                 (REST / JSON over HTTPS)
                         |
+------------------------V----------------------------------------+
|                                                                 |
|  [ Spring Boot Application (Batavia Middleware) ]               |
|                                                                 |
|    +---------------------------------------------------------+  |
|    |  Controller Layer (REST Endpoints)                      |  |
|    |  e.g., /api/{protocol}/transfer                         |  |
|    +---------------------------------------------------------+  |
|                         |                                       |
|    +--------------------V------------------------------------+  |
|    |  Cross-Cutting Aspects (AOP)                            |  |
|    |   - @Idempotent: Idempotency Check                      |  |
|    |   - @CircuitBreaker: Resilience & Fault Tolerance       |  |
|    |   - @Retryable: Transient Failure Handling              |  |
|    +---------------------------------------------------------+  |
|                         |                                       |
|    +--------------------V------------------------------------+  |
|    |  Service Layer (Business Logic)                         |  |
|    |   - FundTransferService                                 |  |
|    |   - Customer20022Service                                |  |
|    |   - Customer8583Service                                 |  |
|    |   - Protocol Routing Logic                              |  |
|    +---------------------------------------------------------+  |
|                         |                                       |
|    +--------------------V------------------------------------+  |
|    |  Protocol Abstraction Layer                             |  |
|    |   - ISO8583Service (using jpos)                         |  |
|    |   - ISO20022Service (using jackson-dataformat-xml)      |  |
|    +---------------------------------------------------------+  |
|                                                                 |
+-----------------------------------------------------------------+
                         |
         (TCP/IP for ISO 8583, HTTP/XML for ISO 20022)
                         |
+------------------------V----------------------------------------+
|                                                                 |
|  [ Mocked Downstream Systems ]                                  |
|   - Core Banking System                                         |
|   - Payment Switch (e.g., Artajasa, Rintis)                     |
|                                                                 |
+-----------------------------------------------------------------+
```

---

## 2. Transaction Flow (Sequence Diagram)

This sequence diagram illustrates the runtime flow of a **Fund Transfer** request. It shows how the various components and aspects interact to process a single transaction.

```
Client                Controller          IdempotencyAspect     CircuitBreaker        FundTransferService   ISO8583Service(Mock)
  |                      |                      |                      |                      |                      |
  |POST/api/8583/transfer|                      |                      |                      |                      |
  | (with X-Request-ID)  |                      |                      |                      |                      |
  |--------------------->|                      |                      |                      |                      |
  |                      | @Idempotent          |                      |                      |                      |
  |                      |--------------------->|                      |                      |                      |
  |                      |                      | check(requestId)     |                      |                      |
  |                      |                      |--------------------->|                      |                      |
  |                      |                      | [Not Found]          |                      |                      |
  |                      |                      |                      |                      |                      |
  |                      | @CircuitBreaker      |                      |                      |                      |
  |                      |-------------------------------------------->|                      |                      |
  |                      |                      |                      | allowRequest()       |                      |
  |                      |                      |                      |--------------------->|                      |
  |                      |                      |                      |                      | transferFunds()      |
  |                      |                      |                      |                      |--------------------->|
  |                      |                      |                      |                      |                      | createFundTransferRequest()
  |                      |                      |                      |                      |                      |--------------------->
  |                      |                      |                      |                      |                      |                      |
  |                      |                      |                      |                      |                      |        ISOMsg        |
  |                      |                      |                      |                      |                      |<---------------------
  |                      |                      |                      |                      |                      |
  |                      |                      |                      |                      |                      | createFundTransferResponse()
  |                      |                      |                      |                      |                      |--------------------->
  |                      |                      |                      |                      |                      |                      |
  |                      |                      |                      |                      |                      |      ISOMsg (Resp)   |
  |                      |                      |                      |                      |                      |<---------------------
  |                      |                      |                      |                      |                      |
  |                      |                      |                      |FundTransferResponseDTO|                     |
  |                      |                      |                      |<---------------------|                      |
  |                      |                      |                      |                      |                      |
  |                      |                      | recordSuccess()      |                      |                      |
  |                      |                      |<---------------------|                      |                      |
  |                      |                      |                      |                      |                      |
  |                      | put(requestId, resp) |                      |                      |                      |
  |                      |<---------------------|                      |                      |                      |
  |                      |                      |                      |                      |                      |
  |  200 OK (Response)   |                      |                      |                      |                      |
  |<---------------------|                      |                      |                      |                      |
  |                      |                      |                      |                      |                      |
```

### Flow Explanation:
1.  **Client Request**: The client sends a `POST` request with a unique `X-Request-ID`.
2.  **Idempotency Aspect**: The `@Idempotent` aspect intercepts the call, checks if the `X-Request-ID` has been processed. If not, it proceeds.
3.  **Circuit Breaker Aspect**: The `@CircuitBreaker` aspect checks the state of the downstream service. If the circuit is closed, it allows the request to proceed.
4.  **Service Logic**: `FundTransferService` is invoked. It determines the protocol (e.g., `8583`).
5.  **Protocol Mapping**: It calls the appropriate service (`ISO8583Service`) to create and process the ISO message.
6.  **Mock Response**: The mock service returns a successful ISO response.
7.  **Response Handling**: The `FundTransferService` parses the ISO response into a DTO.
8.  **Aspects (Post-Execution)**:
    - The Circuit Breaker records a successful call.
    - The Idempotency Aspect stores the successful response against the `X-Request-ID`.
9.  **Client Response**: The final DTO is serialized to JSON and returned to the client.

---

## 3. Physical Deployment Architecture

This architecture is detailed in our cloud deployment guides, which cover AWS deployment and specific networking strategies for legacy systems.

- **[AWS Cloud Deployment Guide](./CLOUD_DEPLOYMENT.md)**
- **[ISO 8583 Networking Guide](./ISO8583_NETWORK.md)**
