Indonesian Banking Middleware

Production-Inspired Architecture Showcase

A production-style banking middleware that demonstrates how modern digital channels integrate with core banking systems and payment networks using ISO 8583 and ISO 20022, designed for high-volume, 24/7 financial operations.

📌 Purpose of This Project

This repository is a showcase project that demonstrates real-world banking middleware design and engineering practices, inspired by production experience in a regulated banking environment.

All external dependencies such as core banking systems, payment switches, and networks are mocked, while preserving:

Realistic transaction flows

Architectural decisions

Failure handling strategies

Compliance-aware design

This project is not a simulator of a specific bank, but a transferable reference architecture.

🏗️ What This Middleware Solves
✔ Connects Multiple Channels to Core Banking

Mobile & web banking

Partner APIs

Internal services

Channels interact only with a clean REST/JSON API, never directly with core systems.

✔ Standardizes Communication

REST / JSON for channels

ISO 8583 for legacy payment networks

ISO 20022 for modern real-time payments

All protocol complexity is isolated inside the middleware.

✔ Handles High-Volume Transactions Safely

Designed for sustained high TPS

Built for correctness, traceability, and stability

Financial safety prioritized over raw throughput

🧱 Architecture Overview
[ Channels / Clients ]
|
REST / JSON
|
[ API Gateway Layer ]
|
[ Middleware Core ]
| Validation & Enrichment
| Routing & Orchestration
| Idempotency & Audit
|
   -------------------
|                 |
[ Mock Core ]   [ Mock Payment Networks ]
| ISO 8583
| ISO 20022 (BI-FAST)

🔑 Key Characteristics

Stateless API layer

Transaction-aware processing

Horizontal scalability

Designed for 24/7 availability

Production-grade failure handling

🔄 Transaction Flow (Simplified)
JSON Request
↓
Validation & Enrichment
↓
Protocol Mapping (ISO 8583 / ISO 20022)
↓
Mock Switch / Network
↓
Response Parsing
↓
Normalized JSON Response

💳 ISO 8583 Integration (Mocked)

This project demonstrates production-style ISO 8583 handling, including:

Financial transaction MTIs (0200 / 0210)

Field mapping and validation

MTI lifecycle management

Reversal handling (0400)

Timeout and retry strategies

Switch abstraction layer

The actual switch is mocked, but message construction, parsing, and lifecycle handling reflect real-world patterns.

🌐 ISO 20022 Integration (BI-FAST Inspired, Mocked)

ISO 20022 integration is modeled after BI-FAST, Indonesia’s real-time retail payment system.

Covered concepts:

pain.xxx (payment initiation)

pacs.xxx (interbank processing)

camt.xxx (status & reporting)

Key design focus:

Message mapping & enrichment

Schema validation (mocked)

Coexistence with ISO 8583

Real-time processing constraints

This demonstrates readiness for ISO 20022-driven environments, including European payment systems.

🏦 Core Banking Connectivity (Mocked)

The mocked core banking module demonstrates:

Synchronous vs Asynchronous Calls

Inquiries handled synchronously

Postings may be synchronous or deferred

Transaction Boundaries

Clear separation between channel request and core commit

No implicit posting

ACID Awareness

Atomic and consistent posting simulation

Isolation of concurrent transactions

Durable transaction state tracking

Posting vs Inquiry

Read-only inquiries

Balance-impacting postings

Cut-Off Time & End-of-Day Handling

Configurable cut-off windows

EOD constraints simulated

Deferred processing where applicable

🚀 High-Volume Transaction Handling

This project is designed with scale and resilience in mind:

Horizontal scaling (stateless services)

Connection pooling

Circuit breakers

Back-pressure management

Idempotency keys

Dead-letter queues (DLQ)

Real-World Issues Modeled

Duplicate requests

Partial failures

Network latency

Reversal scenarios

🔐 Security & Compliance Awareness

While simplified, the design reflects banking-grade principles:

TLS-first communication

Payload masking for sensitive fields

Structured audit logging

Correlation & trace IDs

Role-based access concepts

📊 Observability & Audit

End-to-end transaction tracing

Deterministic transaction states

Structured logs for audit & reconciliation

Clear error classification

🧪 Mocking Strategy

To ensure safety and portability:

Core banking system is fully mocked

Payment switches are mocked

No real financial systems are connected

No real customer data is used

This allows the project to focus on architecture and engineering quality.

🌍 Transferability to European Banking

Although inspired by Indonesian banking systems:

ISO standards are global

Design principles are universal

Architecture aligns with European banking expectations

ISO 20022 readiness is emphasized

This makes the project relevant for SEPA, Instant Payments, and PSD2-style ecosystems.

⚠️ Disclaimer

This project:

Does not represent any specific bank or institution

Uses mocked data and systems exclusively

Is intended for educational and portfolio showcase purposes only

👤 Author

Paulus Slamet Widodo (Wied)
Senior Software Engineer / Engineering Manager
Banking Middleware • Payments • Integration • Architecture