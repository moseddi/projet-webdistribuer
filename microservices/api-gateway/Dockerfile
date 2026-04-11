FROM amazoncorretto:17
WORKDIR /app
COPY target/api-gateway-1.0.0.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]