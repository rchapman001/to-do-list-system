# Step 1: Build the application using Maven
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Debugging step to check the contents of /app/target
RUN ls /app/target

RUN ls /app/src

# Step 2: Create the actual Docker image using the packaged JAR
FROM openjdk:17
WORKDIR /app
# COPY --from=build /app/target/list-service.jar /app/list-service.jar
COPY --from=build /app/target/list-service-0.0.1-SNAPSHOT.jar /app/list-service.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/list-service.jar"]
