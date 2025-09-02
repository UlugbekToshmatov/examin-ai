FROM openjdk:17

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/*.jar app.jar

# Expose the port
EXPOSE 8050

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]