
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .


ENV MAVEN_OPTS="-Xmx300m -XX:+UseSerialGC"


RUN mvn clean package -DskipTests --batch-mode -Dhttp.keepAlive=false

# Run using official Eclipse Temurin JRE image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/burnout-system-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]