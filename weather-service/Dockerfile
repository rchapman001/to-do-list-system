# Step 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Step 2: Create the runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/weather-service-0.0.1-SNAPSHOT.jar weather-service.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "weather-service.jar"]