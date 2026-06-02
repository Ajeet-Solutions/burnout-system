# Step 1: Build the application using Maven
FROM maven:3.8.8-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the application using OpenJDK
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/burnout-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]