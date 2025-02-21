# Usar una imagen de OpenJDK 17
FROM openjdk:21-jdk-slim

# Establecer el directorio de trabajo en /app
WORKDIR /app

# Copiar el archivo JAR de la aplicación al contenedor
COPY target/inditex-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
