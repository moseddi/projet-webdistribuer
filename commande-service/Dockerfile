FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
EXPOSE 8087
ADD target/commande-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
