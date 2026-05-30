# Step 1: Build the application using Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

# Step 2: Create the runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/to-do-list-ui-0.0.1-SNAPSHOT.jar to-do-list-ui.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "to-do-list-ui.jar"]