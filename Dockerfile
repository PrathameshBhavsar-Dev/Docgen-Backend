# =========================
# Stage 1: Build the app
# =========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper + project files first (better caching)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Give execute permission to mvnw
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests


# =========================
# Stage 2: Run the app
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]