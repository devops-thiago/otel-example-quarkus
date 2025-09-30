# Quick Start Guide - OpenTelemetry Quarkus CRUD API

## 🚀 5-Minute Setup

### 1. Start in Development Mode
```bash
cd otel-quarkus-crud
./mvnw quarkus:dev
```
Access: http://localhost:8080/q/swagger-ui

### 2. Run Tests with Coverage
```bash
./mvnw clean test jacoco:report
```
View coverage: `target/site/jacoco/index.html`

### 3. Start Full Observability Stack
```bash
docker compose up -d
```

## 📊 Key URLs

| Service | URL | Credentials |
|---------|-----|-------------|
| Application API | http://localhost:8080/api/users | - |
| Swagger UI | http://localhost:8080/q/swagger-ui | - |
| Dev UI | http://localhost:8080/q/dev | - |
| Health Check | http://localhost:8080/q/health | - |
| Metrics | http://localhost:8080/q/metrics | - |
| Grafana | http://localhost:3000 | admin/admin |
| Alloy | http://localhost:12345 | - |
| MinIO Console | http://localhost:9001 | admin/password123 |

## 🧪 Quick API Tests

```bash
# Create user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Test User", "email": "test@example.com", "bio": "Quick test"}'

# List all users
curl http://localhost:8080/api/users

# Search users
curl "http://localhost:8080/api/users/search?name=Test"

# Get user count
curl http://localhost:8080/api/users/count
```

## 📁 Project Structure

```
otel-quarkus-crud/
├── src/main/java/.../
│   ├── model/User.java              # Entity (Panache)
│   ├── repository/UserRepository.java   # Data layer
│   ├── service/UserService.java     # Business logic + tracing
│   └── resource/UserResource.java   # REST endpoints
├── src/test/java/                   # 80%+ coverage tests
├── config/                          # Observability configs
├── docker-compose.yml               # Full stack
└── pom.xml                          # Dependencies
```

## 🎯 Key Features

✅ **Quarkus 3.17.5** - Fast startup (~1s), low memory (~50MB)
✅ **OpenTelemetry** - Auto + manual instrumentation
✅ **Hibernate Panache** - Simplified ORM
✅ **80%+ Test Coverage** - JUnit 5 + REST Assured
✅ **Full Observability** - Alloy, Tempo, Mimir, Loki, Grafana
✅ **PostgreSQL + H2** - Production & dev databases
✅ **CI/CD Ready** - GitHub Actions workflow
✅ **Docker Ready** - Multi-stage optimized builds

## 🔧 Development Commands

```bash
# Dev mode with live reload
./mvnw quarkus:dev

# Run tests
./mvnw test

# Check coverage
./mvnw jacoco:check

# Build package
./mvnw clean package

# Build Docker image
docker build -t otel-quarkus-crud .

# Start observability stack
docker compose up -d

# View logs
docker compose logs -f otel-quarkus-crud

# Stop all services
docker compose down
```

## 📊 Test Coverage

Run tests with coverage:
```bash
./mvnw clean test jacoco:report
```

Coverage thresholds:
- **Line Coverage**: ≥ 80%
- **Branch Coverage**: ≥ 75%

## 🎨 Technologies

- **Framework**: Quarkus 3.17.5
- **Language**: Java 21
- **Database**: PostgreSQL 17 / H2
- **ORM**: Hibernate Panache
- **REST**: RESTEasy Reactive (JAX-RS)
- **Tracing**: OpenTelemetry
- **Testing**: JUnit 5, REST Assured, Mockito
- **Coverage**: JaCoCo
- **Containers**: Docker, Docker Compose
- **CI/CD**: GitHub Actions

## 🐛 Common Issues

**Port in use:**
```bash
# Change port in application.properties
quarkus.http.port=8081
```

**OpenTelemetry not working:**
```bash
# Verify Alloy is running
curl http://localhost:4320
```

**Database issues:**
```bash
# Check PostgreSQL
docker compose ps postgres
```

## 📚 Documentation

- Full README: `README.md`
- API Docs: http://localhost:8080/q/swagger-ui
- Quarkus Guides: https://quarkus.io/guides/

## 🎯 Next Steps

1. **Explore the API**: Open Swagger UI
2. **View Traces**: Grafana → Explore → Tempo
3. **Check Metrics**: Grafana → Explore → Mimir
4. **View Logs**: Grafana → Explore → Loki
5. **Run Tests**: `./mvnw test`
6. **Add Features**: Extend UserService with custom spans

---

**Happy Coding! 🚀**