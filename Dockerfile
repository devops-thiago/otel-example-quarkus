# Multi-stage Dockerfile for Quarkus Application
# Stage 1: Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven configuration files first (for better layer caching)
COPY pom.xml .

# Download dependencies (cached layer if pom.xml hasn't changed)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (creates quarkus-app directory)
RUN mvn clean package -DskipTests -B

# Verify the build artifacts
RUN ls -la target/ && \
    ls -la target/quarkus-app/

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install curl for health checks and create non-root user
RUN apk add --no-cache curl && \
    addgroup -g 1000 appuser && \
    adduser -u 1000 -G appuser -s /bin/sh -D appuser

# Set working directory
WORKDIR /app

# Copy the Quarkus application from build stage
COPY --from=build --chown=appuser:appuser /app/target/quarkus-app/lib/ ./lib/
COPY --from=build --chown=appuser:appuser /app/target/quarkus-app/*.jar ./
COPY --from=build --chown=appuser:appuser /app/target/quarkus-app/app/ ./app/
COPY --from=build --chown=appuser:appuser /app/target/quarkus-app/quarkus/ ./quarkus/

# Create directories for logs
RUN mkdir -p /app/logs && \
    chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose the application port
EXPOSE 8080

# Environment variables for Quarkus
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="quarkus-run.jar"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/q/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "quarkus-run.jar"]