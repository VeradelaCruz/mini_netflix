# Usar la imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Archivo jar generado
ARG JAR_FILE=target/rating-service-0.0.1.jar

# Copiar el archivo jar generado por Maven/Gradle
COPY ${JAR_FILE} app_rating-service.jar

# Puerto en que corre la aplicación (ajustar según configuración)
EXPOSE 8082

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app_rating-service.jar"]