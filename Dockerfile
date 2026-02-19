FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

# Copy only pom.xml first for better layer caching
COPY pom.xml ./

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Use Eclipse Temurin for the runtime
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE ${PORT}

CMD ["java", "-jar", "app.jar"]