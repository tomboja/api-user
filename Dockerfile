# Stage 1: Build the application
FROM eclipse-temurin:25-jdk AS build

RUN apt-get update \
  && apt-get install -y maven \
  && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy pom.xml and source code to the working directory
COPY pom.xml .
COPY src ./src
# Build the application
RUN mvn -B clean package -DskipTests

# Stage 2: Create the final image
FROM eclipse-temurin:25-jre

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
