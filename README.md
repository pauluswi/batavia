# Batavia - Banking Middleware Service


[![Java Version](https://img.shields.io/badge/Java-17-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)


[![Build Java CI with Maven](https://github.com/pauluswi/batavia/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/pauluswi/batavia/actions/workflows/maven.yml)
[![License](https://img.shields.io/badge/license-MIT-green)](LICENSE)



## Overview
This Batavia repository hosts the codebase for service adapater as part of **Middleware Service for Banking Applications**. 
This service acts as a bridge between various banking systems, providing integration and data transformation to ensure seamless interoperability between legacy and modern banking infrastructures.

## Features
- **Transaction Processing**: Efficient and secure processing of various banking transactions.
- **Data Transformation**: Supports data transformation and mapping across multiple formats (e.g., JSON, ISO 8583, ISO 20022).
- **API Management**: Provides RESTful API endpoints for secure communication.
- **Logging**: Integrated with logging for high visibility and transactional tracking.

## Tech Stack
- **Language**: Java
- **Frameworks**: Spring Boot
- **Protocols**: REST, ISO 8583, ISO 20022
- **Libraries**: jpos for iso8583 parser and javax for iso20022 parser
- **Unit Test**: junit, mockito and jacoco

## Getting Started

### Prerequisites
- **Java 17**
- **Spring Boot 3.3.5**
- **Maven**

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

## Inbound and Outbound

- Inbound JSON   -->   Middleware   -->   Outbound ISO8583 | ISO20022

## API

1. **POST Customer Balance on ISO 8583 Format**:

   **Endpoint**: `/api/8583/customer/balance`

   **Request Body:**

   ```json
   {
     "bankAccountNumber": "123456",
     "customerFullName": "Andi Lukito"
   }
   ```

   **Response Body:**

   ```json
   {
     "responseCode": "00",
     "mti": "0210",
     "data": {
       "bankAccountNumber": "123456",
       "customerFullName": "Andi Lukito",
       "balance": 150000
     }
   }
   ```

   **ISO 8583 Request Message**:
   ```bash
      ISO 8583 Request Message: MTI: 0200, Field 3: 310000, Field 4: 000000000000, Field 7: 1111241830, Field 11: 123456, Field 41: 12345678, 
      Field 49: 360, Field 102: 123456, Field 103: Andi Lukito, 
   ```

   **ISO 8583 Response Message**:
   ```bash
     ISO 8583 Response Message: MTI: 0210, Field 3: 310000, Field 4: 000000000000, Field 7: 1111241830, Field 11: 123456, Field 39: 00, Field 
     41: 12345678, Field 49: 360, Field 54: 000000150000, Field 102: 123456, Field 103: Andi Lukito, 
   ```

2. **POST Customer Balance on ISO 20022 Format**:

   **Endpoint**: `/api/20022/customer/balance`

   **Request Body:**

   ```json
   {
     "bankAccountNumber": "123456",
     "customerFullName": "Andi Lukito"
   }
   ```

   **Response Body:**

   ```json
   {
     "responseCode": "00",
     "mti": "0210",
     "data": {
       "bankAccountNumber": "123456",
       "customerFullName": "Andi Lukito",
       "balance": 150000
     }
   }
   ```

   **ISO 20022 Request Message**:
   ```bash
      ISO 20022 Request Message: <?xml version="1.0" encoding="UTF-8"?><Document xmlns="urn:iso:std:iso:20022:tech:xsd:pain.001.001.03"><CstmrCdtTrfInitn><GrpHdr><MsgId>msg123456</MsgId><CreDtTm>2024-01-01T12:00:00</CreDtTm></GrpHdr><PmtInf><Dbtr><Nm>Andi Lukito</Nm></Dbtr><DbtrAcct><Id><Othr><Id>123456</Id></Othr></Id></DbtrAcct></PmtInf></CstmrCdtTrfInitn></Document> 
   ```

   **ISO 20022 Response Message**:
   ```bash
     ISO 20022 Response Message: <?xml version="1.0" encoding="UTF-8"?><Document xmlns="urn:iso:std:iso:20022:tech:xsd:pain.001.001.03"><CstmrCdtTrfInitn><GrpHdr><MsgId>msg123456</MsgId><CreDtTm>2024-01-01T12:00:00</CreDtTm></GrpHdr><PmtInf><DbtrAcct><Id><Othr><Id>123456</Id></Othr></Id></DbtrAcct></PmtInf><Bal><Amt Ccy="USD">1500.00</Amt></Bal><AcctInf><CIF>111</CIF><Name>Andi Lukito</Name></AcctInf></CstmrCdtTrfInitn></Document>
   ```

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for more details.


