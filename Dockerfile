FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml ./
COPY .mvn/ .mvn
RUN chmod +x mvnw && ./mvnw dependency:resolve

COPY src ./src
RUN ./mvnw clean package -DskipTests

# Use the official Eclipse Temurin image (successor to OpenJDK)
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE ${PORT}

CMD ["java", "-jar", "app.jar"]