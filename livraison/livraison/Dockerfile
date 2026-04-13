# ─────────────────────────────────────────
# ÉTAPE 1 : Build avec Maven
# ─────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copier le pom.xml en premier pour profiter du cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et builder
COPY src ./src
RUN mvn clean package -DskipTests

# ─────────────────────────────────────────
# ÉTAPE 2 : Image finale légère
# ─────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier uniquement le .jar depuis l'étape de build
COPY --from=build /app/target/*.jar livraison-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "livraison-service.jar"]
