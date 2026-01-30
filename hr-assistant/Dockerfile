# Usiamo 'alpine', una versione ultra-leggera di Linux
FROM eclipse-temurin:21-jdk-alpine

# Spring Boot usa /tmp per memorizzare i dati della sessione o file temporanei di Tomcat.
VOLUME /tmp

# Prende il file .jar compilato da Maven e lo rinomina internamente in 'app.jar'.
COPY target/*.jar app.jar

# Copiamo il file del certificato (keystore) dentro il container.
COPY src/main/resources/keystore.p12 keystore.p12

# Avvia la JVM.
ENTRYPOINT ["java","-jar","/app.jar"]
