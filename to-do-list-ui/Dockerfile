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
# COPY --from=build /app/target/to-do-list-ui.jar /app/to-do-list-ui.jar
COPY --from=build /app/target/to-do-list-ui-0.0.1-SNAPSHOT.jar /app/to-do-list-ui.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/to-do-list-ui.jar"]
