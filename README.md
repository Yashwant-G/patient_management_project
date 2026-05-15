<div align="center">

# 🏥 Patient Management System

[![Typing SVG](https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=22&duration=2800&pause=900&color=1B75D0&width=900&lines=Cloud-ready+Healthcare+Microservices;Saga-Orchestrated+Appointment+Booking;Kafka+%2B+gRPC+%2B+Spring+Cloud;AI-Assisted+Appointment+Workflows)](https://git.io/typing-svg)

<br/>

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=spring-boot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Gateway-00A98F?style=for-the-badge)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Driven-231F20?style=for-the-badge&logo=apache-kafka)
![gRPC](https://img.shields.io/badge/gRPC-Protobuf-244C5A?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Service%20DBs-4169E1?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-Cache%20%2B%20Rate%20Limit-DC382D?style=for-the-badge&logo=redis)
![AI](https://img.shields.io/badge/Spring%20AI-Gemini%202.5-8E44AD?style=for-the-badge)

**A production-grade healthcare microservices platform** combining Spring Boot, Kafka, gRPC, distributed saga orchestration, and AI-powered workflows.

</div>

---

## 📋 Quick Navigation

| 📖 | 🏗️ | 🚀 | 📚 | ⚙️ |
|---|---|---|---|---|
| [Overview](#-overview) | [Architecture](#-system-architecture) | [Services](#-service-landscape) | [Workflows](#-workflow-details) | [Tech Stack](#-technology-stack) |

---

## 🎯 Overview

**Patient Management System** is a modern healthcare backend platform demonstrating enterprise-grade distributed system patterns. It showcases:

<table>
<tr>
<td width="50%">

### ✨ Key Features
- 🔐 JWT-based authentication & role-based access
- 📦 Event-driven microservices architecture
- 🎭 Saga orchestration for complex workflows
- 🤖 AI-powered appointment parsing (Gemini 2.5)
- 💾 Multi-database strategy per service
- ⚡ Redis caching & rate limiting
- 📊 Prometheus metrics & actuator health
- 🐳 Docker-ready deployment

</td>
<td width="50%">

### 🎓 Learning Highlights
- Service-oriented architecture patterns
- Event sourcing & async messaging
- gRPC inter-service communication
- Distributed transaction handling
- API gateway security & routing
- Database-per-service isolation
- Observability & monitoring setup

</td>
</tr>
</table>

---

## 📐 System Architecture

### 🌐 Overall System Flow

```text
                                      ┌──────────────────────────────────────┐
                                      │         Client Applications           │
                                      └────────────────┬─────────────────────┘
                                                       │ HTTP Requests
                                                       ▼
                                           ┌───────────────────────────┐
                                           │        API Gateway         │
                                           │        (Port: 9000)        │
                                           │  JWT Security + Rate Limit │
                                           │  Spring Cloud Gateway      │
                                           └───────┬─────────┬─────────┘
                                                   │         │
                         ┌─────────────────────────┘         └─────────────────────────┐
                         │                                                             │
                         ▼                                                             ▼
              ┌──────────────────────┐                                  ┌───────────────────────────┐
              │     Auth Service     │                                  │      Patient Service       │
              │     (Port: 4005)     │                                  │      (Port: 4000)          │
              │  - User signup       │                                  │  - Patient CRUD            │
              │  - Login             │                                  │  - Pagination + Search     │
              │  - JWT generation    │                                  │  - Redis cached listing    │
              │  - JWT validation    │                                  │  - Actuator metrics        │
              │  - PostgreSQL        │                                  │  - PostgreSQL              │
              └──────────────────────┘                                  └──────┬────────┬───────────┘
                                                                                │        │
                                              ┌─────────────────────────────────┘        └─────────────────────────────┐
                                              │ Kafka Protobuf                                      gRPC Protobuf      │
                                              ▼                                                       ▼                 │
                                 ┌──────────────────────────┐                            ┌──────────────────────┐      │
                                 │    Analytics Service     │                            │   Billing Service    │      │
                                 │  - Kafka consumer        │                            │   (Port: 4001)       │      │
                                 │  - Patient event stream  │                            │   gRPC Server: 9001  │      │
                                 │  - Reporting extension   │                            │  - Billing account   │      │
                                 └──────────────────────────┘                            └──────────────────────┘      │
                                                                                                                        │ Kafka Protobuf
                                                                                                                        ▼
                                                                                                           ┌───────────────────────────┐
                                                                                                           │   Appointment Service     │
                                                                                                           │   (Port: 4006)            │
                                                                                                           │  - Appointment queries    │
                                                                                                           │  - Saga orchestration     │
                                                                                                           │  - Kafka consumer         │
                                                                                                           │  - cached_patient sync    │
                                                                                                           │  - PostgreSQL             │
                                                                                                           └───────┬─────────┬─────────┘
                                                                                                                   │         │
                                                                                   ┌───────────────────────────────┘         └───────────────────────────────┐
                                                                                   │ gRPC Protobuf                                           gRPC Protobuf   │
                                                                                   ▼                                                            ▼            │
                                                                        ┌──────────────────────────┐                              ┌──────────────────────────┐ │
                                                                        │      Doctor Service      │                              │     Payment Service      │ │
                                                                        │      (Port: 4008)        │                              │      (Port: 4009)        │ │
                                                                        │   gRPC Server: 9003      │                              │   gRPC Server: 9004      │ │
                                                                        │  - Doctor search         │                              │  - Mock payment          │ │
                                                                        │  - Slot availability     │                              │  - Transaction response  │ │
                                                                        │  - Slot status update    │                              │  - PostgreSQL            │ │
                                                                        │  - PostgreSQL            │                              └──────────────────────────┘ │
                                                                        └──────────────────────────┘                                                           │
                                                                                                                                                                │
                                                                                                                                                                │ gRPC Protobuf
                                                                                                                                                                ▼
                                                                                                                                                   ┌──────────────────────────┐
                                                                                                                                                   │       AI Service         │
                                                                                                                                                   │      (Port: 4007)        │
                                                                                                                                                   │   gRPC Server: 9002      │
                                                                                                                                                   │  - Spring AI             │
                                                                                                                                                   │  - Gemini 2.5 Flash      │
                                                                                                                                                   │  - Appointment parsing   │
                                                                                                                                                   └──────────────────────────┘

────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────
                                                        Infrastructure / Platform Dependencies
────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────────

     ┌──────────────────┐      ┌──────────────────┐      ┌──────────────────────┐      ┌────────────────────┐      ┌────────────────────┐
     │    PostgreSQL    │      │      Redis       │      │    Apache Kafka      │      │    Prometheus      │      │      Docker        │
     │  Per-service DBs │      │ Cache + Gateway  │      │  Protobuf events     │      │ Actuator metrics   │      │ Per-service images │
     │                  │      │ rate limiting    │      │  patient_topic       │      │ Patient Service    │      │ Dockerfiles exist  │
     └──────────────────┘      └──────────────────┘      └──────────────────────┘      └────────────────────┘      └────────────────────┘
```


### Overall Flow from Patient Service

```text
                                      ┌──────────────────────────────────────┐
                                      │          Client / API Gateway         │
                                      └────────────────┬─────────────────────┘
                                                       │ REST
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │           Patient Service             │
                                      │             (Port: 4000)              │
                                      │  - Create patient                     │
                                      │  - Update patient                     │
                                      │  - Delete patient                     │
                                      │  - Search / sort / paginate patients  │
                                      │  - Validate duplicate email           │
                                      └───────┬───────────────┬──────────────┘
                                              │               │
                         ┌────────────────────┘               └────────────────────┐
                         │                                                         │
                         ▼                                                         ▼
              ┌──────────────────────┐                                ┌────────────────────────┐
              │      PostgreSQL      │                                │         Redis          │
              │   patient-service DB │                                │  Cached patient pages  │
              │  - Patient records   │                                │  TTL: 10 minutes       │
              └──────────────────────┘                                └────────────────────────┘
                         │
                         │ After create / update
                         ▼
              ┌─────────────────────────────────────────────────────────────────────┐
              │                           Kafka                                      │
              │  Topics: patient_topic, patient_updated                             │
              │  Payload: PatientEvent Protobuf                                     │
              └───────────────┬─────────────────────────────────────┬───────────────┘
                              │                                     │
                              ▼                                     ▼
              ┌──────────────────────────────┐        ┌──────────────────────────────┐
              │     Appointment Service      │        │      Analytics Service       │
              │  - Kafka consumer            │        │  - Kafka consumer            │
              │  - Sync cached_patient table │        │  - Event analytics hook      │
              │  - Appointment patient lookup│        │  - Reporting extension point │
              └──────────────────────────────┘        └──────────────────────────────┘

              ┌─────────────────────────────────────────────────────────────────────┐
              │ Patient creation also triggers Billing Service through gRPC          │
              └────────────────────────────────────┬────────────────────────────────┘
                                                   │ gRPC Protobuf
                                                   ▼
                                      ┌──────────────────────────────┐
                                      │        Billing Service        │
                                      │        gRPC Port: 9001        │
                                      │  - createBillingAccount()     │
                                      │  - Returns account status     │
                                      └──────────────────────────────┘
```


### Appointment Service Saga Orchestrator Flow

```text
                                      ┌──────────────────────────────────────┐
                                      │          Client / API Gateway         │
                                      └────────────────┬─────────────────────┘
                                                       │ POST /appointments/book
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │        Appointment Service            │
                                      │             (Port: 4006)              │
                                      │  SagaOrchestratorService              │
                                      └────────────────┬─────────────────────┘
                                                       │
                                                       │ 1. Validate cached patient
                                                       │ 2. Check duplicate requestId
                                                       │ 3. Save appointment as PENDING
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │          Appointment DB               │
                                      │  - Appointment row                    │
                                      │  - sagaId                             │
                                      │  - PENDING status                     │
                                      └────────────────┬─────────────────────┘
                                                       │
                                                       │ gRPC: checkSlotAvailability()
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │            Doctor Service             │
                                      │          gRPC Server: 9003            │
                                      │  - Validate doctor slot               │
                                      │  - Return slotId and fees             │
                                      └────────────────┬─────────────────────┘
                                                       │
                                                       │ Appointment status -> PAYMENT_PENDING
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │        Appointment Service            │
                                      │  - Set amount                         │
                                      │  - Set slotId                         │
                                      │  - Send payment request               │
                                      └────────────────┬─────────────────────┘
                                                       │ gRPC: paymentEventSend()
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │           Payment Service             │
                                      │          gRPC Server: 9004            │
                                      │  - Mock payment processing            │
                                      │  - Generate txnId                     │
                                      │  - Return SUCCESS / FAILURE           │
                                      └────────────────┬─────────────────────┘
                                                       │
                         ┌─────────────────────────────┴─────────────────────────────┐
                         │                                                           │
                         ▼                                                           ▼
          ┌────────────────────────────────┐                       ┌────────────────────────────────┐
          │        Payment SUCCESS          │                       │        Payment FAILURE          │
          │  - Update doctor slot Success   │                       │  - Update doctor slot Failure   │
          │  - Appointment CONFIRMED        │                       │  - Appointment CANCELLED        │
          │  - Return success response      │                       │  - Return failed response       │
          └────────────────────────────────┘                       └────────────────────────────────┘
```


### Appointment Service AI Appointment Book Flow

```text
                                      ┌──────────────────────────────────────┐
                                      │          Client / API Gateway         │
                                      └────────────────┬─────────────────────┘
                                                       │ POST /appointments/ai-add/{patientId}
                                                       │ Plain text appointment request
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │        Appointment Service            │
                                      │             (Port: 4006)              │
                                      │  - Receive plain text                 │
                                      │  - Load cached patient context        │
                                      │  - Add patient id and patient name    │
                                      └────────────────┬─────────────────────┘
                                                       │
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │        cached_patient Table           │
                                      │  - Patient id                         │
                                      │  - Patient full name                  │
                                      │  - Patient email                      │
                                      └────────────────┬─────────────────────┘
                                                       │
                                                       │ gRPC: parseAppointment(text)
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │             AI Service                │
                                      │      (Port: 4007, gRPC: 9002)         │
                                      │  - Build appointment prompt           │
                                      │  - Call Gemini through Spring AI      │
                                      │  - Parse JSON response                │
                                      │  - Return Protobuf response           │
                                      └────────────────┬─────────────────────┘
                                                       │
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │          Gemini 2.5 Flash             │
                                      │  - Extract doctor name                │
                                      │  - Extract appointment date/time      │
                                      │  - Extract reason                     │
                                      │  - Return structured JSON             │
                                      └────────────────┬─────────────────────┘
                                                       │ Structured appointment response
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │        Appointment Service            │
                                      │  - Receive doctorName from AI         │
                                      │  - Search Doctor Service              │
                                      └────────────────┬─────────────────────┘
                                                       │ REST: /doctors/search?name=
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │            Doctor Service             │
                                      │             (Port: 4008)              │
                                      │  - Match doctor by name               │
                                      │  - Return doctorId and fullName       │
                                      └────────────────┬─────────────────────┘
                                                       │
                                                       ▼
                                      ┌──────────────────────────────────────┐
                                      │          Appointment DB               │
                                      │  - Save AI-created appointment        │
                                      │  - Return appointment response        │
                                      └──────────────────────────────────────┘
```


---

## 🏗️ Service Landscape

<table>
<thead>
<tr>
<th>🎯 Service</th>
<th>🌐 Port</th>
<th>💾 Storage</th>
<th>📡 Protocol</th>
<th>📝 Role</th>
</tr>
</thead>
<tbody>
<tr>
<td><strong>API Gateway</strong></td>
<td><code>9000</code></td>
<td>Redis</td>
<td>HTTP</td>
<td>Central entry point & rate limiting</td>
</tr>
<tr>
<td><strong>Auth Service</strong></td>
<td><code>4005</code></td>
<td>PostgreSQL</td>
<td>REST</td>
<td>Identity & JWT tokens</td>
</tr>
<tr>
<td><strong>Patient Service</strong></td>
<td><code>4000</code></td>
<td>PostgreSQL + Redis</td>
<td>REST + Kafka + gRPC</td>
<td>Patient domain ownership</td>
</tr>
<tr>
<td><strong>Appointment Service</strong></td>
<td><code>4006</code></td>
<td>PostgreSQL</td>
<td>REST + Kafka + gRPC</td>
<td>Appointment orchestration</td>
</tr>
<tr>
<td><strong>Doctor Service</strong></td>
<td><code>4008</code> / <code>9003</code> gRPC</td>
<td>PostgreSQL</td>
<td>REST + gRPC</td>
<td>Doctor data & slots</td>
</tr>
<tr>
<td><strong>AI Service</strong></td>
<td><code>4007</code> / <code>9002</code> gRPC</td>
<td>-</td>
<td>gRPC</td>
<td>AI appointment parsing</td>
</tr>
<tr>
<td><strong>Payment Service</strong></td>
<td><code>4009</code> / <code>9004</code> gRPC</td>
<td>PostgreSQL</td>
<td>gRPC</td>
<td>Mock payment processor</td>
</tr>
<tr>
<td><strong>Billing Service</strong></td>
<td><code>4001</code> / <code>9001</code> gRPC</td>
<td>-</td>
<td>gRPC</td>
<td>Billing account mgmt</td>
</tr>
<tr>
<td><strong>Analytics Service</strong></td>
<td><code>4002</code></td>
<td>-</td>
<td>Kafka</td>
<td>Event analytics & reporting</td>
</tr>
</tbody>
</table>

---

## 🚀 Service Use Cases & Workflows

### 🔐 Auth Service
```
Signup/Login Request → Validate Credentials → Generate JWT → Bearer Token for Gateway
```
- User registration & login
- JWT token generation & validation
- Role-based identity model

---

### 👥 Patient Service
```
Patient CRUD → PostgreSQL → Kafka Event → Appointment Cache + Analytics
```
- ✅ Create, update, delete, search patients
- 🔍 Paginated listing with Redis caching
- 📤 Event publishing for sync
- 💳 Billing integration via gRPC

---

### 📅 Appointment Service
```
Booking Request → Saga Orchestration → Doctor Slot Check → Payment → Confirmation
```
**Saga Workflow Steps:**
1. Create appointment (PENDING)
2. Check doctor slot availability
3. Request payment
4. Confirm on success / Cancel on failure

**AI Workflow:**
1. Receive plain text appointment request
2. Load cached patient context
3. Call AI to parse appointment details
4. Resolve doctor name to ID
5. Save AI-created appointment

---

### 👨‍⚕️ Doctor Service
```
Doctor Query → Repository Lookup → Doctor DTO
                ↓
         Appointment Saga → gRPC Slot Check → Slot Status Update
```
- Doctor search & lookup
- Slot availability checks
- Slot status management

---

### 🤖 AI Service
```
Plain Text → Prompt Builder → Gemini 2.5 Flash → JSON Parse → Protobuf Response
```
- AI-powered appointment parsing
- Natural language processing
- Structured data extraction

---

---

## 🔄 Workflow Details

### 📊 Event-Driven Patient Synchronization

| # | Source | Protocol | Target | Purpose |
|---|--------|----------|--------|---------|
| 1 | Patient Service | REST | PostgreSQL | Persist data |
| 2 | Patient Service | Kafka Protobuf | `patient_topic` | Publish event |
| 3 | Patient Service | Kafka Protobuf | `patient_updated` | Update event |
| 4 | Kafka Consumer | Async | Appointment Service | Sync cache |
| 5 | Kafka Consumer | Async | Analytics Service | Process stream |
| 6 | Patient Service | gRPC Protobuf | Billing Service | Create account |

### 🎭 Saga-Based Appointment Booking

| Step | Action | State |
|------|--------|-------|
| 1️⃣ | Appointment requested | `PENDING` |
| 2️⃣ | Slot availability checked | Slot & fee returned |
| 3️⃣ | Payment requested | `PAYMENT_PENDING` |
| 4️⃣ | Payment succeeds | `CONFIRMED` + slot marked Success |
| 5️⃣ | Payment fails | `CANCELLED` + slot marked Failure |

### 🧠 AI-Assisted Appointment Creation

| Step | Action | Detail |
|------|--------|--------|
| 1️⃣ | Text received | Patient context added |
| 2️⃣ | AI call sent | gRPC to AI Service |
| 3️⃣ | Gemini processes | JSON appointment details |
| 4️⃣ | Response mapped | Protobuf DTO |
| 5️⃣ | Doctor resolved | Name → ID lookup |
| 6️⃣ | Saved | AI appointment persisted |

---

## 🛠️ Technology Stack

<table>
<tr>
<td width="50%">

### 🎯 Backend
- **Language**: Java 21
- **Framework**: Spring Boot 3.x
- **Gateway**: Spring Cloud Gateway WebFlux
- **Security**: Spring Security + OAuth2 Resource Server
- **Auth**: JWT + JJWT

</td>
<td width="50%">

### 🔌 Integration
- **Database**: PostgreSQL (per-service)
- **Cache**: Redis
- **Messaging**: Apache Kafka
- **RPC**: gRPC + Protocol Buffers
- **AI**: Spring AI + Gemini 2.5 Flash

</td>
</tr>
<tr>
<td>

### 📊 Observability
- **Metrics**: Spring Actuator + Micrometer Prometheus
- **Health**: Actuator health endpoints
- **Monitoring**: Prometheus integration

</td>
<td>

### 🚀 DevOps
- **Build**: Maven wrappers (per-service)
- **Container**: Docker per service
- **Protocols**: REST + gRPC + Kafka

</td>
</tr>
</table>

---

## 🌐 Ports & URLs

| Service | HTTP | gRPC | 📚 Swagger |
|---------|------|------|-----------|
| **API Gateway** | `9000` | - | - |
| **Auth Service** | `4005` | - | http://localhost:4005/swagger-ui.html |
| **Patient Service** | `4000` | - | http://localhost:4000/swagger-ui.html |
| **Appointment Service** | `4006` | - | http://localhost:4006/swagger-ui.html |
| **Doctor Service** | `4008` | `9003` | http://localhost:4008/swagger-ui.html |
| **AI Service** | `4007` | `9002` | - |
| **Billing Service** | `4001` | `9001` | - |
| **Payment Service** | `4009` | `9004` | - |
| **Analytics Service** | `4002` | - | - |

> 💡 For detailed port information, see [PortsAndUrl.md](PortsAndUrl.md)

---

## 🚦 API Gateway Routes

| Gateway Path | Target Service | Purpose |
|---|---|---|
| `/auth/**` | `http://auth-service:4005` | Auth endpoints |
| `/api/patients/**` | `http://patient-service:4000` | Patient operations |
| `/api/appointments/**` | `http://appointment-service:4006` | Appointment operations |
| `/api/doctors/**` | `http://doctor-service:4008` | Doctor operations |
| `/api-docs/auth` | Auth Service OpenAPI | Auth documentation |
| `/api-docs/patients` | Patient Service OpenAPI | Patient documentation |
| `/api-docs/appointments` | Appointment Service OpenAPI | Appointment documentation |
| `/api-docs/doctors` | Doctor Service OpenAPI | Doctor documentation |

---

## 📚 API Documentation

### 🔐 Auth Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/signup` | 📝 Register new user |
| `POST` | `/login` | 🔓 Generate JWT token |
| `GET` | `/validate` | ✅ Validate bearer token |

### 👥 Patient Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/patients?page=1&size=10&sort=asc&sortField=name&searchValue=` | 📄 List patients (paginated + searchable) |
| `POST` | `/patients` | ➕ Create patient |
| `PUT` | `/patients/{id}` | ✏️ Update patient |
| `DELETE` | `/patients/{id}` | 🗑️ Delete patient |

### 📅 Appointment Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/appointments?from=YYYY-MM-DD&to=YYYY-MM-DD` | 📊 Get appointments by date range |
| `POST` | `/appointments/book` | 📋 Book appointment (Saga flow) |
| `POST` | `/appointments/ai-add/{patientId}` | 🤖 Create from AI-parsed text |

### 👨‍⚕️ Doctor Service

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/doctors/search?name={name}` | 🔍 Search doctor by name |
| `GET` | `/doctors/{id}` | 📋 Get doctor details |
| `GET` | `/doctors/{id}/minimal` | ⚡ Get minimal doctor info |
| `GET` | `/doctors/{id}/exists` | ✅ Check doctor existence |

> 📮 **Postman Collection**: Import [patient-management.postman_collection.json](patient-management.postman_collection.json)

---

## 📁 Repository Structure

```
📦 Patient Management System
├── 🌐 api-gateway/
├── 🔐 auth-service/
├── 👥 patient-service/
├── 📅 appointment-service/
├── 👨‍⚕️ doctor-service/
├── 🤖 ai-service/
├── 💳 payment-service/
├── 📦 billing-service/
├── 📊 analytics-service/
├── 📈 monitoring/
│   └── prometheus.yml
├── 📮 patient-management.postman_collection.json
├── 📋 PortsAndUrl.md
├── 📋 README_OLD.md
└── 📋 Readme.md
```

Each service is an **independent Maven project** with:
- ✅ Dedicated `pom.xml`
- ✅ Maven wrapper scripts
- ✅ Source tree (`src/main` & `src/test`)
- ✅ Resource configuration
- ✅ Protobuf definitions (where applicable)
- ✅ Dockerfile for containerization

---

## ⚙️ Configuration Guide

Most services support **environment variable overrides**:

```properties
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:5432/db
SPRING_DATASOURCE_USERNAME=admin_user
SPRING_DATASOURCE_PASSWORD=password

# Database Schema Management
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_SQL_INIT_MODE=always

# Messaging
SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Security
JWT_SECRET=<your-jwt-secret>

# AI Integration
GEMINI_API_KEY=<your-gemini-key>

# Cache
REDIS_HOST=redis
REDIS_PORT=6379
```

### 🔌 gRPC Client Settings

```properties
DOCTOR_SERVICE_ADDRESS=localhost
DOCTOR_SERVICE_GRPC_PORT=9003

PAYMENT_SERVICE_ADDRESS=localhost
PAYMENT_SERVICE_GRPC_PORT=9004

AI_SERVICE_ADDRESS=localhost
AI_SERVICE_GRPC_PORT=9002
```

> ⚠️ **Security Note**: Keep `JWT_SECRET` and `GEMINI_API_KEY` in environment variables or a secret manager!

---

## 📊 Observability & Monitoring

### 🏥 Health Endpoints

```
http://localhost:4000/actuator/health          # Patient Service
http://localhost:4006/actuator/health          # Appointment Service
```

### 📈 Prometheus Metrics

```
http://localhost:4000/actuator/prometheus      # Patient Service metrics
http://localhost:4006/actuator/prometheus      # Appointment Service metrics
```

**Configuration**: [monitoring/prometheus.yml](monitoring/prometheus.yml)

---

## 📝 Project Notes

- ✅ **Service-based architecture**: Independent Maven projects (not multi-module)
- ✅ **Containerization**: Dockerfiles in each service directory
- ✅ **Protocol Buffers**: Service-level `src/main/proto` directories
- ✅ **Payment Service**: Mock implementation for Saga workflow testing
- ✅ **Billing Service**: gRPC integration point for billing accounts
- ✅ **Analytics Service**: Event consumer ready for reporting extensions
- ✅ **API Gateway Security**: JWT validation + role-based authorization via Spring Security
- ℹ️ **Note**: Root `docker-compose.yml` not included; configure as needed

---

<div align="center">

### 🎓 Built to Demonstrate

✨ Microservices Architecture | 🔄 Event-Driven Design | 🎭 Saga Orchestration | 🚀 Cloud-Ready Platform

</div>
