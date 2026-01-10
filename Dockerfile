# Stage 1: Build the application
# Using an image that provides Java 25 and Maven
FROM infotechsoft/maven:3.9.11-jdk-25 AS build

# Set the working directory
WORKDIR /app

# Copy pom.xml and source code to the working directory
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:25-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/target/*.jar ./app.jar

# Add metadata to the image
LABEL maintainer="tdessalegn"
LABEL authors="tdessalegn"

# Expose application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
