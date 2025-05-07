# Etapa 1: Construcción con Gradle
FROM eclipse-temurin:17-jdk AS temp_build_image
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# Copiar archivos necesarios para Gradle
COPY build.gradle ./
COPY settings.gradle ./
COPY gradlew ./
COPY gradle/ ./gradle/

# Descargar Gradle Wrapper
RUN chmod +x ./gradlew && \
    ./gradlew --version

# Copiar código fuente y construir el JAR
COPY src/ ./src/
RUN ./gradlew build --no-daemon

# Etapa 2: Imagen final
FROM eclipse-temurin:17-jdk-jammy
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME

# Copiar JAR desde la etapa de construcción
COPY --from=temp_build_image $APP_HOME/build/libs/*.jar app.jar

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]