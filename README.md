# OpenTelemetry Quarkus Example

[![CI](https://img.shields.io/github/actions/workflow/status/devops-thiago/otel-example-quarkus/ci.yml?branch=main&label=CI)](https://github.com/devops-thiago/otel-example-quarkus/actions)
[![Java Version](https://img.shields.io/badge/java-21-007396?logo=openjdk)](https://openjdk.org)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.17.5-4695EB?logo=quarkus)](https://quarkus.io)
[![License](https://img.shields.io/github/license/devops-thiago/otel-example-quarkus)](LICENSE)
[![Codecov](https://img.shields.io/codecov/c/github/devops-thiago/otel-example-quarkus?label=coverage)](https://app.codecov.io/gh/devops-thiago/otel-example-quarkus)
[![Sonar Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=devops-thiago_otel-example-quarkus&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=devops-thiago_otel-example-quarkus)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=devops-thiago_otel-example-quarkus&metric=coverage)](https://sonarcloud.io/summary/new_code?id=devops-thiago_otel-example-quarkus)
[![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-enabled-blue?logo=opentelemetry)](https://opentelemetry.io)
[![Docker](https://img.shields.io/badge/Docker-ready-blue?logo=docker)](https://www.docker.com)
[![Docker Hub](https://img.shields.io/docker/v/thiagosg/otel-crud-api-quarkus?logo=docker&label=Docker%20Hub)](https://hub.docker.com/r/thiagosg/otel-crud-api-quarkus)
[![Docker Pulls](https://img.shields.io/docker/pulls/thiagosg/otel-crud-api-quarkus)](https://hub.docker.com/r/thiagosg/otel-crud-api-quarkus)

A production-ready Quarkus REST API with comprehensive OpenTelemetry instrumentation, featuring distributed tracing, metrics collection, and structured logging. Built with clean architecture principles and designed for cloud-native deployments.

## 📋 Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Deployment Options](#deployment-options)
- [API Documentation](#api-documentation)
- [Configuration](#configuration)
- [Observability](#observability)
- [Development](#development)
- [Testing](#testing)
- [Contributing](#contributing)

## ✨ Features

- **🚀 Quarkus Framework** - Supersonic Subatomic Java with fast startup times
- **📊 Full Observability** - Distributed tracing, metrics, and structured logging
- **🔌 OpenTelemetry Native** - Built-in OTLP exporter support for traces, metrics, and logs
- **🏗️ Clean Architecture** - Repository pattern with Panache for simplified data access
- **🐳 Docker Ready** - Multi-stage Dockerfile with security best practices
- **🔒 Security First** - Non-root user, minimal attack surface, vulnerability scanning
- **🧪 Well Tested** - Comprehensive test coverage with JUnit 5 and REST-assured
- **📝 API Documentation** - OpenAPI/Swagger UI automatically generated
- **💾 MySQL Integration** - JDBC with full OpenTelemetry instrumentation
- **⚡ Fast Startup** - Sub-second startup time in JVM mode

## 📚 Prerequisites

- Java 21+ (for local development)
- Maven 3.9+
- Docker & Docker Compose
- MySQL 8.0+ (or use the provided docker-compose)
- OpenTelemetry Collector (optional - included in full setup)

## 🚀 Quick Start

### Option 1: Full Stack (App + Database + Observability)

```bash
# Clone the repository
git clone https://github.com/devops-thiago/otel-example-quarkus.git
cd otel-example-quarkus

# Start everything with docker-compose
docker-compose up -d

# Check if services are running
docker-compose ps
```

**Access points:**
- API: http://localhost:8080
- API Docs (Swagger): http://localhost:8080/q/swagger-ui
- Health: http://localhost:8080/q/health
- Metrics: http://localhost:8080/q/metrics
- Grafana: http://localhost:3000 (admin/admin)
- Alloy UI: http://localhost:12345

### Option 2: Run Locally

```bash
# Install dependencies and run in dev mode
./mvnw quarkus:dev

# Or build and run
./mvnw clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## 🚢 Deployment Options

### Using Your Own OpenTelemetry Collector

If you already have an OpenTelemetry infrastructure:

```bash
# Use the app-only compose file
docker-compose -f docker-compose.app-only.yml up -d
```

**Required environment variables:**
```bash
# OpenTelemetry Configuration
OTEL_EXPORTER_OTLP_ENDPOINT=your-collector:4320
OTEL_SERVICE_NAME=otel-quarkus-crud

# Database Configuration
DB_HOST=your-mysql-host
DB_PORT=3306
DB_USER=your-db-user
DB_PASSWORD=your-db-password
DB_NAME=your-db-name
```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: otel-quarkus-api
spec:
  replicas: 3
  selector:
    matchLabels:
      app: otel-quarkus-api
  template:
    metadata:
      labels:
        app: otel-quarkus-api
    spec:
      containers:
      - name: api
        image: otel-example-quarkus:latest
        ports:
        - containerPort: 8080
        env:
        - name: QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT
          value: "http://otel-collector:4320"
        - name: DB_HOST
          value: "mysql-service"
        livenessProbe:
          httpGet:
            path: /q/health/live
            port: 8080
          initialDelaySeconds: 30
        readinessProbe:
          httpGet:
            path: /q/health/ready
            port: 8080
          initialDelaySeconds: 10
```

### Building Docker Image

```bash
# Build the image locally
docker build -t otel-example-quarkus:latest .

# Build with Maven
./mvnw clean package -Dquarkus.container-image.build=true

# Build multi-platform image
docker buildx build \
  --platform linux/amd64,linux/arm64 \
  -t otel-example-quarkus:latest .
```

## 📖 API Documentation

### Health Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/q/health` | Overall health check |
| GET | `/q/health/live` | Liveness probe |
| GET | `/q/health/ready` | Readiness probe |
| GET | `/q/metrics` | Prometheus-compatible metrics |

### User API

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/users` | List all users | - |
| GET | `/api/users/{id}` | Get user by ID | - |
| GET | `/api/users/email/{email}` | Get user by email | - |
| GET | `/api/users/search?name={name}` | Search users by name | - |
| GET | `/api/users/recent?days={days}` | Get recent users | - |
| GET | `/api/users/count` | Get user count | - |
| POST | `/api/users` | Create new user | `{"name": "John", "email": "john@example.com", "bio": "Developer"}` |
| PUT | `/api/users/{id}` | Update user | `{"name": "John Updated"}` |
| DELETE | `/api/users/{id}` | Delete user | - |

### Example Requests

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com", "bio": "Software Engineer"}'

# Get all users
curl http://localhost:8080/api/users

# Get user by ID
curl http://localhost:8080/api/users/1

# Search users
curl http://localhost:8080/api/users/search?name=John

# Get recent users (last 7 days)
curl http://localhost:8080/api/users/recent?days=7

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "John Updated", "bio": "Senior Engineer"}'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1
```

## ⚙️ Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| **OpenTelemetry** | | |
| `QUARKUS_OTEL_ENABLED` | Enable OpenTelemetry | `true` |
| `QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT` | OTLP collector endpoint | `http://localhost:4320` |
| `QUARKUS_OTEL_LOGS_ENABLED` | Enable OTLP log export | `true` |
| `QUARKUS_OTEL_TRACES_ENABLED` | Enable distributed tracing | `true` |
| `QUARKUS_OTEL_METRICS_ENABLED` | Enable metrics collection | `true` |
| **Database** | | |
| `DB_HOST` | MySQL host | `localhost` |
| `DB_PORT` | MySQL port | `3306` |
| `DB_USER` | MySQL user | `user` |
| `DB_PASSWORD` | MySQL password | `password` |
| `DB_NAME` | MySQL database name | `userdb` |
| **Server** | | |
| `QUARKUS_HTTP_PORT` | API server port | `8080` |
| `QUARKUS_HTTP_HOST` | API server host | `0.0.0.0` |

## 🔭 Observability

This project includes a complete observability stack using the LGTM (Loki, Grafana, Tempo, Mimir) stack:

### Distributed Tracing (Tempo)
- Trace all HTTP requests and database queries
- Correlate logs with traces using trace IDs
- View spans in Grafana with trace context

### Metrics Collection (Mimir)
- HTTP server metrics (request duration, status codes, etc.)
- JVM metrics (memory, threads, GC)
- Database connection pool metrics
- Custom business metrics

### Log Aggregation (Loki)
- Structured JSON logs
- Automatic trace correlation
- Log levels and filtering
- Full-text search capabilities

### Visualization (Grafana)
Pre-configured dashboards for:
- Application overview
- HTTP request metrics
- JVM performance
- Trace exploration

**Access Grafana**: http://localhost:3000 (admin/admin)

## 🏗️ Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/br/com/arquivolivre/otelquarkus/
│   │   │   ├── model/          # JPA entities
│   │   │   ├── repository/     # Data access layer
│   │   │   ├── resource/       # REST endpoints
│   │   │   └── service/        # Business logic
│   │   └── resources/
│   │       ├── application.properties  # Configuration
│   │       └── import.sql      # Initial data
│   └── test/
│       └── java/               # Unit and integration tests
├── config/                     # Observability stack configs
│   ├── alloy.alloy            # Grafana Alloy configuration
│   ├── tempo.yaml             # Tempo tracing backend
│   ├── mimir.yaml             # Mimir metrics backend
│   ├── loki.yaml              # Loki logging backend
│   └── grafana/               # Grafana provisioning
├── docker-compose.yml         # Full stack deployment
├── Dockerfile                 # Multi-stage Docker build
├── pom.xml                    # Maven dependencies
└── README.md                  # This file
```

## 🛠️ Development

### Running in Dev Mode

Quarkus provides a development mode with hot-reload:

```bash
./mvnw quarkus:dev
```

This enables:
- Live reload of code changes
- Dev UI at http://localhost:8080/q/dev
- Debugging on port 5005

### Code Quality

```bash
# Format code
./mvnw spotless:apply

# Check code style
./mvnw spotless:check

# Run static analysis
./mvnw verify
```

### Database Migrations

```bash
# The app automatically creates/updates schema on startup
# Initial data is loaded from src/main/resources/import.sql
```

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run with coverage
./mvnw verify

# Run specific test class
./mvnw test -Dtest=UserResourceTest

# Run integration tests only
./mvnw verify -Dskip.unit.tests=true
```

### Test Coverage

- **Unit Tests**: Repository, Service, and Resource layers
- **Integration Tests**: Full API endpoint testing
- **Coverage Target**: >80% line coverage, >75% branch coverage

View coverage report: `target/site/jacoco/index.html`

## 🐳 Docker

### Build and Run

```bash
# Build the Docker image
docker build -t otel-quarkus-api .

# Run the container
docker run -d \
  -p 8080:8080 \
  -e DB_HOST=mysql \
  -e QUARKUS_OTEL_EXPORTER_OTLP_ENDPOINT=http://alloy:4320 \
  otel-quarkus-api
```

### Docker Compose

```bash
# Start full stack
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Rebuild and restart
docker-compose up -d --build app
```

## 📊 Monitoring

### Metrics Endpoints

- Prometheus metrics: http://localhost:8080/q/metrics
- Health check: http://localhost:8080/q/health
- OpenAPI spec: http://localhost:8080/q/openapi

### Grafana Dashboards

1. **Application Overview**: Real-time metrics and request rates
2. **JVM Metrics**: Memory, GC, and thread monitoring
3. **Trace Analysis**: Distributed tracing visualization

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Code Standards

- Follow Java code conventions
- Add tests for new features
- Update documentation as needed
- Run `./mvnw spotless:apply` before committing

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
