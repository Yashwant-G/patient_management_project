# 🏥 Patient Management System

A comprehensive microservices-based patient management platform built with Spring Boot, featuring real-time event processing, secure authentication, and scalable architecture.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
- [Ports and URLs](#ports-and-urls)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Monitoring](#monitoring)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Overview

The Patient Management System is an enterprise-grade microservices platform designed to streamline patient data management, appointments, billing, and analytics. The system follows distributed architecture principles with independent services communicating through APIs and event streams.

**Key Features:**
- ✅ Multi-service architecture with API Gateway
- ✅ Asynchronous event processing with Kafka
- ✅ JWT-based authentication and authorization
- ✅ Real-time analytics and monitoring
- ✅ gRPC for inter-service communication
- ✅ Containerized deployment with Docker
- ✅ Prometheus metrics and monitoring
- ✅ Redis caching and session management
- ✅ Resilience4j circuit breaker patterns

---

## 🏗️ Architecture

```
                     ┌──────────────────────────────────────┐
                     │         Client Applications           │
                     └────────────────┬─────────────────────┘
                                      │ HTTP Requests
                                      ▼
                          ┌───────────────────────────┐
                          │        API Gateway         │
                          │        (Port: 9000)        │
                          │   JWT Validation Filter    │
                          └────────┬─────────┬─────────┘
                                   │         │
              ┌────────────────────┘         └──────────────────────┐
              │                                                       │
              ▼                                                       ▼
   ┌──────────────────────┐                          ┌───────────────────────────┐
   │     Auth Service     │                          │      Patient Service       │
   │     (Port: 4005)     │                          │      (Port: 4000)          │
   │  - User registration │                          │  - Spring Actuator (metrics│
   │  - JWT generate      │                          │    exposed to Prometheus)  │
   │  - JWT validate      │                          │  - Redis cache (1 endpoint)│
   │  - PostgreSQL        │                          │  - PostgreSQL              │
   └──────────────────────┘                          └────┬──────┬───────┬───────┘
                                                          │      │       │
                           ┌──────────────────────────────┘      │       └──────────────────────┐
                           │  Kafka (Protobuf)                    │ gRPC (Protobuf)               │ Kafka (Protobuf)
                           ▼                                      ▼                               ▼
              ┌────────────────────────┐              ┌────────────────────┐     ┌──────────────────────────┐
              │    Analytics Service   │              │   Billing Service  │     │   Appointment Service     │
              │  - Kafka consumer      │              │  - gRPC server     │     │   (Port: 4006)            │
              │  - Patient event stats │              │  - Bill generation │     │  - Kafka consumer         │
              │  - PostgreSQL          │              │  - PostgreSQL      │     │  - Syncs cached_patient   │
              └────────────────────────┘              └────────────────────┘     │    table from Kafka event │
                                                                                 │  - PostgreSQL             │
                                                                                 └────────────┬─────────────┘
                                                                                              │ gRPC (Protobuf)
                                                                                              ▼
                                                                                 ┌────────────────────────────┐
                                                                                 │       AI Service           │
                                                                                 │      (Port: 4007)          │
                                                                                 │   gRPC Server: 9002        │
                                                                                 │   Gemini 2.5 Flash         │
                                                                                 │   (Google GenAI)           │
                                                                                 │   - PostgreSQL             │
                                                                                 └────────────────────────────┘

──────────────────────────────────────────────────────────────────────────────────────────────────────────────
                               Infrastructure (Dockerized via Docker Compose)
──────────────────────────────────────────────────────────────────────────────────────────────────────────────

   ┌──────────────┐   ┌───────────────────┐   ┌────────────────────┐   ┌────────────┐   ┌───────────┐
   │  PostgreSQL  │   │       Redis        │   │    Apache Kafka    │   │ Prometheus │   │  Grafana  │
   │ (per service │   │  (Patient Service  │   │  + Zookeeper       │   │ (scrapes   │   │(dashboard │
   │  own DB)     │   │   one endpoint)    │   │  (event streaming) │   │ actuator)  │   │& alerts)  │
   └──────────────┘   └───────────────────┘   └────────────────────┘   └────────────┘   └───────────┘
```

### Data Flow Summary

| Flow | Source | Protocol | Target | Purpose |
|------|--------|----------|--------|---------|
| Patient events | Patient Service | **Kafka** (Protobuf) | Analytics Service | Real-time analytics |
| Patient events | Patient Service | **Kafka** (Protobuf) | Appointment Service | Sync `cached_patient` table |
| Billing trigger | Patient Service | **gRPC** (Protobuf) | Billing Service | Trigger bill generation |
| Appointment AI query | Appointment Service | **gRPC** (Protobuf) | AI Service | Doctor/patient AI queries |
| Auth validation | Auth Service | **REST** (JWT) | API Gateway filter | Route-level JWT validation |
| Metrics scrape | Patient Service | **HTTP** (Actuator) | Prometheus → Grafana | Observability |

---

## 🔧 Services

### 📱 **API Gateway**
**Port:** `9000`
**Purpose:** Single entry point for all client requests
**Features:**
- Request routing to downstream services
- **JWT Validation Filter** — validates Bearer tokens on every request before forwarding (token issued by Auth Service)
- Reactive processing with Spring Cloud Gateway (WebFlux)
- Rate limiting and request throttling

**Technology Stack:**
- Spring Cloud Gateway (WebFlux)
- Spring Boot 3.5.9

---

### 👤 **Auth Service**
**Port:** `4005`  
**Purpose:** Centralized authentication and authorization  
**Features:**
- User registration (`/signup`) and login
- JWT token generation and validation
- Role-based access control (RBAC)
- Password encryption and security
- OpenAPI/Swagger documentation

**Technology Stack:**
- Spring Security
- JJWT (JSON Web Tokens)
- Spring Data JPA
- PostgreSQL/H2 Database
- Springdoc OpenAPI

---

### 🏥 **Patient Service**
**Port:** `4000`  
**Purpose:** Core patient data management  
**Features:**
- Patient CRUD operations and medical history tracking
- Publishes patient events to Kafka (consumed by Analytics and Appointment Services) using Protobuf serialization
- Calls Billing Service directly via **gRPC** (Protobuf) to trigger billing on patient events
- **Redis cache** on one specific read endpoint for low-latency responses
- **Spring Actuator** endpoints exposed for Prometheus scraping (`/actuator/prometheus`, `/actuator/health`)
- Circuit breaker for fault tolerance (Resilience4j)

**Technology Stack:**
- Spring Boot 3.5.8
- Spring Data JPA
- PostgreSQL
- Kafka (Producer)
- gRPC
- Redis
- Resilience4j
- Micrometer Prometheus

---

### 📅 **Appointment Service**
**Port:** `4006`
**Purpose:** Appointment scheduling and management
**Features:**
- Schedule, modify, and cancel appointments
- Consumes patient events from Kafka to maintain a local `cached_patient` table (keeps in sync with the Patient Service's main patient table)
- Forwards appointment/doctor queries to AI Service via gRPC for AI-assisted responses
- Protocol Buffers for all inter-service communication
- PostgreSQL for appointments and cached patient data

**Technology Stack:**
- Spring Boot 3.4.5
- Spring Data JPA
- PostgreSQL (appointments + `cached_patient` table)
- Kafka (Consumer — patient events)
- gRPC Client (→ AI Service)
- Protocol Buffers

---

### 🤖 **AI Service**
**Port:** `4007` | **gRPC Port:** `9002`
**Purpose:** AI-powered medical query assistance using Google Gemini
**Features:**
- Receives appointment/doctor queries via gRPC from Appointment Service
- Processes queries using Google Gemini 2.5 Flash model
- Low-temperature (0.2) responses for consistent medical output
- Protocol Buffers for all gRPC communication
- PostgreSQL for query/response persistence

**Technology Stack:**
- Spring Boot
- gRPC Server
- Spring AI (Google GenAI / Gemini 2.5 Flash)
- Protocol Buffers
- PostgreSQL

---

### 👨‍⚕️ **Doctor Service**
**Port:** `4008`
**Purpose:** Doctor information and management
**Features:**
- Doctor profile search by name (full-text search)
- Retrieve full doctor details by ID (qualifications, specialization, contact)
- Get minimal doctor information (quick lookups)
- Validate doctor existence
- PostgreSQL for doctor records
- OpenAPI/Swagger documentation

**Endpoints:**
- `GET /doctors/search?name={name}` — Search doctors by name
- `GET /doctors/{id}` — Get complete doctor details
- `GET /doctors/{id}/minimal` — Get minimal doctor info
- `GET /doctors/{id}/exists` — Check if doctor exists

**Technology Stack:**
- Spring Boot 3.5.8
- Spring Data JPA
- PostgreSQL
- Springdoc OpenAPI

---

### 📊 **Analytics Service**
**Purpose:** Real-time analytics and insights  
**Features:**
- Consumes events from Kafka
- Generates analytics reports
- Patient statistics and trends
- Appointment analytics
- Event-driven architecture

**Technology Stack:**
- Spring Boot 3.5.8
- Kafka (Consumer)
- Protocol Buffers
- Event streaming

---

### 💰 **Billing Service**
**Purpose:** Billing and payment processing
**Features:**
- Receives billing triggers from Patient Service via **gRPC** (Protobuf)
- Bill generation and invoice management
- Payment tracking
- PostgreSQL for billing records

**Technology Stack:**
- Spring Boot 3.5.8
- gRPC Server (receives calls from Patient Service)
- Protocol Buffers
- PostgreSQL

---

### 📈 **Monitoring (Prometheus + Grafana)**
**Purpose:** System observability and metrics dashboards
**Features:**
- **Prometheus** scrapes `/actuator/prometheus` on Patient Service (and other services with Actuator enabled)
- **Grafana** connects to Prometheus as a data source for dashboards and alerting
- Spring Actuator health, info, and metrics endpoints
- Configuration in `monitoring/prometheus.yml`

**Access:**
- Prometheus UI: `http://localhost:9090`
- Grafana Dashboard: `http://localhost:3000` (default credentials: `admin / admin`)

---

## 🛠️ Tech Stack

| Component | Technology | Version / Notes |
|-----------|-----------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.4.5 - 3.5.9 |
| **API Gateway** | Spring Cloud Gateway (WebFlux) | 2025.0.1 |
| **Message Queue** | Apache Kafka | 7.5.0 |
| **RPC** | gRPC | 1.68.1 - 1.69.0 |
| **Serialization** | Protocol Buffers | 3.25.5 |
| **Authentication** | JWT (JJWT) | 0.12.6 |
| **AI Model** | Google Gemini 2.5 Flash | via Spring AI (Google GenAI) |
| **Database** | PostgreSQL | Each service has its own DB |
| **Cache** | Redis | Patient Service (1 endpoint) |
| **Metrics** | Prometheus + Grafana | Scrapes Actuator endpoints |
| **Resilience** | Resilience4j | 2.3.0 |
| **Container** | Docker + Docker Compose | Compose v3.8 |

---

## 🔌 Ports and URLs

For a comprehensive reference of all service ports, gRPC ports, database URLs, Docker internal hosts, and Swagger documentation endpoints, see the dedicated **[PortsAndUrl.md](PortsAndUrl.md)** documentation.

**Quick Reference:**
- **API Gateway:** `http://localhost:9000`
- **Auth Service:** `http://localhost:4005`
- **Patient Service:** `http://localhost:4000`
- **Appointment Service:** `http://localhost:4006`
- **Doctor Service:** `http://localhost:4008` (gRPC: 9003)
- **AI Service:** `http://localhost:4007` (gRPC: 9002)
- **Billing Service:** Port 4001 (gRPC: 9001)
- **Payment Service:** Port 4009 (gRPC: 9004)
- **Prometheus:** `http://localhost:9090`
- **Grafana:** `http://localhost:3000`

---

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK)** - Version 21 or higher
  ```bash
  java -version
  ```

- **Apache Maven** - Version 3.6.0 or higher
  ```bash
  mvn -version
  ```

- **Docker** - For containerized deployment
  ```bash
  docker --version
  ```

- **Docker Compose** - For orchestrating multi-container applications
  ```bash
  docker-compose --version
  ```

- **PostgreSQL** (Optional) - For local database development
  ```bash
  psql --version
  ```

---

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

This will start all services along with Kafka, Zookeeper, and other dependencies.

```bash
# Navigate to project root
cd patient-management-project

# Build all services
mvn clean install -DskipTests

# Start all services with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f

# Verify services are running
docker-compose ps
```

**Service URLs:**
- API Gateway: `http://localhost:9000`
- Patient Service: `http://localhost:4000`
- Auth Service: `http://localhost:4005`
- Appointment Service: `http://localhost:4006`
- Doctor Service: `http://localhost:4008`
- AI Service: `http://localhost:4007` | gRPC: `localhost:9002`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

### Option 2: Run Services Individually

```bash
# Build all modules
mvn clean install -DskipTests

# Terminal 1 - Start Zookeeper
docker run -d --name zookeeper -e ZOOKEEPER_CLIENT_PORT=2181 confluentinc/cp-zookeeper:7.5.0

# Terminal 2 - Start Kafka
docker run -d --name kafka -e KAFKA_BROKER_ID=1 -e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://kafka:9092 \
  --link zookeeper confluentinc/cp-kafka:7.5.0

# Terminal 3 - Start Auth Service
cd auth-service
mvn spring-boot:run

# Terminal 4 - Start Patient Service
cd patient-service
mvn spring-boot:run

# Terminal 5 - Start Appointment Service
cd appointment-service
mvn spring-boot:run

# Terminal 6 - Start API Gateway
cd api-gateway
mvn spring-boot:run

# Terminal 7 - Start Doctor Service
cd doctor-service
mvn spring-boot:run

# Terminal 8 - Start Analytics Service (Optional)
cd analytics-service
mvn spring-boot:run

# Terminal 9 - Start Billing Service (Optional)
cd billing-service
mvn spring-boot:run
```

### Verify Installation

```bash
# Check API Gateway
curl http://localhost:9000/health

# Check Patient Service
curl http://localhost:4000/swagger-ui.html

# Check Auth Service
curl http://localhost:4005/swagger-ui.html

# Check Doctor Service
curl http://localhost:4008/swagger-ui.html
```

---

## ⚙️ Configuration

### Environment Variables

Create a `.env` file in the project root:

```env
# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=kafka:9092

# Database Configuration
DB_URL=jdbc:postgresql://postgres:5432/patient_db
DB_USERNAME=postgres
DB_PASSWORD=your_password

# Redis Configuration
REDIS_HOST=redis
REDIS_PORT=6379

# JWT Configuration
JWT_SECRET=your_secret_key_here
JWT_EXPIRATION=86400000

# Service Ports
API_GATEWAY_PORT=9000
AUTH_SERVICE_PORT=4005
PATIENT_SERVICE_PORT=4000
APPOINTMENT_SERVICE_PORT=4006
DOCTOR_SERVICE_PORT=4008

# Spring Profiles
SPRING_PROFILES_ACTIVE=dev
```

### Application Properties

Each service has its own `application.properties` or `application.yml`:

**Patient Service** (`patient-service/src/main/resources/application.properties`):
```properties
server.port=4000
spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=jdbc:postgresql://localhost:5432/patient_db
spring.datasource.username=postgres
spring.datasource.password=password
spring.kafka.bootstrap-servers=localhost:9092
```

**Auth Service** (`auth-service/src/main/resources/application.properties`):
```properties
server.port=4005
spring.security.user.name=admin
spring.security.user.password=admin
```

---

## 📚 API Documentation

All services include **Swagger/OpenAPI** documentation:

### Access API Docs

- **Patient Service Swagger UI:** `http://localhost:4000/swagger-ui.html`
- **Auth Service Swagger UI:** `http://localhost:4005/swagger-ui.html`
- **Appointment Service Swagger UI:** `http://localhost:4006/swagger-ui.html`
- **Doctor Service Swagger UI:** `http://localhost:4008/swagger-ui.html`

### Postman Collection

Import the provided `patient-management.postman_collection.json` into Postman for ready-to-use API requests covering all services (Auth, Patient, Appointment, and Doctor APIs with both Gateway and Direct Service Access options).

**Collection Features:**
- ✅ SignUp endpoint for user registration
- ✅ Doctor API endpoints (search, get details, minimal info, existence check)
- ✅ Gateway routes (port 9000) for all services
- ✅ Direct service access (ports 4000, 4005, 4006, 4008) for testing
- ✅ Pre-configured collection variables (baseUrl, authEmail, bearerToken, etc.)
- ✅ Test scripts for automatic token management

```bash
# Open Postman and import:
File → Import → patient-management.postman_collection.json
```

### Example API Calls

**Authentication:**
```bash
# Sign Up
curl -X POST http://localhost:9000/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"newuser@test.com","password":"password123"}'

# Login
curl -X POST http://localhost:9000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"testuser@test.com","password":"password123"}'

# Response: JWT Token
{
  "token": "eyJhbGc..."
}
```

**Patient Operations:**
```bash
# Get all patients
curl -X GET http://localhost:9000/api/patients \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create patient
curl -X POST http://localhost:9000/api/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"John Doe","email":"john@example.com","address":"123 Main St","dateOfBirth":"1990-01-01","registeredDate":"2026-05-10"}'

# Update patient
curl -X PUT http://localhost:9000/api/patients/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"Jane Doe","email":"jane@example.com","address":"456 Oak Ave","dateOfBirth":"1990-01-01"}'

# Delete patient
curl -X DELETE http://localhost:9000/api/patients/123e4567-e89b-12d3-a456-426614174000 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Appointment Operations:**
```bash
# Get appointments by date range
curl -X GET "http://localhost:9000/api/appointments?from=2026-05-01T00:00:00&to=2026-05-31T23:59:59" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create appointment via AI (natural language)
curl -X POST http://localhost:9000/api/appointments/ai-add/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: text/plain" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d "I have an appointment with Dr. Smith on 2026-05-15 at 14:00 for a routine checkup"

# Book appointment via saga flow
curl -X POST http://localhost:9000/api/appointments/book \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"requestId":"123e4567-e89b-12d3-a456-426614174100","patientId":"123e4567-e89b-12d3-a456-426614174000","doctorId":"123e4567-e89b-12d3-a456-426614174001","appointment_date":"2026-05-20","startTime":"10:00:00","endTime":"10:30:00","reason":"Follow-up","paymentMethod":"UPI","version":0}'
```

**Doctor Operations:**
```bash
# Search doctors by name
curl -X GET "http://localhost:9000/api/doctors/search?name=Smith" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get full doctor details by ID
curl -X GET http://localhost:9000/api/doctors/123e4567-e89b-12d3-a456-426614174001 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Get minimal doctor information
curl -X GET http://localhost:9000/api/doctors/123e4567-e89b-12d3-a456-426614174001/minimal \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Check if doctor exists
curl -X GET http://localhost:9000/api/doctors/123e4567-e89b-12d3-a456-426614174001/exists \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 📊 Monitoring

### Actuator Endpoints (Patient Service — primary metrics source)

| Endpoint | URL |
|----------|-----|
| Prometheus metrics | `http://localhost:4000/actuator/prometheus` |
| Health check | `http://localhost:4000/actuator/health` |
| Info | `http://localhost:4000/actuator/info` |

### Prometheus & Grafana

- **Prometheus UI:** `http://localhost:9090` — query scraped metrics
- **Grafana Dashboard:** `http://localhost:3000` — visualization and alerting (default login: `admin / admin`)
- Prometheus is configured to scrape Patient Service's `/actuator/prometheus` endpoint
- Grafana connects to Prometheus as a data source

### Other Service Health Checks

- **Auth Service Health:** `http://localhost:4005/actuator/health`
- **Appointment Service Health:** `http://localhost:4006/actuator/health`

### Prometheus Configuration

The `monitoring/prometheus.yml` file contains Prometheus scrape configurations for all services.

---

## 🧪 Testing

### Run Unit Tests

```bash
# Run tests for all modules
mvn test

# Run tests for specific module
cd patient-service
mvn test

# Run tests with coverage
mvn test jacoco:report
```

### Integration Tests

```bash
# Run integration tests
mvn verify

# Skip unit tests and run only integration tests
mvn verify -DskipUnitTests
```

---

## 📦 Build & Deployment

### Building Docker Images

```bash
# Build all services
docker-compose build

# Build specific service
docker build -t patient-service:latest ./patient-service

# View images
docker images | grep patient
```

### Pushing to Registry

```bash
# Tag images
docker tag patient-service:latest your-registry/patient-service:latest

# Push to registry
docker push your-registry/patient-service:latest
```

### Kubernetes Deployment (Optional)

Create `k8s-deployment.yaml` files for each service to deploy on Kubernetes.

---

## 🐛 Troubleshooting

### Common Issues

**Issue: Connection refused to Kafka**
```bash
# Solution: Ensure Kafka and Zookeeper are running
docker-compose ps | grep kafka
docker-compose logs kafka
```

**Issue: Database connection errors**
```bash
# Solution: Check database configuration
docker-compose logs postgres
# Verify connection string in application.properties
```

**Issue: Port already in use**
```bash
# Solution: Kill process using the port
lsof -i :9000  # Check port 9000
kill -9 <PID>  # Kill process

# Or change port in configuration
```

**Issue: JWT token validation fails**
```bash
# Solution: Ensure JWT secret matches across services
# Check application.properties for jwt.secret configuration
```

### Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f patient-service
docker-compose logs -f api-gateway

# View recent logs
docker-compose logs --tail=50 patient-service
```

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit your changes** (`git commit -m 'Add amazing feature'`)
4. **Push to the branch** (`git push origin feature/amazing-feature`)
5. **Open a Pull Request**

### Development Guidelines

- Follow Java/Spring Boot best practices
- Write unit tests for new features
- Update documentation as needed
- Use meaningful commit messages
- Ensure code passes all tests before pushing

### Code Style

- Use consistent indentation (4 spaces)
- Follow naming conventions (camelCase for variables, PascalCase for classes)
- Add comments for complex logic
- Keep methods small and focused

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 📞 Support & Contact

For questions, issues, or suggestions:

- **Issues:** [GitHub Issues](./issues)
- **Email:** support@patientmanagement.dev
- **Documentation:** [Wiki](./wiki)

---

## 🙏 Acknowledgments

- Spring Boot community for excellent framework
- Apache Kafka for event streaming
- gRPC for efficient RPC communication
- Prometheus for monitoring capabilities

---

## 📝 Changelog

### v0.0.1 (Current)
- Initial project setup
- Core services implementation
  - Auth Service (login, JWT validation)
  - Patient Service (CRUD operations with caching)
  - Appointment Service (scheduling with AI integration)
  - **Doctor Service (NEW)** — Doctor management and search
  - Analytics Service (event streaming analytics)
  - Billing Service (bill generation via gRPC)
  - AI Service (Gemini-powered query assistance)
- **Auth API Enhancements (NEW)** — SignUp endpoint for user registration
- **Doctor API Endpoints (NEW)**:
  - Search doctor by name (full-text search)
  - Get doctor details by ID
  - Get minimal doctor information
  - Check doctor existence validation
- Kafka integration for event streaming
- gRPC inter-service communication with comprehensive exception handling
- JWT authentication and authorization
- Docker containerization and Docker Compose orchestration
- API documentation with Swagger/OpenAPI
- Postman collection with comprehensive API test cases
- Prometheus metrics and Grafana dashboards
- **Enhancements v0.0.2 (May 13, 2026)**:
  - ✅ **gRPC Exception Handling** — Added StatusRuntimeException handling to AiServiceGrpcClient, DoctorServiceGrpcClient, PaymentServiceGrpcClient
  - ✅ **Enhanced Logging** — Added operational logs at key stages: request received, gRPC call start/end, DB save/update, payment success/failure, saga step transitions, exception handling
  - ✅ **PortsAndUrl.md Documentation** — Comprehensive reference for all service ports, gRPC endpoints, database URLs, Docker internal hosts, and monitoring URLs
  - ✅ **Improved Error Messages** — Better downstream service unavailability detection and reporting
  - ✅ **Code Quality** — Production-oriented exception handling and detailed operational logging throughout the codebase

---

**Last Updated:** May 13, 2026  
**Status:** 🟢 Active Development  
**Version:** 0.0.1-SNAPSHOT
