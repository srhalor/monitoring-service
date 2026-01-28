FROM eclipse-temurin:21-jre-alpine 
 
WORKDIR /app 
 
# Copy the pre-built JAR file 
COPY target/monitoring-service-0.0.1-SNAPSHOT.jar /app/monitoring-service.jar 
 
# Expose port 
EXPOSE 8080 
 
# Start the application 
CMD ["java", "-jar", "/app/monitoring-service.jar"]
