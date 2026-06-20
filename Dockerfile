FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /target/burnout-system-0.0.1-SNAPSHOT.jar burnout-system.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "burnout-system.jar"]