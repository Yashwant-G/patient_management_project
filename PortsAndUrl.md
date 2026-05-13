# Ports and URL Reference

This document lists service ports and URLs based on current project configuration (`application.properties` / `application.yml` / `application.yaml`, Dockerfiles, and gateway routes).

| Service Name | HTTP Server Port | gRPC Port | Database Connection URL | Docker Base URL / Internal Docker Host URL | Swagger/OpenAPI URL |
|---|---:|---:|---|---|---|
| API Gateway | `9000` | `-` | `-` | `http://api-gateway:9000` | `-` |
| Auth Service | `4005` | `-` | `jdbc:postgresql://auth-service-db:5432/db` | `http://auth-service:4005` | `http://localhost:4005/swagger-ui.html` |
| Patient Service | `4000` | `-` | `jdbc:postgresql://patient-service-db:5432/db` | `http://patient-service:4000` | `http://localhost:4000/swagger-ui.html` |
| Appointment Service | `4006` | `-` | `jdbc:postgresql://appointment-service-db:5432/db` | `http://appointment-service:4006` | `http://localhost:4006/swagger-ui.html` |
| Doctor Service | `4008` | `9003` | `jdbc:postgresql://doctor-service-db:5432/db` | `http://doctor-service:4008` | `http://localhost:4008/swagger-ui.html` |
| AI Service | `4007` | `9002` | `Not configured in checked-in application config` | `http://ai-service:4007` | `-` |
| Billing Service | `4001` | `9001` | `Not configured in checked-in application config` | `http://billing-service:4001` | `-` |
| Payment Service | `4009` | `9004` | `jdbc:postgresql://payment-service-db:5432/db` | `http://payment-service:4009` | `-` |
| Analytics Service | `4002` (Dockerfile expose) | `-` | `Not configured in checked-in application config` | `http://analytics-service:4002` | `-` |

## API Gateway Routes

| Gateway Path | Downstream Service URL |
|---|---|
| `/auth/**` | `http://auth-service:4005` |
| `/api/patients/**` | `http://patient-service:4000` |
| `/api/appointments/**` | `http://appointment-service:4006` |
| `/api/doctors/**` | `http://doctor-service:4008` |
| `/api-docs/auth` | `http://auth-service:4005/v3/api-docs` |
| `/api-docs/patients` | `http://patient-service:4000/v3/api-docs` |
| `/api-docs/appointments` | `http://appointment-service:4006/v3/api-docs` |
| `/api-docs/doctors` | `http://doctor-service:4008/v3/api-docs` |

## gRPC Client-to-Server Mapping

| Client Service | Server Service | Target Host | gRPC Port |
|---|---|---|---:|
| Patient Service | Billing Service | `billing.service.address` (default `localhost`) | `9001` |
| Appointment Service | Doctor Service | `doctor.service.address` (default `localhost`) | `9003` |
| Appointment Service | Payment Service | `payment.service.address` (default `localhost`) | `9004` |
| Appointment Service | AI Service | `ai.service.address` (default `localhost`) | `9002` |
