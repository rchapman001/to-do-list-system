# Step 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Step 2: Create runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/list-service-0.0.1-SNAPSHOT.jar list-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "list-service.jar"]