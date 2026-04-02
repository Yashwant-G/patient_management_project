# 🏥 Patient Management System

A comprehensive microservices-based patient management platform built with Spring Boot, featuring real-time event processing, secure authentication, and scalable architecture.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Services](#services)
- [Tech Stack](#tech-stack)
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
┌─────────────────────────────────────────────────────────────────┐
│                         Client Applications                      │
└────────────────────────────┬────────────────────────────────────┘
                             │
                             ▼
                    ┌────────────────┐
                    │  API Gateway   │ (Port: 9000)
                    │  (WebFlux)     │
                    └────────┬───────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
   ┌─────────┐         ┌──────────┐         ┌─────────────┐
   │ Patient │         │  Auth    │         │ Appointment │
   │ Service │         │ Service  │         │  Service    │
   │(Port:   │         │(Port:    │         │(Port: 4006) │
   │ 4000)   │         │ 4005)    │         └─────────────┘
   └────┬────┘         └────┬─────┘              │
        │                   │                    │
        │    ┌──────────────┴────────────────┐   │
        │    │                               │   │
        ▼    ▼                               ▼   ▼
   ┌─────────────────────┐        ┌──────────────────┐
   │   Event Stream      │        │ Analytics Service│
   │     (Kafka)         │        │   (Consumer)     │
   └─────────────────────┘        └──────────────────┘
        │
        ▼
   ┌─────────────────────┐
   │ Billing Service     │
   │ (Event Consumer)    │
   └─────────────────────┘
```

---

## 🔧 Services

### 📱 **API Gateway**
**Port:** `9000`  
**Purpose:** Single entry point for all client requests  
**Features:**
- Request routing and load balancing
- Rate limiting and request throttling
- Response caching with Redis
- Reactive processing with Spring Cloud Gateway
- Security filter chains

**Technology Stack:**
- Spring Cloud Gateway (Webflux)
- Spring Data Redis Reactive
- Spring Boot 3.5.9

---

### 👤 **Auth Service**
**Port:** `4005`  
**Purpose:** Centralized authentication and authorization  
**Features:**
- User registration and login
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
- Patient CRUD operations
- Medical history tracking
- Event publication for updates
- gRPC endpoints for inter-service communication
- Redis caching for frequently accessed data
- Circuit breaker for fault tolerance
- Metrics collection with Prometheus

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
- Schedule appointments
- Appointment modification and cancellation
- Event consumption from Kafka
- Patient availability checking
- gRPC integration with other services

**Technology Stack:**
- Spring Boot 3.4.5
- Spring Data JPA
- PostgreSQL
- Kafka (Consumer/Producer)
- Protocol Buffers
- Jackson for JSON processing

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
- Bill generation and management
- Payment tracking
- Event-driven billing triggers
- Invoice generation
- gRPC communication with other services

**Technology Stack:**
- Spring Boot 3.5.8
- gRPC
- Kafka (Consumer)
- Protocol Buffers

---

### 📈 **Monitoring**
**Purpose:** System monitoring and metrics collection  
**Features:**
- Prometheus configuration
- Service health checks
- Performance metrics
- Custom application metrics

---

## 🛠️ Tech Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 21 |
| **Framework** | Spring Boot | 3.4.5 - 3.5.9 |
| **API Gateway** | Spring Cloud Gateway | 2025.0.1 |
| **Message Queue** | Apache Kafka | 7.5.0 |
| **RPC** | gRPC | 1.68.1 - 1.69.0 |
| **Serialization** | Protocol Buffers | 3.25.5 |
| **Authentication** | JWT (JJWT) | 0.12.6 |
| **Database** | PostgreSQL | Latest |
| **Cache** | Redis | Latest |
| **Monitoring** | Prometheus | Latest |
| **Resilience** | Resilience4j | 2.3.0 |
| **Container** | Docker | Latest |
| **Orchestration** | Docker Compose | 3.8 |

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

# Terminal 7 - Start Analytics Service (Optional)
cd analytics-service
mvn spring-boot:run

# Terminal 8 - Start Billing Service (Optional)
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
- **Appointment Service:** `http://localhost:4006/swagger-ui.html`

### Postman Collection

Import the provided `patient-management.postman_collection.json` into Postman for ready-to-use API requests.

```bash
# Open Postman and import:
File → Import → patient-management.postman_collection.json
```

### Example API Calls

**Authentication:**
```bash
# Login
curl -X POST http://localhost:4005/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"password"}'

# Response: JWT Token
{
  "token": "eyJhbGc...",
  "expires": 86400000
}
```

**Patient Operations:**
```bash
# Get all patients
curl -X GET http://localhost:4000/api/patients \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Create patient
curl -X POST http://localhost:4000/api/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"John Doe","email":"john@example.com"}'

# Get patient by ID
curl -X GET http://localhost:4000/api/patients/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Update patient
curl -X PUT http://localhost:4000/api/patients/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"name":"Jane Doe"}'

# Delete patient
curl -X DELETE http://localhost:4000/api/patients/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Appointment Operations:**
```bash
# Get all appointments
curl -X GET http://localhost:4006/api/appointments \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"

# Schedule appointment
curl -X POST http://localhost:4006/api/appointments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"patientId":1,"doctorId":1,"appointmentDate":"2026-04-15T10:00:00"}'
```

---

## 📊 Monitoring

### Prometheus Metrics

Access Prometheus metrics endpoints:

- **Patient Service Metrics:** `http://localhost:4000/actuator/prometheus`
- **Auth Service Metrics:** `http://localhost:4005/actuator/prometheus`
- **Appointment Service Metrics:** `http://localhost:4006/actuator/prometheus`

### Health Checks

- **Patient Service Health:** `http://localhost:4000/actuator/health`
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
- Kafka integration
- JWT authentication
- Docker containerization
- API documentation

---

**Last Updated:** April 2, 2026  
**Status:** 🟢 Active Development  
**Version:** 0.0.1-SNAPSHOT


