FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
# Copia il jar dalla cartella target (che è relativa a dove si trova questo Dockerfile)
COPY target/*.jar app.jar
# Copia il keystore per l'HTTPS
COPY src/main/resources/keystore.p12 keystore.p12
ENTRYPOINT ["java","-jar","/app.jar"]
