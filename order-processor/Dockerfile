FROM eclipse-temurin:21-jdk-jammy

# Crear directorio app
WORKDIR /app

# Copiar el jar generado por Spring Boot
COPY target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Comando para correr la app
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
