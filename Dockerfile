# Multi-stage build for Spring Boot 3 (Java 21)
# 1) Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
# Cache dependencies first
COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline
# Copy source and build
COPY src ./src
RUN mvn -B -q -DskipTests package

# 2) Runtime stage
FROM eclipse-temurin:21-jre
# Security best-practices
ENV JAVA_OPTS=""
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE}
WORKDIR /app
# Copy fat jar from build stage
COPY --from=build /app/target/coderover-0.0.1-SNAPSHOT.jar app.jar
# Expose default Spring Boot port (Render maps it automatically)
EXPOSE 8080
# Health check (optional). Using wget which is available in Debian-based images
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health >/dev/null 2>&1 || exit 1
# Run the application; allow extra JVM args via JAVA_OPTS
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
