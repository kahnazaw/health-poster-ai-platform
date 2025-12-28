# Multi-stage Dockerfile for Railway deployment
# Dockerfile متعدد المراحل لنشر Railway

# ============================================================================
# Multi-stage Dockerfile for Railway deployment
# ============================================================================
# Dockerfile متعدد المراحل لنشر Railway
# VERIFIED: Java 20 configuration for optimal performance
#
# Purpose: Build and deploy the Health Poster AI Platform
# الغرض: بناء ونشر منصة بوسترات التوعية الصحية
#
# Build Process:
# 1. Stage 1: Build the application using Maven
# 2. Stage 2: Create lightweight runtime image
# ============================================================================

# ============================================================================
# Stage 1: Build
# ============================================================================
FROM maven:3.9-eclipse-temurin-20 AS build
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer for faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# ============================================================================
# Stage 2: Runtime (Lightweight)
# ============================================================================
FROM eclipse-temurin:20-jre-alpine
WORKDIR /app

# Create non-root user for security
# إنشاء مستخدم غير root للأمان
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from build stage
COPY --from=build /app/target/health-poster-ai-platform-1.0.0.jar app.jar

# Expose port (Railway will set PORT environment variable)
EXPOSE 8080

# Health check to ensure application is running
# فحص الصحة للتأكد من أن التطبيق يعمل
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/actuator/health || exit 1

# Run application
# تشغيل التطبيق
ENTRYPOINT ["java", "-jar", "app.jar"]

