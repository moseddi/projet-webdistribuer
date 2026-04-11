FROM amazoncorretto:17
WORKDIR /app
COPY target/stock-service-1.0.0.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]