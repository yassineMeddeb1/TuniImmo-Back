# Utilise l'image officielle Maven avec JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Utilise une image JRE légère pour l'exécution
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9091
CMD ["java", "-jar", "app.jar"]
