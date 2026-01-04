# Batavia - Banking Middleware Service

[![Java Version](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Build Java CI with Maven](https://github.com/pauluswi/batavia/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/pauluswi/batavia/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

## Overview
This Batavia repository hosts the codebase for a service adapter as part of a **Middleware Service for Banking Applications**. This service acts as a bridge between various banking systems, providing integration and data transformation to ensure seamless interoperability between legacy and modern banking infrastructures.

## Core Features
- **Protocol-Based Routing**: Dynamically routes incoming requests to the appropriate backend service based on the specified protocol (`ISO8583` or `ISO20022`).
- **Data Transformation**: Supports data transformation and mapping across multiple formats (e.g., JSON to ISO 8583, JSON to ISO 20022).
- **Idempotent Operations**: Prevents duplicate transaction processing by tracking a unique `X-Request-ID` provided in the request header.
- **Resilience & Fault Tolerance**:
  - **Circuit Breakers**: Uses Resilience4j to prevent cascading failures when downstream systems are unresponsive.
  - **Retry Mechanism**: Automatically retries failed operations (e.g., due to network issues) to improve reliability.
- **Security & Compliance**:
  - **Data Masking**: Automatically masks sensitive fields (PAN, Account Numbers, Names) in logs to comply with PII/PCI-DSS standards.
- **Observability**:
  - **Distributed Tracing**: Integrated with Micrometer Tracing (Brave) to provide Trace ID and Span ID in logs for end-to-end request tracking.
- **RESTful API**: Provides clear and secure RESTful API endpoints for all operations.

## High-Level Architecture

    Client (Mobile / Channel)
      |
      v
    REST API Gateway (JSON)
      |
      v
    Middleware Core
    - Validation
    - Idempotency (Interface-based, scalable)
    - Routing
    - Circuit Breaker (Resilience4j)
      |
      +--> ISO8583 Adapter → Switch / Core Banking
      |
      +--> ISO20022 Adapter → Payment Engine

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.3.5
- **Protocols**: REST, ISO 8583, ISO 20022
- **Libraries**:
  - `jpos`: ISO 8583 message parsing.
  - `jackson-dataformat-xml`: Robust ISO 20022 XML handling.
  - `spring-cloud-starter-circuitbreaker-resilience4j`: Circuit Breaker implementation.
  - `micrometer-tracing-bridge-brave`: Distributed tracing.
  - `spring-retry`: For automatic retry logic.
  - `spring-boot-starter-aop`: For implementing idempotency.
- **Unit Testing**: JUnit, Mockito, JaCoCo

## Getting Started

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

## API Endpoints

### Inbound and Outbound Flow
- **Inbound**: JSON (via REST API)
- **Middleware**: Transforms the request and routes it based on the protocol.
- **Outbound**: ISO 8583 or ISO 20022

---

### 1. Get Customer Balance
Processes a balance inquiry by transforming the JSON request into an ISO 8583 or ISO 20022 message.

- **Endpoint**: `POST /api/{protocol}/customer/balance`
  - `{protocol}` can be `8583` or `20022`.
- **Headers**:
  - `Content-Type: application/json`
  
- **Request Body**:
  ```json
  {
    "bankAccountNumber": "123456",
    "customerFullName": "Andi Lukito"
  }
  ```

- **Success Response (200 OK)**:
  ```json
  {
    "responseCode": "00",
    "mti": "0210",
    "data": {
      "bankAccountNumber": "123456",
      "customerFullName": "Andi Lukito",
      "balance": 150000.0
    }
  }
  ```

---

### 2. Fund Transfer
Initiates a fund transfer between two accounts using either ISO 8583 or ISO 20022 (`pain.001`). This endpoint is idempotent and protected by a circuit breaker.

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

### Idempotency
To prevent duplicate transactions, state-changing operations (like Fund Transfer) require an `X-Request-ID` header. If a request with the same ID is received more than once, the server will return a cached response or a duplicate transaction error.

- **Example Duplicate Request Response**:
  ```json
  {
    "responseCode": "94",
    "mti": null,
    "data": null
  }
  ```

### Resilience & Reliability
- **Circuit Breaker**: If the downstream system fails repeatedly (threshold: 50%), the circuit opens for 5 seconds, returning a fast failure (`SYSTEM_ERROR`) instead of waiting for timeouts.
- **Retry**: Transient failures (e.g., network blips) trigger automatic retries (max 3 attempts, 1s delay).

### Logging and Observability
- **Data Masking**: Sensitive data (Account Numbers, Names) is masked in logs (e.g., `12******90`).
- **Distributed Tracing**: Logs include `[traceId, spanId]` to trace requests across microservices.

### Error Code Table
The following table lists the common response codes returned by the API:

| Code | Message                | Description                                                                 |
|------|------------------------|-----------------------------------------------------------------------------|
| `00` | Success                | The transaction was processed successfully.                                 |
| `14` | Invalid Request        | The request was malformed or contained invalid data.                        |
| `68` | Request Timed Out      | The request to a downstream system timed out.                               |
| `94` | Duplicate Transaction  | A request with the same `X-Request-ID` has already been processed.          |
| `96` | System Error           | An unexpected internal error occurred. Check logs for more details.         |

## ⚠️ Disclaimer

- This project is a portfolio showcase only.
- It does not represent any real bank system or proprietary implementation.
- Business rules and data structures are simplified and anonymized to respect NDA obligations.

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for more details.
