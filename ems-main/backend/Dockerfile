# ------------ BUILD STAGE ------------
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

# Ensure Spring Boot creates an executable (fat) JAR
RUN mvn -DskipTests clean package spring-boot:repackage

# ------------ RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]
