
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/acme-tarifas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 7070

ENTRYPOINT ["java", "-jar", "app.jar"]