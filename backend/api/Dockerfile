# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build

# Copiar archivos de Maven
COPY pom.xml .
COPY src ./src

# Build con Maven
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR del builder
COPY --from=builder /build/target/*.jar app.jar

# Variables de entorno por defecto
ENV PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
