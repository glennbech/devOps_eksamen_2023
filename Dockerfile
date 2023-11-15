# Multistage docker file.

# Stage 1: Build the application with Maven
  # Sets working directory within itself to /app
  # Copy pom.xml from host machine.
  # Copy src from host machine and compile the project.
FROM maven:3.8-amazoncorretto-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package

# Stage 2: Run the application with OpenJDK
 # copy the jar from builder and runs the application.
FROM openjdk:17-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar /app/application.jar
ENTRYPOINT ["java","-jar","/app/application.jar"]