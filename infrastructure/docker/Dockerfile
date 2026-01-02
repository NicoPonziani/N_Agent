# ============================================
# N_Agent - Multi-stage Dockerfile
# ============================================
# Build: docker build -t n-agent:beta .
# Run: docker run -p 8080:8080 --env-file .env n-agent:beta
# ============================================

# ============================================
# STAGE 1: Build
# ============================================
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build

# Copy pom.xml first for better layer caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build application (skip tests for faster build)
RUN mvn clean package -DskipTests -B

# ============================================
# STAGE 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

# Install curl for healthchecks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/target/*.jar app.jar

# Create directories for keys and logs
RUN mkdir -p /app/keys /app/logs && \
    chown -R appuser:appgroup /app

# Copy private key (will be overridden by volume mount in production)
# This is just for local testing
COPY --chown=appuser:appgroup code-analisys-agent.2025-12-17.private-key.pem /app/keys/private-key.pem

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/code-agent/test/health || exit 1

# JVM optimization for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+DisableExplicitGC \
               -Djava.security.egd=file:/dev/./urandom"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

