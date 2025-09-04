FROM openjdk:17

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/auth-0.0.1-SNAPSHOT.jar app.jar

# Expose the port
EXPOSE 8070

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]