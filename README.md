# Batavia - Production-Inspired Banking Middleware

[![Java Version](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Build Java CI with Maven](https://github.com/pauluswi/batavia/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/pauluswi/batavia/actions/workflows/maven.yml)
[![codecov](https://codecov.io/gh/pauluswi/batavia/graph/badge.svg?token=63S7RVDOUR)](https://codecov.io/gh/pauluswi/batavia)
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

## 🧱 Architecture

For a detailed look into the logical components, runtime transaction flows, and physical deployment models, please see our complete **[Architecture Guide](./ARCHITECTURE.md)**.

---

## ☁️ Cloud Deployment & Networking

For a detailed guide on deploying this application to a cloud environment like AWS and handling specific networking challenges, please see:

- **[AWS Cloud Deployment Guide](./CLOUD_DEPLOYMENT.md)**: Recommended architecture using ECS Fargate, CI/CD pipelines, and security best practices.
- **[ISO 8583 Networking Guide](./ISO8583_NETWORK.md)**: Strategies for handling single, persistent TCP connections required by legacy banking switches.

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

## 🚀 Implemented Production-Grade Features

### High-Volume Transaction Handling
- **Circuit Breakers**: Implemented with **Resilience4j**. If a downstream service fails repeatedly, the circuit opens, preventing the middleware from waiting on a failing service and providing an immediate fallback response.
- **Idempotency**: Prevents duplicate processing of state-changing operations (Fund Transfer) via an `X-Request-ID` header, which is crucial in timeout and retry scenarios.
- **Retry Mechanism**: Uses `spring-retry` to automatically retry operations against transient, short-lived failures, avoiding unnecessary error responses to the client.

### Latency and Timeout Management
- **Fail-Fast with Circuit Breakers**: The primary mechanism to manage latency is the "fail-fast" behavior of the circuit breaker. Instead of letting a request hang for a slow downstream service, the breaker opens and returns an immediate error, protecting system resources.
- **Asynchronous Internal Processing**: For long-running processes (like ISO 8583 transactions over a slow link), the recommended architecture in the **[ISO 8583 Networking Guide](./ISO8583_NETWORK.md)** uses message queues. This allows the API to quickly accept a request and respond later via a webhook or polling, preventing long-held HTTP connections.
- **Configurable Timeouts**: While not explicitly configured in this demo, a production setup would involve setting timeouts at multiple levels:
  - **HTTP Client**: For calls to other microservices.
  - **Resilience4j TimeLimiter**: To wrap any long-running call in a timeout decorator.
  - **Database**: Connection and query timeouts.

### Security & Compliance Awareness
- **Payload Masking**: Sensitive data in logs (Account Numbers, Names) is automatically masked (e.g., `12******90`) to comply with PII/PCI-DSS standards.
- **Correlation & Trace IDs**: Achieved via Micrometer Tracing.

### Observability & Audit
- **End-to-End Transaction Tracing**: Logs automatically include a **Trace ID** and **Span ID**, enabling request tracing across a distributed system. This is critical for pinpointing which service in a chain is introducing latency.
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
