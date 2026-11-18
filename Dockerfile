FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace
COPY payment-platform/ payment-platform/
RUN mvn -B -f payment-platform/pom.xml -DskipTests package

FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build --chown=10001:10001 /workspace/payment-platform/payment-api/target/payment-api-1.0.0-SNAPSHOT.jar app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
