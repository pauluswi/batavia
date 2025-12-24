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
- **Idempotent Operations**: Prevents duplicate transaction processing by tracking a unique `Idempotency-Key` provided in the request header.
- **Retry Mechanism**: Automatically retries failed operations (e.g., due to network issues) to improve reliability.
- **RESTful API**: Provides clear and secure RESTful API endpoints for all operations.
- **Comprehensive Logging**: Integrated with SLF4J for high visibility and transactional tracking.

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3.3.5
- **Protocols**: REST, ISO 8583, ISO 20022
- **Libraries**:
  - `jpos`: ISO 8583 message parsing.
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

### 1. Get Customer Balance (ISO 8583)
Processes a balance inquiry by transforming the JSON request into an ISO 8583 message.

- **Endpoint**: `POST /api/8583/customer/balance`
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

- **Internal ISO 8583 Messages**:
  - **Request**: `MTI: 0200, Field 3: 310000, Field 11: 123456, Field 102: 123456, ...`
  - **Response**: `MTI: 0210, Field 39: 00, Field 54: 000000150000, ...`

---

### 2. Get Customer Balance (ISO 20022)
Processes a balance inquiry by transforming the JSON request into an ISO 20022 XML message.

- **Endpoint**: `POST /api/20022/customer/balance`
- **Headers**:
  - `Content-Type: application/json`
  - `Idempotency-Key: <unique-request-id>` (e.g., `uuid-5678-efgh`)

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
    "mti": "msg123456",
    "data": {
      "bankAccountNumber": "123456",
      "customerFullName": "123456",
      "balance": 1500.0
    }
  }
  ```

- **Internal ISO 20022 Messages**:
  - **Request**: `<?xml ...><PmtInf><DbtrAcct><Id><Othr><Id>123456</Id>...`
  - **Response**: `<?xml ...><Bal><Amt Ccy="USD">1500.00</Amt></Bal>...`

---

### Idempotency
To prevent duplicate transactions, most (but not all) `POST` requests support an `Idempotency-Key` header. If a request with the same key is received more than once, the server will return a cached response without re-processing the transaction.

- **Example Duplicate Request Response**:
  ```json
  {
    "responseCode": "94",
    "mti": null,
    "data": null
  }
  ```

### Retry Mechanism
The service uses `spring-retry` to automatically retry failed operations. This helps improve resilience against transient issues like network timeouts or temporary service unavailability.

- **Configuration**: The `@Retryable` annotation is used on service methods.
- **Default Behavior**:
  - **Max Attempts**: 3
  - **Backoff Delay**: 1000ms (1 second) between retries.
- **Trigger**: Retries are triggered by specific exceptions, such as `ISOException` or other `Exception` subclasses, depending on the service.

### Logging and Monitoring
The application uses **SLF4J** for logging. Key events, such as incoming requests, outgoing messages, and errors, are logged to provide visibility into the application's behavior.

- **Log Format**: Logs typically include the timestamp, log level, logger name, and a descriptive message.
- **Monitoring**: For a production environment, it is recommended to:
  - Integrate with a centralized logging platform (e.g., ELK Stack, Splunk).
  - Use Spring Boot Actuator for health checks and application metrics.

### Error Code Table
The following table lists the common response codes returned by the API:

| Code | Message                | Description                                                                 |
|------|------------------------|-----------------------------------------------------------------------------|
| `00` | Success                | The transaction was processed successfully.                                 |
| `14` | Invalid Request        | The request was malformed or contained invalid data.                        |
| `68` | Request Timed Out      | The request to a downstream system timed out.                               |
| `94` | Duplicate Transaction  | A request with the same `Idempotency-Key` has already been processed.       |
| `96` | System Error           | An unexpected internal error occurred. Check logs for more details.         |


## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for more details.
