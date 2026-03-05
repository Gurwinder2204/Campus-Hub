# ============================================================
# Campus Study Hub - Production Dockerfile
# Multi-stage build for optimized image size
# ============================================================

# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached unless pom.xml changes)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application (skip tests for faster build)
RUN ./mvnw clean package -DskipTests -B

# ============================================================
# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create uploads and backups directory
RUN mkdir -p uploads backups && chown -R spring:spring /app

# Copy backup script
COPY scripts/backup-db.sh /app/backup-db.sh
RUN chmod +x /app/backup-db.sh

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8080

# Default environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
