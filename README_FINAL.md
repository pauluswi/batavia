# Batavia - Production-Inspired Banking Middleware

[![Java Version](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Build Java CI with Maven](https://github.com/pauluswi/batavia/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/pauluswi/batavia/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

> A production-style banking middleware that demonstrates how modern digital channels integrate with core banking systems and payment networks using ISO 8583 and ISO 20022, designed for high-volume, 24/7 financial operations.

---

## 📌 Purpose of This Project

This repository is a **showcase project** that demonstrates real-world banking middleware design and engineering practices, inspired by production experience in a regulated banking environment.

All external dependencies such as **core banking systems, payment switches, and networks are mocked**, while preserving:
- Realistic transaction flows
- Architectural decisions
- Failure handling strategies
- Compliance-aware design

This project is **not a simulator of a specific bank**, but a **transferable reference architecture**.

---

## 🏗️ What This Middleware Solves

### ✔ Connects Multiple Channels to Core Banking
- Mobile & web banking
- Partner APIs
- Internal services

Channels interact only with a **clean REST/JSON API**, never directly with core systems.

### ✔ Standardizes Communication
- REST / JSON for channels
- ISO 8583 for legacy payment networks
- ISO 20022 for modern real-time payments

All protocol complexity is isolated inside the middleware.

### ✔ Handles High-Volume Transactions Safely
- Designed for sustained high TPS
- Built for correctness, traceability, and stability
- Financial safety prioritized over raw throughput

---

## 🛠️ Tech Stack & Implemented Features

- **Language**: Java 17
- **Framework**: Spring Boot 3.3.5
- **Protocols**: REST, ISO 8583, ISO 20022
- **Libraries**:
  - `jpos`: ISO 8583 message parsing.
  - `jackson-dataformat-xml`: Robust ISO 20022 XML handling (`pain.001`, `pacs.002`).
  - `spring-cloud-starter-circuitbreaker-resilience4j`: Circuit Breaker implementation for fault tolerance.
  - `micrometer-tracing-bridge-brave`: Distributed tracing for observability.
  - `spring-retry`: For automatic retry logic on transient failures.
  - `spring-boot-starter-aop`: For implementing cross-cutting concerns like idempotency.
- **Unit Testing**: JUnit, Mockito, JaCoCo

---

## 🧱 Architecture Overview

```
[ Channels / Clients ]
          |
     REST / JSON
          |
[ Middleware Core ]
 | Validation & Enrichment
 | Routing & Orchestration
 | Idempotency (Interface-based, scalable)
 | Circuit Breaker (Resilience4j)
 | Distributed Tracing (Micrometer)
          |
   -------------------
   |                 |
[ Mock Core ]   [ Mock Payment Networks ]
                   | ISO 8583
                   | ISO 20022 (pain.001, pacs.002)
```

---

## 🚀 Implemented Production-Grade Features

### High-Volume Transaction Handling
- **Circuit Breakers**: Implemented with **Resilience4j**. If a downstream service fails repeatedly (threshold: 50%), the circuit opens for 5 seconds, returning a fast failure instead of waiting for timeouts.
- **Idempotency**: Implemented for state-changing operations (Fund Transfer) via an `X-Request-ID` header. The design uses an interface (`IdempotencyService`) to allow swapping the backend store (e.g., from in-memory to Redis) for horizontal scalability.
- **Retry Mechanism**: Uses `spring-retry` to automatically retry failed operations against transient issues.

### Security & Compliance Awareness
- **Payload Masking**: Sensitive data in logs (Account Numbers, Names) is automatically masked (e.g., `12******90`) to comply with PII/PCI-DSS standards.
- **Correlation & Trace IDs**: Achieved via Micrometer Tracing.

### Observability & Audit
- **End-to-End Transaction Tracing**: Logs automatically include a **Trace ID** and **Span ID**, enabling request tracing across a distributed system.
  - **Log Format**: `INFO [batavia,65b8e9f8e9f8e9f8,65b8e9f8e9f8e9f8] : Processing balance inquiry...`

---

## 🏁 Getting Started

### Prerequisites
- **Java 17**
- **Maven**

### Installation
1. **Clone the repository**:
   ```bash
   git clone https://github.com/pauluswi/batavia.git
   ```
2. **Build the project**:
   Use Maven to compile the source code and install dependencies.
   ```bash
   mvn clean install
   ```
3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
   The service will start on the default port `8080`.

---

## ⚙️ API Endpoints

### 1. Get Customer Balance
Processes a balance inquiry by transforming the JSON request into an ISO 8583 or ISO 20022 message.

- **Endpoint**: `POST /api/{protocol}/customer/balance`
  - `{protocol}` can be `8583` or `20022`.
- **Request Body**:
  ```json
  {
    "bankAccountNumber": "123456",
    "customerFullName": "Andi Lukito"
  }
  ```

### 2. Fund Transfer
Initiates a fund transfer using either ISO 8583 or ISO 20022 (`pain.001`). This endpoint is **idempotent** and protected by a **circuit breaker**.

- **Endpoint**: `POST /api/{protocol}/transfer`
  - `{protocol}` can be `8583` or `20022`.
- **Headers**:
  - `Content-Type: application/json`
  - `X-Request-ID: <unique-uuid>` (Required for Idempotency)
- **Request Body**:
  ```json
  {
    "sourceAccountNumber": "1234567890",
    "destinationAccountNumber": "0987654321",
    "amount": 100000,
    "currency": "IDR",
    "description": "Payment for Invoice"
  }
  ```
- **Success Response (200 OK)**:
  ```json
  {
    "responseCode": "00",
    "transactionId": "RRN123456",
    "message": "Transfer Successful"
  }
  ```

---

## ⚠️ Disclaimer

This project:
- Does not represent any specific bank or institution
- Uses mocked data and systems exclusively
- Is intended for **educational and portfolio showcase purposes only**

---

## 👤 Author

**Slamet Widodo (Wied)**  
Senior Software Engineer / Software Architect  
Banking Middleware • Payments • Integration • Architecture
