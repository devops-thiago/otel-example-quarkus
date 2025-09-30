# OpenTelemetry Quarkus CRUD API

[![CI/CD Pipeline](https://github.com/devops-thiago/otel-quarkus-crud/actions/workflows/ci.yml/badge.svg)](https://github.com/devops-thiago/otel-quarkus-crud/actions/workflows/ci.yml)
[![codecov](https://codecov.io/gh/devops-thiago/otel-quarkus-crud/branch/main/graph/badge.svg)](https://codecov.io/gh/devops-thiago/otel-quarkus-crud)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Quarkus](https://img.shields.io/badge/Quarkus-3.17.5-blue.svg)](https://quarkus.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://hub.docker.com/)
[![OpenTelemetry](https://img.shields.io/badge/OpenTelemetry-Enabled-blueviolet.svg)](https://opentelemetry.io/)

A comprehensive Quarkus REST API demonstrating complete CRUD operations with full OpenTelemetry integration and enterprise-grade observability stack for distributed tracing, metrics, and log aggregation.

## 🚀 Features

- **Complete CRUD Operations**: Full Create, Read, Update, Delete functionality for User entities
- **Quarkus Framework**: Supersonic Subatomic Java with fast startup and low memory footprint
- **OpenTelemetry Integration**: Automatic and manual instrumentation for distributed tracing
- **Hibernate Panache**: Simplified data access layer with active record pattern
- **Full Observability Stack**: Grafana Alloy, Tempo, Mimir, Loki, and Grafana
- **RESTEasy Reactive**: Non-blocking REST endpoints with JAX-RS annotations
- **80%+ Code Coverage**: Comprehensive unit and integration tests
- **PostgreSQL & H2**: Production-ready database with H2 for development
- **Bean Validation**: Input validation using Jakarta Bean Validation
- **Health Checks**: SmallRye Health for monitoring
- **Prometheus Metrics**: Micrometer with Prometheus registry
- **OpenAPI/Swagger**: Interactive API documentation
- **Docker Support**: Multi-stage builds optimized for production
- **CI/CD Pipeline**: GitHub Actions with automated testing and deployment

## 📋 Table of Contents

- [Technologies Used](#technologies-used)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Observability](#observability)
- [Docker Deployment](#docker-deployment)
- [CI/CD](#cicd)
- [Contributing](#contributing)

## 🛠 Technologies Used

### Core Framework
- **Java 21** - Latest LTS version with modern language features
- **Quarkus 3.17.5** - Supersonic Subatomic Java Framework
- **Maven** - Dependency management and build tool

### Data Layer
- **Hibernate ORM with Panache** - Simplified persistence layer
- **PostgreSQL 17** - Production database
- **H2 Database** - In-memory database for development and testing

### Observability
- **OpenTelemetry** - Distributed tracing and metrics
- **Grafana Alloy** - Telemetry data collector
- **Grafana Tempo** - Distributed tracing backend
- **Grafana Mimir** - Prometheus-compatible metrics storage
- **Grafana Loki** - Log aggregation system
- **Grafana** - Visualization and dashboarding
- **MinIO** - S3-compatible object storage

### REST & Validation
- **RESTEasy Reactive** - Reactive REST endpoints
- **Jakarta REST (JAX-RS)** - Standard REST annotations
- **Hibernate Validator** - Bean validation
- **Jackson** - JSON processing

### Testing
- **JUnit 5** - Testing framework
- **REST Assured** - REST API testing
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **JaCoCo** - Code coverage analysis

### DevOps
- **Docker & Docker Compose** - Containerization
- **GitHub Actions** - CI/CD pipeline
- **Codecov** - Coverage reporting

## 🏗 Architecture Overview

### Application Architecture
```
┌──────────────┐
│   Client     │
└──────┬───────┘
       │ HTTP/REST
┌──────▼───────────────┐
│   UserResource       │  (JAX-RS REST Endpoints)
│   (REST Layer)       │
└──────┬───────────────┘
       │
┌──────▼───────────────┐
│   UserService        │  (Business Logic + OpenTelemetry)
│   (Service Layer)    │
└──────┬───────────────┘
       │
┌──────▼───────────────┐
│  UserRepository      │  (Panache Repository)
│  (Data Layer)        │
└──────┬───────────────┘
       │
┌──────▼───────────────┐
│  PostgreSQL/H2       │  (Database)
└──────────────────────┘
```

### Observability Stack
```
┌────────────────┐
│ Quarkus App    │
└───────┬────────┘
        │ OTLP (gRPC/HTTP)
        ▼
┌───────────────────┐
│  Grafana Alloy    │  (Collector)
└───┬───────┬───┬───┘
    │       │   │
    ▼       ▼   ▼
┌──────┐ ┌──────┐ ┌──────┐
│Tempo │ │Mimir │ │Loki  │  (Storage Backends)
└───┬──┘ └───┬──┘ └───┬──┘
    │        │        │
    └────────┴────────┴─────► MinIO (S3 Storage)
                      │
                      ▼
              ┌───────────────┐
              │    Grafana    │  (Visualization)
              └───────────────┘
```

## 📁 Project Structure

```
otel-quarkus-crud/
├── src/
│   ├── main/
│   │   ├── java/br/com/arquivolivre/otelquarkus/
│   │   │   ├── model/
│   │   │   │   └── User.java                    # Entity with Panache
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java         # Data access layer
│   │   │   ├── service/
│   │   │   │   └── UserService.java            # Business logic + tracing
│   │   │   └── resource/
│   │   │       └── UserResource.java           # REST endpoints
│   │   └── resources/
│   │       ├── application.properties          # Configuration
│   │       └── import.sql                      # Sample data
│   └── test/
│       └── java/                               # Comprehensive tests
├── config/
│   ├── alloy.alloy                            # Alloy configuration
│   ├── tempo.yaml                             # Tempo configuration
│   ├── mimir.yaml                             # Mimir configuration
│   ├── loki.yaml                              # Loki configuration
│   └── grafana/                               # Grafana provisioning
├── .github/workflows/
│   └── ci.yml                                 # CI/CD pipeline
├── docker-compose.yml                         # Full stack deployment
├── Dockerfile                                 # Multi-stage build
└── pom.xml                                    # Maven configuration
```

## 🚦 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker and Docker Compose** (for full observability stack)
- **PostgreSQL 17** (optional, H2 used by default in dev mode)

### Option 1: Quick Start (Development Mode)

Quarkus Dev Mode provides live reload and H2 console:

```bash
# Clone the repository
git clone https://github.com/devops-thiago/otel-quarkus-crud.git
cd otel-quarkus-crud

# Run in development mode
./mvnw quarkus:dev
```

Access points:
- **API**: http://localhost:8080/api/users
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **Health**: http://localhost:8080/q/health
- **Metrics**: http://localhost:8080/q/metrics
- **Dev UI**: http://localhost:8080/q/dev

### Option 2: Package and Run

```bash
# Build the application
./mvnw clean package

# Run the JAR
java -jar target/quarkus-app/quarkus-run.jar
```

### Option 3: Full Observability Stack

```bash
# Start the complete stack
docker compose up -d

# Check services status
docker compose ps
```

Access points:
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/q/swagger-ui
- **Grafana**: http://localhost:3000 (admin/admin)
- **Alloy UI**: http://localhost:12345
- **MinIO Console**: http://localhost:9001 (admin/password123)

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api/users
```

### Endpoints

#### 1. Get All Users
```http
GET /api/users
```

#### 2. Get User by ID
```http
GET /api/users/{id}
```

#### 3. Get User by Email
```http
GET /api/users/email/{email}
```

#### 4. Create User
```http
POST /api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "bio": "Software Engineer"
}
```

#### 5. Update User
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john.updated@example.com",
  "bio": "Senior Software Engineer"
}
```

#### 6. Delete User
```http
DELETE /api/users/{id}
```

#### 7. Search Users by Name
```http
GET /api/users/search?name={query}
```

#### 8. Get Recent Users
```http
GET /api/users/recent?days={days}
```

#### 9. Get User Count
```http
GET /api/users/count
```

#### 10. Health Check
```http
GET /api/users/health
```

### Example Requests

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Smith", "email": "jane@example.com", "bio": "DevOps Engineer"}'

# Get all users
curl http://localhost:8080/api/users

# Search users
curl "http://localhost:8080/api/users/search?name=Jane"

# Update user
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Jane Updated", "email": "jane.updated@example.com", "bio": "Senior DevOps"}'

# Delete user
curl -X DELETE http://localhost:8080/api/users/1
```

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Run Tests with Coverage
```bash
./mvnw clean test jacoco:report
```

### View Coverage Report
```bash
open target/site/jacoco/index.html
```

### Run Integration Tests Only
```bash
./mvnw verify
```

### Test Structure

- **Repository Tests**: `UserRepositoryTest` - Data layer tests
- **Service Tests**: `UserServiceTest` - Business logic tests with mocking
- **Resource Tests**: `UserResourceTest` - REST API integration tests

### Coverage Goals

- **Line Coverage**: ≥ 80%
- **Branch Coverage**: ≥ 75%

The build will warn if coverage falls below these thresholds.

## 📊 Observability

### OpenTelemetry Configuration

The application automatically instruments:
- HTTP requests and responses
- Database queries
- Custom business logic spans

### Viewing Traces in Grafana

1. Open Grafana: http://localhost:3000
2. Navigate to Explore
3. Select **Tempo** datasource
4. Query: `{service.name="otel-quarkus-crud"}`

### Custom Spans

The service layer includes custom instrumentation:

```java
@WithSpan("UserService.createUser")
public User createUser(@SpanAttribute("user.email") User user) {
    Span span = Span.current();
    span.setAttribute("user.name", user.name);
    // Business logic
}
```

### Metrics

View Prometheus metrics:
- **Endpoint**: http://localhost:8080/q/metrics
- **Grafana Query**: `rate(http_requests_total[5m])`

### Logs

Logs are collected by Loki and can be queried in Grafana:
```
{job="otel-quarkus-crud"} |= "error"
```

### Key Metrics to Monitor

1. **Request Rate**: `rate(http_server_requests_seconds_count[5m])`
2. **Error Rate**: `rate(http_server_requests_seconds_count{status=~"5.."}[5m])`
3. **Response Time (p95)**: `histogram_quantile(0.95, http_server_requests_seconds_bucket)`
4. **JVM Memory**: `jvm_memory_used_bytes`

## 🐳 Docker Deployment

### Build Docker Image
```bash
docker build -t otel-quarkus-crud .
```

### Run Container
```bash
docker run -p 8080:8080 otel-quarkus-crud
```

### Docker Compose Services

| Service | Port | Description |
|---------|------|-------------|
| **otel-quarkus-crud** | 8080 | Main application |
| **postgres** | 5432 | PostgreSQL database |
| **grafana** | 3000 | Visualization (admin/admin) |
| **alloy** | 12345, 4320, 4321 | Telemetry collector |
| **tempo** | 3200, 4317, 4318 | Trace storage |
| **mimir** | 9009 | Metrics storage |
| **loki** | 3100 | Log aggregation |
| **minio** | 9000, 9001 | Object storage |

### Docker Commands

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f otel-quarkus-crud

# Stop services
docker compose down

# Stop and remove volumes (⚠️ Data loss)
docker compose down -v

# Rebuild and restart
docker compose up -d --build
```

## 🔄 CI/CD

### GitHub Actions Workflow

The CI/CD pipeline includes:

1. **Test Job**: Runs unit tests and generates coverage reports
2. **Build Job**: Packages the application
3. **Docker Job**: Builds and pushes Docker image (main branch only)
4. **Integration Test Job**: Tests the full stack

### Required GitHub Secrets

Configure these secrets in your repository:

```
Settings → Secrets and variables → Actions
```

- `CODECOV_TOKEN`: Codecov upload token
- `DOCKER_USERNAME`: Docker Hub username
- `DOCKER_PASSWORD`: Docker Hub password/token

### Workflow Triggers

- Push to `main` or `develop` branches
- Pull requests to `main` branch

### Status Badges

Add these to your README:
```markdown
[![CI/CD](https://github.com/YOUR_USERNAME/otel-quarkus-crud/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/otel-quarkus-crud/actions)
[![codecov](https://codecov.io/gh/YOUR_USERNAME/otel-quarkus-crud/branch/main/graph/badge.svg)](https://codecov.io/gh/YOUR_USERNAME/otel-quarkus-crud)
```

## 🔧 Configuration

### Profiles

Quarkus supports multiple profiles:

- **dev**: Development mode (H2, debug logging)
- **test**: Test mode (H2, minimal logging)
- **prod**: Production mode (PostgreSQL, JSON logging)

### Environment Variables

Override configuration using environment variables:

```bash
export QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/mydb
export QUARKUS_DATASOURCE_USERNAME=user
export QUARKUS_DATASOURCE_PASSWORD=pass
export QUARKUS_OTEL_EXPORTER_OTLP_TRACES_ENDPOINT=http://alloy:4320
```

### Key Configuration Properties

```properties
# Database
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/quarkus_db

# OpenTelemetry
quarkus.otel.enabled=true
quarkus.otel.exporter.otlp.traces.endpoint=http://localhost:4317

# Server
quarkus.http.port=8080
quarkus.http.host=0.0.0.0
```

## 📈 Performance

### Quarkus Benefits

- **Fast Startup**: ~1 second startup time
- **Low Memory**: ~50MB RSS memory
- **High Throughput**: Reactive architecture
- **Native Compilation**: Optional GraalVM native image support

### Build Native Image

```bash
./mvnw package -Dnative
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

### Development Guidelines

- Write tests for new features (maintain 80%+ coverage)
- Follow existing code style
- Update documentation
- Add OpenTelemetry spans for new operations

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🔗 Resources

- [Quarkus Documentation](https://quarkus.io/guides/)
- [OpenTelemetry Java](https://opentelemetry.io/docs/languages/java/)
- [Grafana Alloy](https://grafana.com/docs/alloy/)
- [Grafana Tempo](https://grafana.com/docs/tempo/)
- [Grafana Mimir](https://grafana.com/docs/mimir/)
- [Grafana Loki](https://grafana.com/docs/loki/)
- [Hibernate Panache](https://quarkus.io/guides/hibernate-orm-panache)

## 💡 Tips & Tricks

### Quarkus Dev Mode

- Press `w` to open browser
- Press `e` to edit configuration
- Press `h` for help
- Press `s` to force restart

### Quick Database Reset

```bash
# In dev mode, just restart and DB is recreated
./mvnw quarkus:dev
```

### Generate Coverage Report Quickly

```bash
./mvnw test jacoco:report && open target/site/jacoco/index.html
```

## 🐛 Troubleshooting

### Port Already in Use

```bash
# Change port in application.properties
quarkus.http.port=8081
```

### OpenTelemetry Not Working

```bash
# Check Alloy is running
curl http://localhost:4320

# Enable debug logging
quarkus.log.category."io.opentelemetry".level=DEBUG
```

### Database Connection Issues

```bash
# Verify PostgreSQL is running
docker compose ps postgres

# Check connection
psql -h localhost -U postgres -d quarkus_db
```

---

**Built with ❤️ using Quarkus and OpenTelemetry**

For questions or issues, please open an issue in the GitHub repository.