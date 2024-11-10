# Batavia - Banking Middleware Service


[![Java Version](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)

[![Build Java CI with Maven](https://github.com/pauluswi/batavia/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/pauluswi/batavia/actions/workflows/maven.yml)
[![Coverage](https://img.shields.io/badge/coverage-90%25-yellowgreen)](https://your-code-coverage-url)
![Coverage](https://codecov.io/gh/pauluswi/batavia/branch/main/graph/badge.svg)



## Overview
This Batavia repository hosts the codebase for a **Middleware Service for Banking Applications**. 
This service acts as a bridge between various banking systems, providing integration, data transformation, and transaction routing to ensure seamless interoperability between legacy and modern banking infrastructures.

Built with **Java** and designed to meet banking-specific standards, this middleware solution aims to provide high availability, scalability, and security for financial transactions and data exchanges.

## Features
- **Transaction Processing**: Efficient and secure processing of various banking transactions.
- **Data Transformation**: Supports data transformation and mapping across multiple formats (e.g., JSON, ISO 8583, ISO 20022).
- **API Management**: Provides RESTful API endpoints for secure communication.
- **Authentication and Authorization**: Ensures secure access through OAuth2, JWT, or other banking-grade security mechanisms.
- **Logging and Monitoring**: Integrated with logging and monitoring tools for high visibility and transactional tracking.
- **Error Handling**: Robust error handling with retries for failed transactions.

## Tech Stack
- **Language**: Java
- **Frameworks**: Spring Boot, Spring Security
- **Protocols**: REST, ISO 8583, ISO 20022
- **Database**: PostgreSQL 
- **Deployment**: Docker, Kubernetes

## Inbound and Outbound

- Inbound JSON   -->   Middleware   -->   Outbound ISO8583 | ISO20022

## Getting Started

### Prerequisites
- **Java 17**
- **Spring Boot 3.3.5**
- **Maven**
- **Docker**

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/pauluswi/batavia.git
   ```
2. **Install dependencies: Use Maven to install the required dependencies.**:
   ```bash
   mvn install
   ```
3. **Set up environment variables**:
   - **Configure the database and other environment variables in** application.properties
     
4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```


## Usage
The middleware service exposes various endpoints for transaction processing, data retrieval, and more. Refer to the [API Documentation](./docs/api-documentation.md) for details on each endpoint.

## Contributing
We welcome contributions! Please see the [CONTRIBUTING.md](./CONTRIBUTING.md) for guidelines.

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for more details.

## Contact
For more information, questions, or collaboration opportunities, please reach out via GitHub Issues or contact me at [your-email@example.com](mailto:your-email@example.com).

